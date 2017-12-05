package cn.worldwalker.game.wyqp.server.dispatcher;

import io.netty.channel.ChannelHandlerContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.worldwalker.game.wyqp.common.domain.base.BaseMsg;
import cn.worldwalker.game.wyqp.common.domain.base.BaseRequest;
import cn.worldwalker.game.wyqp.common.domain.base.UserInfo;
import cn.worldwalker.game.wyqp.common.enums.GameTypeEnum;
import cn.worldwalker.game.wyqp.common.enums.MsgTypeEnum;
import cn.worldwalker.game.wyqp.common.result.Result;
import cn.worldwalker.game.wyqp.server.service.CommonGameService;

@Service(value="commonMsgDispatcher")
public class CommonMsgDisPatcher extends BaseMsgDisPatcher{
	
	@Autowired
	private CommonGameService commonGameService;
	
	@Override
	public void requestDispatcher(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo) throws Exception {
		BaseMsg msg = request.getMsg();
		Integer msgType = request.getMsgType();
		MsgTypeEnum msgTypeEnum= MsgTypeEnum.getMsgTypeEnumByType(msgType);
		switch (msgTypeEnum) {
			case entryHall:
				commonGameService.entryHall(ctx, request, userInfo);
				break;
			case syncPlayerLocation:
				commonGameService.syncPlayerLocation(ctx, request, userInfo);
				break;
			case entryRoom:
				commonGameService.commonEntryRoom(ctx, request, userInfo);
				break;
			case userFeedback:
				commonGameService.userFeedback(ctx, request, userInfo);
				break;
			case heartBeat:
				channelContainer.sendTextMsgByPlayerIds(new Result(GameTypeEnum.nn.gameType, MsgTypeEnum.heartBeat.msgType), userInfo.getPlayerId());
				break;
			case refreshRoom:
				channelContainer.addChannel(ctx, userInfo.getPlayerId());
				commonGameService.commonRefreshRoom(ctx, request, userInfo);
				break;
			case productList:
				commonGameService.productList(ctx, request, userInfo);
				break;
			case bindProxy:
				commonGameService.bindProxy(ctx, request, userInfo);
				break;
			case checkBindProxy:
				commonGameService.checkBindProxy(ctx, request, userInfo);
				break;
			case unifiedOrder:
				commonGameService.unifiedOrder(ctx, request, userInfo);
			case notice:
				commonGameService.notice(ctx, request, userInfo);
				break;
			default:
				break;
			}
		
	
	}

}
