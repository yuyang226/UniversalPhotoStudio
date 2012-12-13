/**
 * 
 */
package com.gmail.charleszq.ups.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.support.v4.app.Fragment;

import com.gmail.charleszq.ups.R;
import com.gmail.charleszq.ups.utils.ImageCache.ImageCacheParams;
import com.gmail.charleszq.ups.utils.ImageFetcher;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public abstract class AbstractFragmentWithImageFetcher extends Fragment {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	protected ImageFetcher mImageFetcher;

	/**
	 * Initializes the image fetcher
	 */
	protected void initializeImageFetcher(String cacheFolder, int imageSize) {
		// The ImageFetcher takes care of loading images into our ImageView
		// children asynchronously
		mImageFetcher = new ImageFetcher(getActivity(), imageSize);
		mImageFetcher.setLoadingImage(R.drawable.empty_photo);

		ImageCacheParams cacheParams = new ImageCacheParams(getActivity(),
				cacheFolder);

		// Set memory cache to 25% of mem class
		cacheParams.setMemCacheSizePercent(getActivity(), 0.25f);
		mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(),
				cacheParams);
	}

	@Override
	public void onDestroy() {
		mImageFetcher.closeCache();
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		mImageFetcher.setExitTasksEarly(false);
	}

	@Override
	public void onPause() {
		mImageFetcher.setExitTasksEarly(true);
		mImageFetcher.flushCache();
		super.onPause();
	}
	
	@Override
	public void onDetach() {
		mImageFetcher.closeCache();
		super.onDetach();
	}
}
