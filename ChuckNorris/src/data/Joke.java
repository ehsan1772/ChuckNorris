package data;

import com.google.gson.annotations.SerializedName;

/**
 *  Simple class with GSON annotations to parse the feed
 * @author ehsan.barekati
 */
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
