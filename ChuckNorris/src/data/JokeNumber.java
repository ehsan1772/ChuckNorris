package data;

import com.google.gson.annotations.SerializedName;

public class JokeNumber {
	@SerializedName("type")
	private String type;
	@SerializedName("value")
	private int value;
	
	public String getType() {
		return type;
	}
	public int getValue() {
		return value;
	}

}
