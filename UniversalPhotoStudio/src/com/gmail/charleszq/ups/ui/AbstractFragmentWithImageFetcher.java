/**
 * 
 */
package com.gmail.charleszq.ups.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public abstract class AbstractFragmentWithImageFetcher extends Fragment {

	protected ImageLoader mImageFetcher;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageFetcher = ImageLoader.getInstance();
	}

	@Override
	public void onDestroy() {
		mImageFetcher.stop();
		super.onDestroy();
	}
}
