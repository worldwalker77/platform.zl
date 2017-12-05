package cn.worldwalker.game.wyqp.web.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import cn.worldwalker.game.wyqp.common.domain.base.Card;
import cn.worldwalker.game.wyqp.common.domain.base.RedisRelaModel;
import cn.worldwalker.game.wyqp.common.domain.jh.JhPlayerInfo;
import cn.worldwalker.game.wyqp.common.domain.jh.JhRoomInfo;
import cn.worldwalker.game.wyqp.common.domain.nn.NnPlayerInfo;
import cn.worldwalker.game.wyqp.common.domain.nn.NnRoomInfo;
import cn.worldwalker.game.wyqp.common.enums.DissolveStatusEnum;
import cn.worldwalker.game.wyqp.common.enums.GameTypeEnum;
import cn.worldwalker.game.wyqp.common.enums.MsgTypeEnum;
import cn.worldwalker.game.wyqp.common.result.Result;
import cn.worldwalker.game.wyqp.common.service.RedisOperationService;
import cn.worldwalker.game.wyqp.common.utils.GameUtil;
import cn.worldwalker.game.wyqp.jh.cards.JhCardResource;
import cn.worldwalker.game.wyqp.jh.cards.JhCardRule;
import cn.worldwalker.game.wyqp.jh.enums.JhPlayerStatusEnum;
import cn.worldwalker.game.wyqp.jh.enums.JhRoomStatusEnum;
import cn.worldwalker.game.wyqp.jh.service.JhGameService;
import cn.worldwalker.game.wyqp.nn.cards.NnCardResource;
import cn.worldwalker.game.wyqp.nn.cards.NnCardRule;
import cn.worldwalker.game.wyqp.nn.enums.NnPlayerStatusEnum;
import cn.worldwalker.game.wyqp.nn.enums.NnRoomBankerTypeEnum;
import cn.worldwalker.game.wyqp.nn.enums.NnRoomStatusEnum;
import cn.worldwalker.game.wyqp.nn.service.NnGameService;

@Component(value="readyOverTimeNoticeJob")
public class ReadyOverTimeNoticeJob {
	
	private final static Log log = LogFactory.getLog(ReadyOverTimeNoticeJob.class);
	
	@Autowired
	public RedisOperationService redisOperationService;
	@Autowired
	private ChannelContainer channelContainer;
	@Autowired
	private JhGameService jhGameService;
	@Autowired
	private NnGameService nnGameService;
	
	public void doTask(){
		String ip = Constant.localIp;
		if (StringUtils.isBlank(ip)) {
			return;
		}
		Map<String, String> map = redisOperationService.getAllNotReadyIpRoomIdTime();
		Set<Entry<String, String>> set = map.entrySet();
		for(Entry<String, String> entry : set){
			try {
				Integer roomId = Integer.valueOf(entry.getKey());
				Long time = Long.valueOf(entry.getValue());
				RedisRelaModel rrm = redisOperationService.getGameTypeUpdateTimeByRoomId(roomId);
				if (rrm == null) {
					redisOperationService.delNotReadyIpRoomIdTime(roomId);
					continue;
				}
				Integer gameType = rrm.getGameType();
				if (GameTypeEnum.jh.gameType.equals(gameType)) {
					processJinhua(roomId, time);
				}else if(GameTypeEnum.nn.gameType.equals(gameType)){
					processNn(roomId, time);
				}else if(GameTypeEnum.mj.gameType.equals(gameType)){
					
				}else{
					
				}
				
				
			} catch (Exception e) {
				log.error("roomId:" + entry.getKey(), e);
			}
		}
		
	}
	
	private void processJinhua(Integer roomId, Long time){
		JhRoomInfo roomInfo = redisOperationService.getRoomInfoByRoomId(roomId, JhRoomInfo.class);
		if (roomInfo == null) {
			redisOperationService.delNotReadyIpRoomIdTime(roomId);
			return;
		}
		/**如果房间已经在游戏中了,将此房间抢庄标记从redis中删掉*/
		if (roomInfo.getStatus().equals(JhRoomStatusEnum.inGame.status)) {
			redisOperationService.delNotReadyIpRoomIdTime(roomId);
			return;
		}
		if (System.currentTimeMillis() - time < 10000) {
			return;
		}
		/**将没有准备的人设置为观察者，并且发牌给其他的人，同时通过刷新房间接口返回个所有玩家*/
		List<JhPlayerInfo> playerList = roomInfo.getPlayerList();
		for(JhPlayerInfo player : playerList){
			/**玩家的状态为没有准备，则设置状态为观察者*/
			if (!player.getStatus().equals(JhPlayerStatusEnum.ready.status)) {
				player.setStatus(JhPlayerStatusEnum.observer.status);
				player.setCardList(null);
			}else if(player.getStatus().equals(JhPlayerStatusEnum.ready.status)){/**已经准备*/
				/**发牌给当前玩家*/
				List<List<Card>> playerCards = JhCardResource.dealCards(1);
				List<Card> cardList = playerCards.get(0);
				/**为玩家设置牌及牌型*/
				player.setCardList(cardList);
				player.setCardType(JhCardRule.calculateCardType(cardList));
				player.setStatus(JhPlayerStatusEnum.notWatch.status);
				player.setStakeTimes(0);
				player.setCurTotalStakeScore(0);
				player.setCurScore(0);
				player.setCurStakeScore(0);
			}
			/**设置每个玩家的解散房间状态为不同意解散，后面大结算返回大厅的时候回根据此状态判断是否解散房间*/
			player.setDissolveStatus(DissolveStatusEnum.disagree.status);
		}
		/**开始发牌时将房间内当前局数+1*/
		roomInfo.setCurGame(roomInfo.getCurGame() + 1);
		/**概率控制*/
		JhCardRule.probabilityProcess(roomInfo);
		/**根据庄家id获取最开始出牌玩家的id,注意有可能庄家没有准备成为观察者，所以就要以此往下推知道找出第一个准备的玩家*/
		Integer nextOperatePlayerId = GameUtil.getNextOperatePlayerIdByRoomBankerId(playerList, roomInfo.getRoomBankerId());
		roomInfo.setCurPlayerId(nextOperatePlayerId);
		roomInfo.setStatus(JhRoomStatusEnum.inGame.status);
		roomInfo.setUpdateTime(new Date());
		redisOperationService.setRoomIdRoomInfo(roomId, roomInfo);
		/**删除未准备10秒计时器*/
		redisOperationService.delNotReadyIpRoomIdTime(roomId);
		
		/**复用刷新接口，向所有玩家返回**/
		jhGameService.refreshRoomForAllPlayer(roomInfo);
	}
	
