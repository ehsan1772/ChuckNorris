package data;

import com.foundation.restful.RestFulDataManager;
import com.ovenbits.chucknorris.R;

public class ChuckRestfulDataManager extends RestFulDataManager{

	@Override
	protected int getThreadPoolSize() {
		return getResources().getInteger(R.integer.thread_pool_size);
	}

	@Override
	protected int getDownloadQueueSize() {
		return getResources().getInteger(R.integer.download_queue_size);
	}

	@Override
	protected int getCacheSize() {
		return getResources().getInteger(R.integer.cache_size);
	}

}
