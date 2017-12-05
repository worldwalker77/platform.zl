package cn.worldwalker.game.wyqp.common.dao;

import java.util.List;

import cn.worldwalker.game.wyqp.common.domain.base.UserRecordModel;

public interface UserRecordDao {
	
	public long insertRecord(UserRecordModel model);
	
	public long batchInsertRecord(List<UserRecordModel> modelList);
	
	public List<UserRecordModel> getUserRecord(UserRecordModel model);
	
}
