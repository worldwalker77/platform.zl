package cn.worldwalker.game.wyqp.common.domain.base;

import java.util.Date;
import java.util.List;

public class UserRecordModel {
	
	private Long id;
	
	private Integer gameType;
	
	private Integer playerId;
	
	private Integer roomId;
	
	private Integer payType;
	
	private Integer totalGames;
	
	private Integer score;
	
	private String recordInfo;
	
	private String remark; 
	
	private Date createTime;
	
	private List<RecordModel> recordList;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getGameType() {
		return gameType;
	}

	public void setGameType(Integer gameType) {
		this.gameType = gameType;
	}

	public Integer getPlayerId() {
		return playerId;
	}

	public void setPlayerId(Integer playerId) {
		this.playerId = playerId;
	}

	public Integer getRoomId() {
		return roomId;
	}

	public void setRoomId(Integer roomId) {
		this.roomId = roomId;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}


	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public Integer getTotalGames() {
		return totalGames;
	}

	public void setTotalGames(Integer totalGames) {
		this.totalGames = totalGames;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRecordInfo() {
		return recordInfo;
	}

	public void setRecordInfo(String recordInfo) {
		this.recordInfo = recordInfo;
	}

	public List<RecordModel> getRecordList() {
		return recordList;
	}

	public void setRecordList(List<RecordModel> recordList) {
		this.recordList = recordList;
	}


}
