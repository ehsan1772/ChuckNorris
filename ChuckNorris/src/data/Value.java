package data;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringEscapeUtils;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * Simple class with GSON annotations to parse the feed
 * @author ehsan.barekati
 */
public class Value {

	@SerializedName("id")
	private int id;

	@SerializedName("joke")
	private String joke;

	@SerializedName("categories")
	private List<String> categories = new ArrayList<String>();

	public int getId() {
		return id;
	}

	public String getJoke() {
		//to parse the string correctly we unescape HTML tags
		return StringEscapeUtils.unescapeHtml3(joke);
	}

	public List<String> getCategories() {
		return categories;
	}

}
