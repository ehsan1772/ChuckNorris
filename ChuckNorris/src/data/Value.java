package data;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import android.text.Html;

import com.google.gson.annotations.Expose;
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
		return Html.escapeHtml(joke);
	}
	
	public List<String> getCategories() {
		return categories;
	}

}
