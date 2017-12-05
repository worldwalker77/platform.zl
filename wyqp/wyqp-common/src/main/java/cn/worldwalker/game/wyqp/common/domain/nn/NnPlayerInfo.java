package cn.worldwalker.game.wyqp.common.domain.nn;

import java.util.List;

import cn.worldwalker.game.wyqp.common.domain.base.BasePlayerInfo;
import cn.worldwalker.game.wyqp.common.domain.base.Card;

public class NnPlayerInfo extends BasePlayerInfo{
	/**押注分数 1,2,3,4,5*/
	private Integer stakeScore;
	/**玩家抢庄时间*/
	private Long robBankerTime;
	/**有牛的情况下，返回的三张和为10的倍数的三张牌*/
	private List<Card> nnCardList;
	/**抢庄的时候先给的四张牌*/
	private List<Card> robFourCardList;
	/**抢庄的时候第五张牌*/
	private Card fifthCard;
	
	public Integer getStakeScore() {
		return stakeScore;
	}

	public void setStakeScore(Integer stakeScore) {
		this.stakeScore = stakeScore;
	}

	public Long getRobBankerTime() {
		return robBankerTime;
	}

	public void setRobBankerTime(Long robBankerTime) {
		this.robBankerTime = robBankerTime;
	}

	public List<Card> getNnCardList() {
		return nnCardList;
	}

	public void setNnCardList(List<Card> nnCardList) {
		this.nnCardList = nnCardList;
	}

	public List<Card> getRobFourCardList() {
		return robFourCardList;
	}

	public void setRobFourCardList(List<Card> robFourCardList) {
		this.robFourCardList = robFourCardList;
	}

	public Card getFifthCard() {
		return fifthCard;
	}

	public void setFifthCard(Card fifthCard) {
		this.fifthCard = fifthCard;
	}

}
