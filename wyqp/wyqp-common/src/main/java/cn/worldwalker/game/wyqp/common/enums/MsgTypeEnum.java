package cn.worldwalker.game.wyqp.common.enums;

public enum MsgTypeEnum {
	
	/**公共消息类型 1-99*/
	entryHall(1, "进入大厅"),
	userRecord(2, "玩家战绩"),
	userFeedback(3, "用户反馈"),
	notice(4, "公告通知"),
	createRoom(5, "创建房间"),
	entryRoom(6, "进入房间"),
	ready(7, "点击准备"),
	dealCards(8, "发牌消息"),
	dissolveRoom(9, "解散房间"),
	agreeDissolveRoom(10, "同意解散房间"),
	disagreeDissolveRoom(11, "不同意解散房间"),
	successDissolveRoom(12, "成功解散房间通知"),
	refreshRoom(13, "刷新房间信息"),
	offlineNotice(14, "通知玩家离线"),
	onlineNotice(15, "通知玩家上线"),
	delRoomConfirmBeforeReturnHall(16, "客户端返回大厅时的通知消息"),
	queryPlayerInfo(17, "查看玩家信息"),
	dissolveRoomCausedByOffline(18, "玩家离线超过20分钟，解散房间"),
	chatMsg(19, "聊天消息"),
	heartBeat(20, "心跳检测"),
	roomCardNumUpdate(21, "房卡数更新"),
	sendEmoticon(22, "给某个玩家发送特殊的表情符号"),
	syncPlayerLocation(23, "同步玩家地理位置信息"),
	curSettlement(24, "小结算"),
	totalSettlement(25, "大结算"),
	productList(26, "房卡商品列表"),
	bindProxy(27, "绑定代理"),
	checkBindProxy(28, "校验是否绑定代理"),
	unifiedOrder(29, "微信预支付前统一下单接口"),
//	notReadyTimer(30, "未准备玩家计时器"),
	
	/**斗牛特有100-199*/
	readyRobBanker(100, "准备抢庄"),
	robBanker(101, "抢庄"),
	robBankerOverTime(102, "抢庄超过10s时间限制"),
	readyStake(103, "准备压分"),
	stakeScore(104, "压分"),
	showCard(105, "亮牌"),
	
	/**麻将特有200-299*/
	playDice(200, "掷色子"),
	moPai(201, "返回摸牌"),
	chuPai(202, "出牌"),
	noticeCanChiPai(203, "通知可以吃牌或胡牌"),//告诉客户端吃、碰还是杠或胡,也可能是都可以
	chiPai(204, "吃牌"),//需要告诉服务端吃、碰还是杠；同时服务端也需要校验
	pengPai(205, "碰牌"),
	gangPai(206, "杠牌"),
	huPai(207, "胡牌"),//服务端校验
	pass(208, "过"),
	
	/**金花特有300-399*/
	stake(300, "压分"),
	autoCardsCompare(301, "到达跟注数量上限的自动比牌"),
	manualCardsCompare(302, "局中发起比牌"),
	watchCards(303, "看牌"),
	discardCards(304, "弃牌"),
	notReadyTimer(305, "未准备玩家计时器");
	
	public int msgType;
	public String desc;
	
	private MsgTypeEnum(int msgType, String desc){
		this.msgType = msgType;
		this.desc = desc;
	}
	
	public static MsgTypeEnum getMsgTypeEnumByType(int msgType){
		for(MsgTypeEnum msgTypeEnum : MsgTypeEnum.values()){
			if (msgType == msgTypeEnum.msgType) {
				return msgTypeEnum;
			}
		}
		return null;
	}
	
}
