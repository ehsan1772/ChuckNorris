package data;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Value {

	@SerializedName("id")
	int id;
	
	@SerializedName("joke")
	String joke;
	
	@SerializedName("categories")
	List<String> categories = new ArrayList<String>(4);
	
//	
//	public int getId() {
//		return id;
//	}
//	public void setId(int id) {
//		this.id = id;
//	}
//	public String getJoke() {
//		return joke;
//	}
//	public void setJoke(String joke) {
//		this.joke = joke;
//	}
	

}
