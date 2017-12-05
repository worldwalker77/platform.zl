package cn.worldwalker.game.wyqp.web.job;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.worldwalker.game.wyqp.common.channel.ChannelContainer;
import cn.worldwalker.game.wyqp.common.constant.Constant;
import cn.worldwalker.game.wyqp.common.domain.nn.NnRoomInfo;
import cn.worldwalker.game.wyqp.common.enums.GameTypeEnum;
import cn.worldwalker.game.wyqp.common.enums.MsgTypeEnum;
import cn.worldwalker.game.wyqp.common.result.Result;
import cn.worldwalker.game.wyqp.common.service.RedisOperationService;
import cn.worldwalker.game.wyqp.common.utils.GameUtil;
import cn.worldwalker.game.wyqp.common.utils.IPUtil;
import cn.worldwalker.game.wyqp.nn.enums.NnRoomStatusEnum;

@Component(value="nnRobBankerOverTimeNoticeJob")
public class NnRobBankerOverTimeNoticeJob {
	
	private final static Log log = LogFactory.getLog(NnRobBankerOverTimeNoticeJob.class);
	
	@Autowired
	public RedisOperationService redisOperationService;
	@Autowired
	private ChannelContainer channelContainer;
	
	public void doTask(){
		String ip = Constant.localIp;
		if (StringUtils.isBlank(ip)) {
			return;
		}
		Map<String, String> map = redisOperationService.getAllNnRobIpRoomIdTime();
		Set<Entry<String, String>> set = map.entrySet();
		for(Entry<String, String> entry : set){
			try {
				Integer roomId = Integer.valueOf(entry.getKey());
				Long time = Long.valueOf(entry.getValue());
			
				NnRoomInfo nnRoomInfo = redisOperationService.getRoomInfoByRoomId(roomId, NnRoomInfo.class);
				if (nnRoomInfo == null) {
					redisOperationService.delNnRobIpRoomIdTime(roomId);
					continue;
				}
				/**如果有庄家，则说明大家已经抢庄了,将此房间抢庄标记从redis中删掉*/
				if (nnRoomInfo.getRoomBankerId() != null) {
					redisOperationService.delNnRobIpRoomIdTime(roomId);
					continue;
				}
				if (System.currentTimeMillis() - time < 10000) {
					continue;
				}
				if (nnRoomInfo.getCurGame() == 1) {
					nnRoomInfo.setRoomBankerId(nnRoomInfo.getRoomOwnerId());
				}else{
					nnRoomInfo.setRoomBankerId(nnRoomInfo.getCurWinnerId());
				}
				nnRoomInfo.setStatus(NnRoomStatusEnum.inStakeScore.status);
				redisOperationService.setRoomIdRoomInfo(roomId, nnRoomInfo);
				Result result = new Result();
				result.setGameType(GameTypeEnum.nn.gameType);
				Map<String, Object> data = new HashMap<String, Object>();
				result.setData(data);
				result.setMsgType(MsgTypeEnum.readyStake.msgType);
				data.put("roomBankerId", nnRoomInfo.getRoomBankerId());
				channelContainer.sendTextMsgByPlayerIds(result, GameUtil.getPlayerIdArr(nnRoomInfo.getPlayerList()));
			} catch (Exception e) {
				log.error("roomId:" + entry.getKey(), e);
			}
		}
		
	}
}
