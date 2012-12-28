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

package com.gmail.charleszq.picorner.ui;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.model.MediaObjectCollection;
import com.gmail.charleszq.picorner.ui.command.ICommand;
import com.gmail.charleszq.picorner.ui.command.PhotoListCommand;

/**
 * The main fragment that powers the ImageGridActivity screen. Fairly straight
 * forward GridView implementation with the key addition being the ImageWorker
 * class w/ImageCache to load children asynchronously, keeping the UI nice and
 * smooth and caching thumbnails for quick retrieval. The cache is retained over
 * configuration changes like orientation change so the images are populated
 * quickly if, for example, the user rotates the device.
 */
public class PhotoGridFragment extends AbstractPhotoGridFragment {

	private static final String TAG = PhotoGridFragment.class.getName();

	/**
	 * Empty constructor as per the Fragment documentation
	 */
	public PhotoGridFragment() {
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
		if (command == mCurrentCommand || getActivity() == null) {
			// make sure this method will not be called after click the main
			// menu item.
			Log.d(TAG, "command is the same, just ignore."); //$NON-NLS-1$
			return;
		}
		if (this.mGridView != null) {
			mGridView.setOnScrollListener(null);
			mGridView.smoothScrollToPositionFromTop(0, 0);
		}
		this.mCurrentCommand = (PhotoListCommand) command;

		// remove command done from the main menu UI, so later when load more
		// data, this method will not called again.
		mCurrentCommand.clearCommandDoneListener();
		mNoMoreData = false;

		mPhotosProvider.loadData(photos, command);
		mAdapter.notifyDataSetChanged();
		if (mGridView != null) {
			mScrollListener = new GridOnScrollListener(this);
			mGridView.setOnScrollListener(mScrollListener);
		}
		if (mLoadingMessageText != null) {
			mLoadingMessageText.setVisibility(View.GONE);
		}

		// add listener for load more, so after done, we can hide the message.
		mCurrentCommand.addCommndDoneListener(mCommandDoneListener);

		//show main menu at the first time.
		if (getActivity() != null) {
			PicornerApplication app = (PicornerApplication) getActivity()
					.getApplication();
			if (app.isFirstTime()) {
				((MainSlideMenuActivity) getActivity()).getSlidingMenu()
						.showMenu(true);
				app.setFirstTimeFalse();
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		activity.getActionBar().setSubtitle( R.string.main_photo_grid );
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		final Intent i = new Intent(getActivity(), ImageDetailActivity.class);
		i.putExtra(ImageDetailActivity.DP_KEY, mPhotosProvider);
		i.putExtra(ImageDetailActivity.LARGE_IMAGE_POSITION, (int) id);
		startActivity(i);
	}

	@Override
	protected void loadFirstPage() {
	}

	@Override
	protected void initialIntentData(Intent intent) {
		// do nothing here.
	}

	@Override
	protected String getLoadingMessage() {
		return getString(R.string.loading_photos);
	}

	@Override
	protected void bindData() {
		if (mLoadingMessageText != null) {
			mLoadingMessageText.setText(mLoadingMessage);
			mLoadingMessageText.setVisibility(View.GONE);
		}
	}
}
