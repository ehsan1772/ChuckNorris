package data;

import com.google.gson.annotations.SerializedName;

/**
 * 
 *  Simple class with GSON annotations to parse the feed
 * @author ehsan.barekati
 */
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
