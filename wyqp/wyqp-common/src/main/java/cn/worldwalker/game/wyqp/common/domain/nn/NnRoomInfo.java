package cn.worldwalker.game.wyqp.common.domain.nn;

import java.util.ArrayList;
import java.util.List;

import cn.worldwalker.game.wyqp.common.domain.base.BaseRoomInfo;

public class NnRoomInfo  extends BaseRoomInfo{
	
	private Integer roomBankerType;
	
	private Integer multipleLimit;
	
	private List<NnPlayerInfo> playerList = new ArrayList<NnPlayerInfo>();
	
	public Integer getRoomBankerType() {
		return roomBankerType;
	}
	public void setRoomBankerType(Integer roomBankerType) {
		this.roomBankerType = roomBankerType;
	}
	public Integer getMultipleLimit() {
		return multipleLimit;
	}
	public void setMultipleLimit(Integer multipleLimit) {
		this.multipleLimit = multipleLimit;
	}
	public List<NnPlayerInfo> getPlayerList() {
		return playerList;
	}
	public void setPlayerList(List<NnPlayerInfo> playerList) {
		this.playerList = playerList;
	}
	
	
}
