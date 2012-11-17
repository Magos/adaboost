package adaboost.instance;

public enum GlassEnum {
	One, Two, Three, Four, Five, Six, Seven;

	public static GlassEnum fromString(String s){
		switch(s){
		case "1":
			return One;
		case "2":
			return Two;
		case "3":
			return Three;
		case "4":
			return Four;
		case "5":
			return Five;
		case "6":
			return Six;
		case "7":
			return Seven;
		default:
			return valueOf(s);
		}
	}
}
