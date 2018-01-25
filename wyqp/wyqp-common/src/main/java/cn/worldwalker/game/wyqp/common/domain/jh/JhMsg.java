package cn.worldwalker.game.wyqp.common.domain.jh;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import cn.worldwalker.game.wyqp.common.domain.base.BaseMsg;
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class JhMsg extends BaseMsg{
	
	private Integer stakeLimit;
	
	private Integer stakeTimesLimit;
	
	private Integer curStakeScore;//当前押注分数
	
	public Integer getStakeLimit() {
		return stakeLimit;
	}
	public void setStakeLimit(Integer stakeLimit) {
		this.stakeLimit = stakeLimit;
	}
	public Integer getStakeTimesLimit() {
		return stakeTimesLimit;
	}
	public void setStakeTimesLimit(Integer stakeTimesLimit) {
		this.stakeTimesLimit = stakeTimesLimit;
	}
	public Integer getCurStakeScore() {
		return curStakeScore;
	}
	public void setCurStakeScore(Integer curStakeScore) {
		this.curStakeScore = curStakeScore;
	}
	
}
