package adaboost.instance;

public enum PageEnum {
	Zero, One, Two, Three, Four,Five;

	public static PageEnum fromString(String s){
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
		case "5":
			return Five;
		default:
			return valueOf(s);
		}
	}
}