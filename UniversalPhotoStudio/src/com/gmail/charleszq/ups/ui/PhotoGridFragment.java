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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.gmail.charleszq.ups.R;
import com.gmail.charleszq.ups.UPSApplication;
import com.gmail.charleszq.ups.dp.IPhotosProvider;
import com.gmail.charleszq.ups.dp.SinglePagePhotosProvider;
import com.gmail.charleszq.ups.model.MediaObjectCollection;
import com.gmail.charleszq.ups.ui.adapter.PhotoGridAdapter;
import com.gmail.charleszq.ups.ui.command.ICommand;
import com.gmail.charleszq.ups.utils.IConstants;

/**
 * The main fragment that powers the ImageGridActivity screen. Fairly straight
 * forward GridView implementation with the key addition being the ImageWorker
 * class w/ImageCache to load children asynchronously, keeping the UI nice and
 * smooth and caching thumbnails for quick retrieval. The cache is retained over
 * configuration changes like orientation change so the images are populated
 * quickly if, for example, the user rotates the device.
 */
public class PhotoGridFragment extends AbstractFragmentWithImageFetcher implements
		AdapterView.OnItemClickListener {

	private int mImageThumbSize;
	private int mImageThumbSpacing;
	private PhotoGridAdapter mAdapter;
	private GridView mGridView = null;

	private IPhotosProvider mPhotosProvider = new SinglePagePhotosProvider(
			new MediaObjectCollection());
	private ICommand<?> mCurrentCommand = null;

	/**
	 * Empty constructor as per the Fragment documentation
	 */
	public PhotoGridFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		final View v = inflater.inflate(R.layout.image_grid_fragment,
				container, false);
		mGridView = (GridView) v.findViewById(R.id.gridView);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(this);
		mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView absListView,
					int scrollState) {
				// Pause fetcher to ensure smoother scrolling when flinging
				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
					mImageFetcher.setPauseWork(true);
				} else {
					mImageFetcher.setPauseWork(false);
				}
			}

			@Override
			public void onScroll(AbsListView absListView, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});

		// This listener is used to get the final width of the GridView and then
		// calculate the
		// number of columns and the width of each column. The width of each
		// column is variable
		// as the GridView has stretchMode=columnWidth. The column width is used
		// to set the height
		// of each view so we get nice square thumbnails.
		mGridView.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						if (mAdapter.getNumColumns() == 0) {
							final int numColumns = (int) Math.floor(mGridView
									.getWidth()
									/ (mImageThumbSize + mImageThumbSpacing));
							if (numColumns > 0) {
								final int columnWidth = (mGridView.getWidth() / numColumns)
										- mImageThumbSpacing;
								mAdapter.setNumColumns(numColumns);
								mAdapter.setItemHeight(columnWidth);
							}
						}
					}
				});

		return v;
	}

	/**
	 * This method will be only be called from the menu fragment, after photos
	 * are loaded, if we need to load more photos(the following pages), it will
	 * call dp.loadData();
	 * 
	 * @param photos
	 * @param command
	 */
	void populatePhotoList(MediaObjectCollection photos, ICommand<?> command) {
		this.mCurrentCommand = command;
		mPhotosProvider.loadData(photos, command);
		mAdapter.notifyDataSetChanged();
		if (mGridView != null) {
			mGridView.smoothScrollToPositionFromTop(0, 0);
		}
		UPSApplication app = (UPSApplication) getActivity().getApplication();
		app.setPhotosProvider(this.mPhotosProvider);
	}

	@Override
	public void onResume() {
		super.onResume();
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mImageThumbSize = getResources().getDimensionPixelSize(
				R.dimen.image_thumbnail_size);
		mImageThumbSpacing = getResources().getDimensionPixelSize(
				R.dimen.image_thumbnail_spacing);

		initializeImageFetcher(IConstants.IMAGE_THUMBS_CACHE_DIR, mImageThumbSize);
		mAdapter = new PhotoGridAdapter( getActivity(), mPhotosProvider, mImageFetcher);

		//when configuration changes, mCurrentCommand will be saved, at this time, we
		//need to attach the current context to it, otherwise, there will be NPEs.
		if( mCurrentCommand != null ) {
			mCurrentCommand.attacheContext(getActivity());
		}
		
		this.setRetainInstance(true);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		final Intent i = new Intent(getActivity(), ImageDetailActivity.class);
		i.putExtra(ImageDetailActivity.EXTRA_IMAGE, (int) id);
		startActivity(i);
	}
}
