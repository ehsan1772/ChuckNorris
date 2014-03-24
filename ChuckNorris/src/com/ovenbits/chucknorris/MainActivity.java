package com.ovenbits.chucknorris;

import com.foundation.restful.RestFulDataManager;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getSimpleName();
	private String url = "http://api.icndb.com/jokes/15";
	
	BroadcastReceiver downloadUpdateReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.hasExtra(RestFulDataManager.REF_KEY)) {
				int refKey = intent.getExtras().getInt(RestFulDataManager.REF_KEY);
				if (refKey == url.hashCode()) {
					Log.d(TAG, "Data is available for request id " + refKey);
					String result = RestFulDataManager.getInstance().getData(refKey, String.class);
				}
			}
			
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		IntentFilter filter = new IntentFilter();
		filter.addAction("RESULT");
		LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(downloadUpdateReceiver, filter);
		
		fetchData();
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void fetchData() {
		Intent intent = new Intent(this, RestFulDataManager.class);
		intent.putExtra(RestFulDataManager.REQUESTED_URL, "Test");
		startService(intent);
	}

}
