package data;

import com.google.gson.annotations.SerializedName;

public class Joke {
	@SerializedName("type")
	private String type;
	@SerializedName("value")
	private Value value;
	
	public Value getValue() {
		return value;
	}

	public String getType() {
		return type;
	}

}
