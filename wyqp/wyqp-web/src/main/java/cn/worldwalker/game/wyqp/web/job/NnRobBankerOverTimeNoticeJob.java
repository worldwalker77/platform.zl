package cn.worldwalker.game.wyqp.web.job;

import java.util.List;
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
import cn.worldwalker.game.wyqp.common.domain.base.UserInfo;
import cn.worldwalker.game.wyqp.common.domain.nn.NnMsg;
import cn.worldwalker.game.wyqp.common.domain.nn.NnPlayerInfo;
import cn.worldwalker.game.wyqp.common.domain.nn.NnRequest;
import cn.worldwalker.game.wyqp.common.domain.nn.NnRoomInfo;
import cn.worldwalker.game.wyqp.common.enums.GameTypeEnum;
import cn.worldwalker.game.wyqp.common.enums.MsgTypeEnum;
import cn.worldwalker.game.wyqp.common.service.RedisOperationService;
import cn.worldwalker.game.wyqp.common.utils.JsonUtil;
import cn.worldwalker.game.wyqp.nn.enums.NnPlayerStatusEnum;
import cn.worldwalker.game.wyqp.nn.service.NnGameService;
/**
 * 牛牛
 * @author lenovo
 *
 */
@Component(value="nnRobBankerOverTimeNoticeJob")
public class NnRobBankerOverTimeNoticeJob {
	
	private final static Log log = LogFactory.getLog(NnRobBankerOverTimeNoticeJob.class);
	
	@Autowired
	public RedisOperationService redisOperationService;
	@Autowired
	private ChannelContainer channelContainer;
	@Autowired
	private NnGameService nnGameService;
	
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
				if (System.currentTimeMillis() - time < 5000) {
					continue;
				}
				List<NnPlayerInfo> playerList = nnRoomInfo.getPlayerList();
				NnRequest request = new NnRequest();
				request.setGameType(GameTypeEnum.nn.gameType);
				request.setMsgType(MsgTypeEnum.robBanker.msgType);
				NnMsg msg = new NnMsg();
				request.setMsg(msg);
				UserInfo userInfo = new UserInfo();
				userInfo.setRoomId(roomId);
				/**模拟抢庄流程，robBankerId抢庄，其他玩家不抢*/
				for(NnPlayerInfo player : playerList){
					/**状态为经准备的玩家才自动抢庄*/
					if (player.getStatus().equals(NnPlayerStatusEnum.ready.status)) {
						userInfo.setPlayerId(player.getPlayerId());
						userInfo.setRoomId(roomId);
						msg.setIsRobBanker(NnPlayerStatusEnum.rob.status);
						msg.setRobMultiple(1);
						msg.setRoomId(roomId);
						msg.setPlayerId(player.getPlayerId());
						log.info(player.getPlayerId()+ player.getNickName() + "==========自动抢庄===============" + JsonUtil.toJson(request));
						nnGameService.robBanker(null, request, userInfo);
					}
				}
			} catch (Exception e) {
				log.error("roomId:" + entry.getKey(), e);
			}
		}
		
	}
}
