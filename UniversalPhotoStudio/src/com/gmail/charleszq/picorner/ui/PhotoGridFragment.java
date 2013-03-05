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

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.SpinnerAdapter;

import com.github.yuyang226.j500px.photos.PhotoCategory;
import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.SPUtil;
import com.gmail.charleszq.picorner.model.MediaObjectCollection;
import com.gmail.charleszq.picorner.msg.Message;
import com.gmail.charleszq.picorner.msg.MessageBus;
import com.gmail.charleszq.picorner.offline.IOfflineViewParameter;
import com.gmail.charleszq.picorner.offline.OfflineControlFileUtil;
import com.gmail.charleszq.picorner.ui.command.ICommand;
import com.gmail.charleszq.picorner.ui.command.PhotoListCommand;
import com.gmail.charleszq.picorner.ui.command.flickr.GroupSearchPhotosCommand;
import com.gmail.charleszq.picorner.ui.command.flickr.MyGroupsCommand;
import com.gmail.charleszq.picorner.ui.command.px500.AbstractPx500PhotoListCommand;
import com.gmail.charleszq.picorner.ui.flickr.FlickrGroupInfoDialog;
import com.googlecode.flickrjandroid.groups.Group;
import com.slidingmenu.lib.SlidingMenu.OnOpenedListener;

/**
 * The main fragment that powers the ImageGridActivity screen. Fairly straight
 * forward GridView implementation with the key addition being the ImageWorker
 * class w/ImageCache to load children asynchronously, keeping the UI nice and
 * smooth and caching thumbnails for quick retrieval. The cache is retained over
 * configuration changes like orientation change so the images are populated
 * quickly if, for example, the user rotates the device.
 */
public class PhotoGridFragment extends AbstractPhotoGridFragment implements
		OnNavigationListener {

	private static final String TAG = PhotoGridFragment.class.getName();

	/**
	 * Show this dialog when 500px category changes.
	 */
	private ProgressDialog mCategoryDialog = null;

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

		if (mCategoryDialog != null) {
			try {
				mCategoryDialog.dismiss();
			} catch (Exception e) {
			}
		}
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

		// mGridView.setOnScrollListener(null);
		this.mCurrentCommand = (PhotoListCommand) command;
		getActivity().invalidateOptionsMenu();
		mCommandComparator = mCurrentCommand.getAdapter(Comparator.class);

		// remove command done from the main menu UI, so later when load more
		// data, this method will not called again.
		mNoMoreData = false;

		mPhotosProvider.loadData(photos, command, mCommandComparator);
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

			prepareActionBar(act);
		}
	}

	private void prepareActionBar(Activity act) {
		if (AbstractPx500PhotoListCommand.class.isInstance(mCurrentCommand)) {
			List<PhotoCategory> categories = Arrays.asList(PhotoCategory
					.values());
			SpinnerAdapter adapter = new ArrayAdapter<String>(act,
					R.layout.px500_category_item, act.getResources()
							.getStringArray(R.array.px500_categories));
			act.getActionBar()
					.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			act.getActionBar().setListNavigationCallbacks(adapter, this);

			PhotoCategory cat = ((AbstractPx500PhotoListCommand) mCurrentCommand)
					.getPhotoCategory();
			int pos = categories.indexOf(cat);
			act.getActionBar().setSelectedNavigationItem(pos);
		} else {
			act.getActionBar().setNavigationMode(
					ActionBar.NAVIGATION_MODE_STANDARD);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "Current photo size: " + mAdapter.getCount()); //$NON-NLS-1$
		if (mCurrentCommand != null) {
			getActivity().getActionBar().setSubtitle(
					mCurrentCommand.getDescription());

			prepareActionBar(getActivity());
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		final Intent i = new Intent(getActivity(), ImageDetailActivity.class);
		i.putExtra(ImageDetailActivity.DP_KEY, mPhotosProvider);
		i.putExtra(ImageDetailActivity.LARGE_IMAGE_POSITION, (int) id);
		if (mCurrentCommand != null) {
			boolean overallOfflineEnabled = SPUtil
					.isOfflineEnabled(getActivity());
			if (overallOfflineEnabled) {
				IOfflineViewParameter offlineParam = (IOfflineViewParameter) mCurrentCommand
						.getAdapter(IOfflineViewParameter.class);
				boolean offlineEnabled = false;
				if (offlineParam != null) {
					offlineEnabled = OfflineControlFileUtil
							.isOfflineViewEnabled(getActivity(), offlineParam);
				}
				i.putExtra(ImageDetailActivity.OFFLINE_COMMAND_KEY,
						Boolean.toString(offlineEnabled));
			}
			Object showActionBarString = mCurrentCommand
					.getAdapter(ActionBar.class);
			if (showActionBarString == null) {
				showActionBarString = Boolean.toString(true);
			}
			i.putExtra(ImageDetailActivity.SHOW_ACTION_BAR_KEY,
					Boolean.parseBoolean(showActionBarString.toString()));
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_flickr_group_info, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_item_flickr_group_info) {
			Group group = (Group) mCurrentCommand.getAdapter(Comparator.class);
			if (group == null)
				return false;

			Intent intent = new Intent(getActivity(),
					FlickrGroupInfoDialog.class);
			intent.putExtra(FlickrGroupInfoDialog.F_GROUP_ID_KEY, group.getId());
			intent.putExtra(FlickrGroupInfoDialog.F_GROUP_TITLE_KEY,
					group.getName());
			if (MyGroupsCommand.class.isInstance(mCurrentCommand))
				intent.putExtra(FlickrGroupInfoDialog.F_GROUP_MY_GROUP_KEY,
						Boolean.TRUE);
			getActivity().startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		MenuItem item = menu.findItem(R.id.menu_item_flickr_group_info);
		if (this.mCurrentCommand == null)
			item.setVisible(false);
		else {
			item.setVisible(MyGroupsCommand.class.isInstance(mCurrentCommand)
					|| GroupSearchPhotosCommand.class
							.isInstance(mCurrentCommand));
		}
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		if (AbstractPx500PhotoListCommand.class.isInstance(mCurrentCommand)) {
			AbstractPx500PhotoListCommand cmd = (AbstractPx500PhotoListCommand) mCurrentCommand;
			List<PhotoCategory> categories = Arrays.asList(PhotoCategory
					.values());
			PhotoCategory cat = categories.get(itemPosition);

			// ignore the first time naviagtion item change.
			if (cmd.getPhotoCategory().equals(cat))
				return false;

			cmd.setPhotoCategory(cat);
			Message msg = new Message(Message.PX500_CHG_CAT, null, null,
					mCurrentCommand);
			MessageBus.broadcastMessage(msg);

			mCategoryDialog = ProgressDialog.show(getActivity(), null,
					getActivity().getString(R.string.loading_photos));
			return true;
		} else
			return false;
	}
}
