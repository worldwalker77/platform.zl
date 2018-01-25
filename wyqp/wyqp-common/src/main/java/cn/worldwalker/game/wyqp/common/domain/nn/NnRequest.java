package cn.worldwalker.game.wyqp.common.domain.nn;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import cn.worldwalker.game.wyqp.common.domain.base.BaseRequest;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class NnRequest extends BaseRequest{
	
	private NnMsg msg = new NnMsg();

	public NnMsg getMsg() {
		return msg;
	}

	public void setMsg(NnMsg msg) {
		this.msg = msg;
	}
	
	
}
