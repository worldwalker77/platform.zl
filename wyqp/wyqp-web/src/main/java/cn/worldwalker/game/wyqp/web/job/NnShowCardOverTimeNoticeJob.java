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
import cn.worldwalker.game.wyqp.common.domain.nn.NnPlayerInfo;
import cn.worldwalker.game.wyqp.common.domain.nn.NnRoomInfo;
import cn.worldwalker.game.wyqp.common.service.RedisOperationService;
import cn.worldwalker.game.wyqp.nn.enums.NnPlayerStatusEnum;
import cn.worldwalker.game.wyqp.nn.enums.NnRoomStatusEnum;
import cn.worldwalker.game.wyqp.nn.service.NnGameService;

/**
 * 超过10s没有明牌，则自动明牌
 * @author lenovo
 *
 */
@Component(value="nnShowCardOverTimeNoticeJob")
public class NnShowCardOverTimeNoticeJob {
	
	private final static Log log = LogFactory.getLog(NnShowCardOverTimeNoticeJob.class);
	
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
		Map<String, String> map = redisOperationService.getAllNnShowCardIpRoomIdTime();
		Set<Entry<String, String>> set = map.entrySet();
		for(Entry<String, String> entry : set){
			try {
				Integer roomId = Integer.valueOf(entry.getKey());
				Long time = Long.valueOf(entry.getValue());
			
				NnRoomInfo nnRoomInfo = redisOperationService.getRoomInfoByRoomId(roomId, NnRoomInfo.class);
				if (nnRoomInfo == null) {
					redisOperationService.delNnShowCardIpRoomIdTime(roomId);
					continue;
				}
				/**如果状态不为在游戏中，则说明小局结束或者一圈结束,将此房间抢庄标记从redis中删掉*/
				if (!nnRoomInfo.getStatus().equals(NnRoomStatusEnum.inGame.status)) {
					redisOperationService.delNnShowCardIpRoomIdTime(roomId);
					continue;
				}
				if (System.currentTimeMillis() - time < 10000) {
					continue;
				}
				List<NnPlayerInfo> playerList = nnRoomInfo.getPlayerList();
				for(NnPlayerInfo player : playerList){
					if (player.getStatus() < NnPlayerStatusEnum.showCard.status) {
						UserInfo userInfo = new UserInfo();
						userInfo.setPlayerId(player.getPlayerId());
						userInfo.setRoomId(roomId);
						nnGameService.showCard(null, null, userInfo);
					}
				}
				redisOperationService.delNnShowCardIpRoomIdTime(roomId);
			} catch (Exception e) {
				log.error("roomId:" + entry.getKey(), e);
			}
		}
		
	}
}
