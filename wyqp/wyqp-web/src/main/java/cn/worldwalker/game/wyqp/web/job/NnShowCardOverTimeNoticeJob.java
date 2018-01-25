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
import cn.worldwalker.game.wyqp.nn.enums.NnRoomBankerTypeEnum;
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
					/**如果是庄家*/
					if (nnRoomInfo.getRoomBankerId().equals(player.getPlayerId())) {
						if (nnRoomInfo.getRoomBankerType().equals(NnRoomBankerTypeEnum.robBanker.type)) {
							if (player.getStatus() < NnPlayerStatusEnum.rob.status) {
								continue;
							}
						}else{
							if (player.getStatus() < NnPlayerStatusEnum.ready.status) {
								continue;
							}
						}
					}else{/**如果不是庄家*/
						/**玩家状态小于已压分，则说明是观察者*/
						if (player.getStatus() < NnPlayerStatusEnum.stakeScore.status) {
							continue;
						}
					}
					
					if (player.getStatus() < NnPlayerStatusEnum.showCard.status) {
						UserInfo userInfo = new UserInfo();
						userInfo.setPlayerId(player.getPlayerId());
						userInfo.setRoomId(roomId);
						log.info("==============自动亮牌============");
						nnGameService.showCard(null, null, userInfo);
					}
				}
				redisOperationService.delNnShowCardIpRoomIdTime(roomId);
			} catch (Exception e) {
				log.error("roomId:" + entry.getKey(), e);
			}
		}
		
	}
	
	
	public static void main(String[] args) {
		NnRoomInfo roomInfo = new NnRoomInfo();
		roomInfo.setRoomId(147155);
		roomInfo.setRoomBankerId(20006);
		roomInfo.setRoomBankerType(3);
		roomInfo.setCurGame(5);
		roomInfo.setGameType(1);
		roomInfo.setTotalGames(10);
		roomInfo.setPayType(1);
		roomInfo.setMultipleLimit(10000);
		roomInfo.setStatus(3);
		
		NnPlayerInfo player1 = new NnPlayerInfo();
		player1.setPlayerId(20000);
		player1.setNickName("有基没汤");
		player1.setStatus(4);
		NnPlayerInfo player2 = new NnPlayerInfo();
		player2.setPlayerId(20006);
		player2.setNickName("worldwalker");
		player2.setStatus(4);
		NnPlayerInfo player3 = new NnPlayerInfo();
		player3.setPlayerId(20000);
		player3.setNickName("有基没汤");
		player3.setStatus(0);
		roomInfo.getPlayerList().add(player1);
		roomInfo.getPlayerList().add(player2);
		roomInfo.getPlayerList().add(player3);
		
		List<NnPlayerInfo> playerList = roomInfo.getPlayerList();
		for(NnPlayerInfo player : playerList){
			/**如果是庄家*/
			if (roomInfo.getRoomBankerId().equals(player.getPlayerId())) {
				if (roomInfo.getRoomBankerType().equals(NnRoomBankerTypeEnum.robBanker.type)) {
					if (player.getStatus() < NnPlayerStatusEnum.rob.status) {
						continue;
					}
				}else{
					if (player.getStatus() < NnPlayerStatusEnum.ready.status) {
						continue;
					}
				}
			}else{/**如果不是庄家*/
				/**玩家状态小于已压分，则说明是观察者*/
				if (player.getStatus() < NnPlayerStatusEnum.stakeScore.status) {
					continue;
				}
			}
			
			if (player.getStatus() < NnPlayerStatusEnum.showCard.status) {
				UserInfo userInfo = new UserInfo();
				userInfo.setPlayerId(player.getPlayerId());
				log.info("==============自动亮牌============playerId:" + player.getPlayerId());
			}
		}
	}
}
