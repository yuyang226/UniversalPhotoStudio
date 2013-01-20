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

import java.util.Comparator;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.model.MediaObjectCollection;
import com.gmail.charleszq.picorner.offline.IOfflineViewParameter;
import com.gmail.charleszq.picorner.offline.OfflineControlFileUtil;
import com.gmail.charleszq.picorner.ui.command.ICommand;
import com.gmail.charleszq.picorner.ui.command.PhotoListCommand;
import com.slidingmenu.lib.SlidingMenu.OnOpenedListener;

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
	 * In some cases, the command instance might be the same, this will provide
	 * another way to tell whether we should populate a new result.
	 */
	private Object mCommandComparator = null;

	/**
	 * This method will be only be called from the menu fragment, after photos
	 * are loaded, if we need to load more photos(the following pages), it will
	 * call dp.loadData();
	 * 
	 * @param photos
	 * @param command
	 */
	void populatePhotoList(MediaObjectCollection photos, ICommand<?> command) {
		if (getActivity() == null) {
			Log.w(TAG, "activity is null!"); //$NON-NLS-1$
			return;
		}

		if (command == mCurrentCommand) {

			Object newComparator = command.getAdapter(Comparator.class);
			if (newComparator == null
					|| newComparator.equals(mCommandComparator)) {
				// make sure this method will not be called after click the main
				// menu item.
				Log.d(TAG, "command is the same, just ignore."); //$NON-NLS-1$
				return;
			}
		}

		if (this.mGridView != null) {
			mGridView.setOnScrollListener(null);
			mGridView.smoothScrollToPositionFromTop(0, 0);
		}
		this.mCurrentCommand = (PhotoListCommand) command;
		mCommandComparator = mCurrentCommand.getAdapter(Comparator.class);

		// remove command done from the main menu UI, so later when load more
		// data, this method will not called again.
		mNoMoreData = false;

		mPhotosProvider.loadData(photos, mCommandComparator == null ? command
				: mCommandComparator);
		mAdapter.notifyDataSetChanged();
		if (mGridView != null) {
			mScrollListener = new GridOnScrollListener(this);
			mGridView.setOnScrollListener(mScrollListener);
		}
		if (mLoadingMessageText != null) {
			mLoadingMessageText.setVisibility(View.GONE);
		}

		// add listener for load more, so after done, we can hide the message.
		mCurrentCommand.setCommndDoneListener(mCommandDoneListener);

		MainSlideMenuActivity act = (MainSlideMenuActivity) getActivity();
		if (act != null) {
			// show help layer at the first time.
			PicornerApplication app = (PicornerApplication) act
					.getApplication();
			if (app.isFirstTime()) {
				final View v = LayoutInflater.from(getActivity()).inflate(
						R.layout.photo_grid_help_layer, null);
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				v.setLayoutParams(params);
				ViewGroup vp = (ViewGroup) getView();
				vp.addView(v);

				// close the help layer if you open the menu.
				act.getSlidingMenu().setOnOpenedListener(
						new OnOpenedListener() {
							@Override
							public void onOpened() {
								v.setVisibility(View.GONE);
							}
						});

				app.setFirstTimeFalse();
			}

			// set the subtitle of the action bar
			getActivity().getActionBar().setSubtitle(
					mCurrentCommand.getDescription());
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "Current photo size: " + mAdapter.getCount()); //$NON-NLS-1$
		if (mCurrentCommand != null) {
			getActivity().getActionBar().setSubtitle(
					mCurrentCommand.getDescription());
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		final Intent i = new Intent(getActivity(), ImageDetailActivity.class);
		i.putExtra(ImageDetailActivity.DP_KEY, mPhotosProvider);
		i.putExtra(ImageDetailActivity.LARGE_IMAGE_POSITION, (int) id);
		if (mCurrentCommand != null) {
			IOfflineViewParameter offlineParam = (IOfflineViewParameter) mCurrentCommand
					.getAdapter(IOfflineViewParameter.class);
			boolean offlineEnabled = false;
			if (offlineParam != null) {
				offlineEnabled = OfflineControlFileUtil
						.isOfflineViewEnabled(offlineParam);
			}
			i.putExtra(ImageDetailActivity.OFFLINE_COMMAND_KEY,
					Boolean.toString(offlineEnabled));
		}
		startActivity(i);
	}

	@Override
	protected void loadFirstPage() {
		MainSlideMenuActivity act = (MainSlideMenuActivity) getActivity();
		if (act != null && mCurrentCommand == null) {
			act.loadDefaultPhotoList();
		}
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