	private void processNn(Integer roomId, Long time){
		NnRoomInfo roomInfo = redisOperationService.getRoomInfoByRoomId(roomId, NnRoomInfo.class);
		if (roomInfo == null) {
			redisOperationService.delNotReadyIpRoomIdTime(roomId);
			return;
		}
		/**如果是抢庄阶段或者压分阶段（对应抢庄类型和非抢庄类型）*/
		if (roomInfo.getStatus().equals(NnRoomStatusEnum.inRob.status) 
			|| roomInfo.getStatus().equals(NnRoomStatusEnum.inStakeScore.status)) {
			redisOperationService.delNotReadyIpRoomIdTime(roomId);
			return;
		}
		if (System.currentTimeMillis() - time < 10000) {
			return;
		}
		
		/**将没有准备的人设置为观察者，并且发牌给其他的人，同时通过刷新房间接口返回个所有玩家*/
		List<NnPlayerInfo> playerList = roomInfo.getPlayerList();
		int size = playerList.size();
		/**发牌*/
		List<List<Card>> playerCards = NnCardResource.dealCardsWithOutRank(size);
		/**为每个玩家设置牌及牌型*/
		for(int i = 0; i < size; i++ ){
			NnPlayerInfo player = playerList.get(i);
			if (!player.getStatus().equals(NnPlayerStatusEnum.ready.status)) {
				player.setStatus(NnPlayerStatusEnum.observer.status);
				player.setCardType(null);
				player.setCardList(null);
				player.setNnCardList(null);
				player.setRobFourCardList(null);
				player.setFifthCard(null);
			}else{
				List<Card> cardList = playerCards.get(i);
				List<Card> nnCardList = new ArrayList<Card>();
				List<Card> robFourCardList = new ArrayList<Card>();
				Card fifthCard = new Card();
				player.setCardType(NnCardRule.calculateCardType(cardList, nnCardList, robFourCardList, fifthCard));
				player.setCardList(cardList);
				player.setNnCardList(nnCardList);
				player.setRobFourCardList(robFourCardList);
				player.setFifthCard(fifthCard);
			}
			/**设置每个玩家的解散房间状态为不同意解散，后面大结算返回大厅的时候回根据此状态判断是否解散房间*/
			player.setDissolveStatus(DissolveStatusEnum.disagree.status);
		}
		/**如果是抢庄类型，则设置房间状态为抢庄阶段*/
		if (NnRoomBankerTypeEnum.robBanker.type.equals(roomInfo.getRoomBankerType())) {
			roomInfo.setStatus(NnRoomStatusEnum.inRob.status);
		}else{
			roomInfo.setStatus(NnRoomStatusEnum.inStakeScore.status);
		}
		roomInfo.setUpdateTime(new Date());
		/**开始发牌时将房间内当前局数+1*/
		roomInfo.setCurGame(roomInfo.getCurGame() + 1);
		redisOperationService.setRoomIdRoomInfo(roomId, roomInfo);
		/**删除未准备10秒计时器*/
		redisOperationService.delNotReadyIpRoomIdTime(roomId);
		/**复用刷新接口，向所有玩家返回**/
		jhGameService.refreshRoomForAllPlayer(roomInfo);
		
		
		
		/**刷新完成之后，通知走正常抢庄流程或者压分流程*/
		Result result = new Result();
		result.setGameType(GameTypeEnum.nn.gameType);
		Map<String, Object> data = new HashMap<String, Object>();
		result.setData(data);
		data.put("roomId", roomInfo.getRoomId());
		data.put("roomOwnerId", roomInfo.getRoomOwnerId());
		data.put("totalGames", roomInfo.getTotalGames());
		data.put("curGame", roomInfo.getCurGame());
		/**如果是抢庄类型，则给每个玩家返回四张牌，并通知准备抢庄.同时开启后台定时任务计数*/
		if (NnRoomBankerTypeEnum.robBanker.type.equals(roomInfo.getRoomBankerType())) {
			/**开启后台定时任务计数*/
			redisOperationService.setNnRobIpRoomIdTime(roomId);
			result.setMsgType(MsgTypeEnum.readyRobBanker.msgType);
			for(int i = 0; i < size; i++ ){
				NnPlayerInfo player = playerList.get(i);
				List<Card> cardList = player.getRobFourCardList();
				data.put("cardList", cardList);
				channelContainer.sendTextMsgByPlayerIds(result, player.getPlayerId());
			}
		}else{/**如果是非抢庄类型，则通知玩家谁是庄家并准备压分*/
			result.setMsgType(MsgTypeEnum.readyStake.msgType);
			data.put("roomBankerId", roomInfo.getRoomBankerId());
			channelContainer.sendTextMsgByPlayerIds(result, GameUtil.getPlayerIdArr(playerList));
		}
		
	}
}
