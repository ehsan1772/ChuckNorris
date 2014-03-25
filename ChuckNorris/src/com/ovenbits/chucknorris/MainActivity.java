package com.ovenbits.chucknorris;

import com.foundation.restful.RestFulDataManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.ovenbits.chucknorris.JokeFragment.JokeActivity;

import data.Joke;
import data.JokeNumber;

import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
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
	
	private JokeFragment jokeFragment;
	private String countUrl;
	
	String fakeResult= "{\"type\": \"success\"}";
	
	private BroadcastReceiver downloadUpdateReceiver;
	private BroadcastReceiver jokeCountReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	//	jokeFragment = new JokeFragment();
		
//		createJokeReceiver();
//		jokeCountReceiver();
		
//		IntentFilter filter = new IntentFilter();
//		filter.addAction("com.ovenbits.chucknorris.RESULT");
//		LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(downloadUpdateReceiver, filter);
//		LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(jokeCountReceiver, filter);
		
//		fetchData(countUrl);
	}
	
	@Override
	protected void onResume() {
		Fragment existingFragment = getFragmentManager().findFragmentByTag(JokeFragment.TAG);
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		if (existingFragment == null) {
			transaction.replace(R.id.joke_fragment_container, JokeFragment.getInstance(), JokeFragment.TAG);
		} else {
			transaction.replace(R.id.joke_fragment_container, existingFragment, JokeFragment.TAG);
		}
		transaction.commit();
		super.onResume();
	}
	

//	private void createJokeReceiver() {
//		downloadUpdateReceiver = new BroadcastReceiver() {
//			
//			@Override
//			public void onReceive(Context context, Intent intent) {
//				if (intent.hasExtra(RestFulDataManager.REF_KEY)) {
//					int refKey = intent.getExtras().getInt(RestFulDataManager.REF_KEY);
//			//		if (refKey == url.hashCode()) {
//					String result = RestFulDataManager.getInstance().getData(refKey, String.class);
//					Log.d(TAG, "Result is: " + result);
//					
//						Gson gson = new GsonBuilder().create();
//						try {
//						Joke joke = gson.fromJson(result, Joke.class);
//						} catch(JsonSyntaxException e) {
//							Log.e(TAG, "Wrong syntax : " + result);
//						}
//								
//						Log.d(TAG, "Data is available for request id " + refKey);
//
//			//		}
//				}
//				
//			}
//		};
//	}
//	
//	private void jokeCountReceiver() {
//		countUrl = getResources().getString(R.string.joke_count_url);
//		fetchData(countUrl);
//		jokeCountReceiver = new BroadcastReceiver() {
//			
//			@Override
//			public void onReceive(Context context, Intent intent) {
//				if (intent.hasExtra(RestFulDataManager.REF_KEY)) {
//					int refKey = intent.getExtras().getInt(RestFulDataManager.REF_KEY);
//					if (refKey == countUrl.hashCode()) {
//					String result = RestFulDataManager.getInstance().getData(refKey, String.class);
//					Log.d(TAG, "Result is: " + result);
//					JokeNumber number = null;
//						Gson gson = new GsonBuilder().create();
//						try {
//						number = gson.fromJson(result, JokeNumber.class);
//						int count = number.getValue();
//						} catch(JsonSyntaxException e) {
//							Log.e(TAG, "Wrong syntax : " + result);
//						}
//						int value = number.getValue();		
//						Log.d(TAG, "Data is available for request id " + refKey);
//
//					}
//				}
//				
//			}
//		};
//	}
	
	

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
	
	@Override
	protected void onDestroy() {
		LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(downloadUpdateReceiver);
		LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(jokeCountReceiver);
		super.onDestroy();
	}

}
