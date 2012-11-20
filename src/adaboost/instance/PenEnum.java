package adaboost.instance;

public enum PenEnum {
	Zero, One, Two, Three, Four,Five, Six, Seven, Eight, Nine;

	public static PenEnum fromString(String s){
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
		case "6":
			return Six;
		case "7":
			return Seven;
		case "8":
			return Eight;
		case "9":
			return Nine;
		default:
			return valueOf(s);
		}
	}
}