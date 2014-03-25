package com.ovenbits.chucknorris;

import com.foundation.restful.RestFulDataManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.ovenbits.chucknorris.JokeFragment.JokeActivity;

import data.Joke;

import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity implements JokeActivity{
	private static final String TAG = MainActivity.class.getSimpleName();
	private String url = "http://api.icndb.com/jokes/15";
	private String url2 = "http://api.icndb.com/jokes/16";
	private String url3 = "http://api.icndb.com/jokes/17";
	private String url4 = "http://api.icndb.com/jokes/18";
	private String url5 = "http://api.icndb.com/jokes/19";
	private String url6 = "http://api.icndb.com/jokes/20";
	private String url7 = "http://api.icndb.com/jokes/21";
	private String url8 = "http://api.icndb.com/jokes/22";
	
	private JokeFragment jokeFragment;
	
	String fakeResult= "{\"type\": \"success\"}";
	
	private BroadcastReceiver downloadUpdateReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		jokeFragment = new JokeFragment();
		
		createReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.ovenbits.chucknorris.RESULT");
		LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(downloadUpdateReceiver, filter);
		
		fetchData(url);
		fetchData(url2);
		fetchData(url3);
		fetchData(url4);
		fetchData(url5);
		fetchData(url6);
		fetchData(url7);
		fetchData(url8);
	}
	
	@Override
	protected void onResume() {
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.joke_fragment_container, jokeFragment, JokeFragment.TAG);
		transaction.commit();
		super.onResume();
	}
	

	private void createReceiver() {
		downloadUpdateReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.hasExtra(RestFulDataManager.REF_KEY)) {
					int refKey = intent.getExtras().getInt(RestFulDataManager.REF_KEY);
			//		if (refKey == url.hashCode()) {
					String result = RestFulDataManager.getInstance().getData(refKey, String.class);
					Log.d(TAG, "Result is: " + result);
					
						Gson gson = new GsonBuilder().create();
						try {
						Joke joke = gson.fromJson(result, Joke.class);
						} catch(JsonSyntaxException e) {
							Log.e(TAG, "Wrong syntax : " + result);
						}
								
						Log.d(TAG, "Data is available for request id " + refKey);

			//		}
				}
				
			}
		};
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void fetchData(String url) {
		Intent intent = new Intent(this, RestFulDataManager.class);
		intent.putExtra(RestFulDataManager.REQUESTED_URL, url);
		startService(intent);
	}

}
