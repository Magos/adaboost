package adaboost.instance;

public enum NurseryEnum {
	Zero, One, Two, Three, Four;

	public static NurseryEnum fromString(String s){
		switch(s){
		case "0":
			return Zero;
		case "1":
			return One;
		case "2":
			return Two;
		case "3":
			return Three;
		case "4":
			return Four;
		default:
			return valueOf(s);
		}
	}
}
