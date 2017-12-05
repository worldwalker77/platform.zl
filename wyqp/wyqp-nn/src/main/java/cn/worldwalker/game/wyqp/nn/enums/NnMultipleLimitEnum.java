package cn.worldwalker.game.wyqp.nn.enums;

public enum NnMultipleLimitEnum {
	
	notReady(1,3, "3倍封顶"),
	ready(2,5, "5倍封顶"),
	notRob(3,100, "不封顶");
	
	public Integer index;
	public Integer multiple;
	public String desc;
	
	private NnMultipleLimitEnum(Integer index, Integer multiple, String desc){
		this.index = index;
		this.multiple = multiple;
		this.desc = desc;
	}
	
	public static NnMultipleLimitEnum getNnMultipleLimitEnum(Integer index){
		for(NnMultipleLimitEnum nnMultipleLimitEnum : NnMultipleLimitEnum.values()){
			if (nnMultipleLimitEnum.index.equals(index)) {
				return nnMultipleLimitEnum;
			}
		}
		return null;
	}
}
