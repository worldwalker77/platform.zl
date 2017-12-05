package cn.worldwalker.game.wyqp.common.domain.nn;

import cn.worldwalker.game.wyqp.common.domain.base.BaseMsg;

public class NnMsg extends BaseMsg{
	
	private Integer roomBankerType;
	/**是否抢庄 3不抢 4 抢*/
	private Integer isRobBanker;
	
	private Integer stakeScore;
	
	private Integer multipleLimit;
	
	public Integer getRoomBankerType() {
		return roomBankerType;
	}

	public void setRoomBankerType(Integer roomBankerType) {
		this.roomBankerType = roomBankerType;
	}

	public Integer getIsRobBanker() {
		return isRobBanker;
	}

	public void setIsRobBanker(Integer isRobBanker) {
		this.isRobBanker = isRobBanker;
	}

	public Integer getStakeScore() {
		return stakeScore;
	}

	public void setStakeScore(Integer stakeScore) {
		this.stakeScore = stakeScore;
	}

	public Integer getMultipleLimit() {
		return multipleLimit;
	}

	public void setMultipleLimit(Integer multipleLimit) {
		this.multipleLimit = multipleLimit;
	}
	
}
