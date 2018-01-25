package cn.worldwalker.game.wyqp.common.domain.jh;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import cn.worldwalker.game.wyqp.common.domain.base.BaseRequest;
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class JhRequest extends BaseRequest{
	
	private JhMsg msg = new JhMsg();

	public JhMsg getMsg() {
		return msg;
	}

	public void setMsg(JhMsg msg) {
		this.msg = msg;
	}
	
	
}
