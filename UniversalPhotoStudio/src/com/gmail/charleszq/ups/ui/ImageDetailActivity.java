/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gmail.charleszq.ups.ui;

import java.util.HashSet;
import java.util.Set;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;

import com.gmail.charleszq.ups.R;
import com.gmail.charleszq.ups.UPSApplication;
import com.gmail.charleszq.ups.dp.IPhotosProvider;
import com.gmail.charleszq.ups.model.MediaObject;
import com.gmail.charleszq.ups.utils.IConstants;
import com.gmail.charleszq.ups.utils.ImageCache;
import com.gmail.charleszq.ups.utils.ImageFetcher;

public class ImageDetailActivity extends FragmentActivity implements
		OnClickListener {
	public static final String EXTRA_IMAGE = "extra_image"; //$NON-NLS-1$

	private ImagePagerAdapter mAdapter;
	private ImageFetcher mImageFetcher;
	private ViewPager mPager;
	
	private Set<IActionBarVisibleListener> mActionBarListeners;

	private IPhotosProvider mPhotosProvider;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_detail_pager);

		// Fetch screen height and width, to use as our max size when loading
		// images as this
		// activity runs full screen
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		final int height = displayMetrics.heightPixels;
		final int width = displayMetrics.widthPixels;

		// For this sample we'll use half of the longest width to resize our
		// images. As the
		// image scaling ensures the image is larger than this, we should be
		// left with a
		// resolution that is appropriate for both portrait and landscape. For
		// best image quality
		// we shouldn't divide by 2, but this will use more memory and require a
		// larger memory
		// cache.
		final int longest = (height > width ? height : width) / 2;

		ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(
				this, IConstants.IMAGE_CACHE_DIR);
		cacheParams.setMemCacheSizePercent(this, 0.25f); // Set memory cache to
															// 25% of mem class

		// The ImageFetcher takes care of loading images into our ImageView
		// children asynchronously
		mImageFetcher = new ImageFetcher(this, longest);
		mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
		mImageFetcher.setImageFadeIn(false);

		// Set up ViewPager and backing adapter
		UPSApplication app = (UPSApplication) this.getApplication();
		mPhotosProvider = app.getPhotosProvider();
		mAdapter = new ImagePagerAdapter(mPhotosProvider,
				getSupportFragmentManager(), mPhotosProvider.getTotalCount());
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		mPager.setPageMargin((int) getResources().getDimension(
				R.dimen.image_detail_pager_margin));
		mPager.setOffscreenPageLimit(2);

		// Set up activity to go full screen
		getWindow().addFlags(LayoutParams.FLAG_FULLSCREEN);

		// Enable some additional newer visibility and ActionBar features to
		// create a more
		// immersive photo viewing experience
		{
			final ActionBar actionBar = getActionBar();

			// Hide title text and set home as up
			actionBar.setDisplayShowTitleEnabled(false);
			actionBar.setDisplayHomeAsUpEnabled(true);

			// Hide and show the ActionBar as the visibility changes
			mPager.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
				@Override
				public void onSystemUiVisibilityChange(int vis) {
					boolean shown = true;
					if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
						actionBar.hide();
						shown = false;
					} else {
						actionBar.show();
					}
					
					if(mActionBarListeners != null ) {
						for( IActionBarVisibleListener lis : mActionBarListeners ) {
							lis.onActionBarShown(shown);
						}
					}
				}
			});

			// Start low profile mode and hide ActionBar
			mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
			actionBar.hide();
		}

		// Set the current item based on the extra passed in to this activity
		final int extraCurrentItem = getIntent().getIntExtra(EXTRA_IMAGE, -1);
		if (extraCurrentItem != -1) {
			mPager.setCurrentItem(extraCurrentItem);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		mImageFetcher.setExitTasksEarly(false);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mImageFetcher.setExitTasksEarly(true);
		mImageFetcher.flushCache();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mImageFetcher.closeCache();
	}

	/**
	 * Called by the ViewPager child fragments to load images via the one
	 * ImageFetcher
	 */
	public ImageFetcher getImageFetcher() {
		return mImageFetcher;
	}

	/**
	 * The main adapter that backs the ViewPager. A subclass of
	 * FragmentStatePagerAdapter as there could be a large number of items in
	 * the ViewPager and we don't want to retain them all in memory at once but
	 * create/destroy them on the fly.
	 */
	private class ImagePagerAdapter extends FragmentStatePagerAdapter {
		private final int mSize;
		private IPhotosProvider mProvider;

		public ImagePagerAdapter(IPhotosProvider provider, FragmentManager fm,
				int size) {
			super(fm);
			mSize = size;
			this.mProvider = provider;
		}

		@Override
		public int getCount() {
			return mSize;
		}

		@Override
		public Fragment getItem(int position) {
			MediaObject obj = mProvider.getMediaObject(position);
			ImageDetailFragment frg = ImageDetailFragment
					.newInstance(obj.getLargeUrl() == null ? obj.getThumbUrl()
							: obj.getLargeUrl(), position);
			return frg;
		}
	}

	/**
	 * Set on the ImageView in the ViewPager children fragments, to
	 * enable/disable low profile mode when the ImageView is touched.
	 */
	@Override
	public void onClick(View v) {
		final int vis = mPager.getSystemUiVisibility();
		if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
			mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
		} else {
			mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
		}
	}
	
	void addActionBarListener( IActionBarVisibleListener lis ) {
		if( mActionBarListeners == null ) {
			mActionBarListeners = new HashSet<IActionBarVisibleListener>();
		}
		mActionBarListeners.add(lis);
	}
	
	void removeActionBarListener( IActionBarVisibleListener lis ) {
		if( mActionBarListeners != null ) {
			mActionBarListeners.remove(lis);
		}
	}
	
	/**
	 * @author charles(charleszq@gmail.com)
	 *
	 */
	static interface IActionBarVisibleListener {
		void onActionBarShown( boolean show );
	}
}
