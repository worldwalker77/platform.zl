package cn.worldwalker.game.wyqp.server.service;

import io.netty.channel.ChannelHandlerContext;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.worldwalker.game.wyqp.common.domain.base.BaseRequest;
import cn.worldwalker.game.wyqp.common.domain.base.BaseRoomInfo;
import cn.worldwalker.game.wyqp.common.domain.base.RedisRelaModel;
import cn.worldwalker.game.wyqp.common.domain.base.UserInfo;
import cn.worldwalker.game.wyqp.common.enums.GameTypeEnum;
import cn.worldwalker.game.wyqp.common.enums.MsgTypeEnum;
import cn.worldwalker.game.wyqp.common.result.Result;
import cn.worldwalker.game.wyqp.common.service.BaseGameService;
import cn.worldwalker.game.wyqp.jh.service.JhGameService;
import cn.worldwalker.game.wyqp.mj.service.MjGameService;
import cn.worldwalker.game.wyqp.nn.service.NnGameService;

@Service(value="commonGameService")
public class CommonGameService extends BaseGameService{
	
	@Autowired
	private NnGameService nnGameService;
	
	@Autowired
	private MjGameService mjGameService;
	
	@Autowired
	private JhGameService jhGameService;
	
	public void commonEntryRoom(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo){
		Integer roomId = request.getMsg().getRoomId();
		RedisRelaModel rrm = redisOperationService.getGameTypeUpdateTimeByRoomId(roomId);
		Integer realGameType = rrm.getGameType();
		/**设置真是的gameType*/
		request.setGameType(realGameType);
		GameTypeEnum gameTypeEnum = GameTypeEnum.getGameTypeEnumByType(realGameType);
		switch (gameTypeEnum) {
			case nn:
				nnGameService.entryRoom(ctx, request, userInfo);
				break;
			case mj:
				mjGameService.entryRoom(ctx, request, userInfo);
				break;
			case jh:
				jhGameService.entryRoom(ctx, request, userInfo);
				break;
			default:
				break;
			}
	}
	
	
	public void commonRefreshRoom(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo){
		Integer roomId = userInfo.getRoomId();
		if (roomId == null) {
			channelContainer.sendTextMsgByPlayerIds(new Result(0, MsgTypeEnum.entryHall.msgType), userInfo.getPlayerId());
			return;
		}
		RedisRelaModel rrm = redisOperationService.getGameTypeUpdateTimeByRoomId(roomId);
		/**如果为null，则说明可能是解散房间后，玩家的userInfo里面的roomId没有清空，需要清空掉*/
		if (rrm == null) {
			userInfo.setRoomId(null);
			redisOperationService.setUserInfo(request.getToken(), userInfo);
			channelContainer.sendTextMsgByPlayerIds(new Result(0, MsgTypeEnum.entryHall.msgType), userInfo.getPlayerId());
			return;
		}
		Integer realGameType = rrm.getGameType();
		/**设置真是的gameType*/
		request.setGameType(realGameType);
		GameTypeEnum gameTypeEnum = GameTypeEnum.getGameTypeEnumByType(realGameType);
		switch (gameTypeEnum) {
			case nn:
				nnGameService.refreshRoom(ctx, request, userInfo);
				break;
			case mj:
				nnGameService.refreshRoom(ctx, request, userInfo);
				break;
			case jh:
				jhGameService.refreshRoom(ctx, request, userInfo);
				break;
			default:
				break;
			}
	}
	
	
	@Override
	public BaseRoomInfo doCreateRoom(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo) {
		return null;
	}

	@Override
	public BaseRoomInfo doEntryRoom(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo) {
		return null;
	}

	@Override
	public List<BaseRoomInfo> doRefreshRoom(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo) {
		return null;
	}


	@Override
	public BaseRoomInfo getRoomInfo(ChannelHandlerContext ctx,
			BaseRequest request, UserInfo userInfo) {
		// TODO Auto-generated method stub
		return null;
	}


}
