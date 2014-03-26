package data;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;

import com.google.gson.annotations.SerializedName;

public class Value {

	@SerializedName("id")
	private int id;
	
	@SerializedName("joke")
	private String joke;
	
	@SerializedName("categories")
	private List<String> categories = new ArrayList<String>(4);
	

	public int getId() {
		return id;
	}

	public String getJoke() {
		return StringEscapeUtils.unescapeHtml3(joke);
	}
	
	public List<String> getCategories() {
		return categories;
	}

}
