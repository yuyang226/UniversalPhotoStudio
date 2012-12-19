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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.app.ActionBar;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.ShareActionProvider.OnShareTargetSelectedListener;
import android.widget.Toast;

import com.capricorn.ArcMenu;
import com.gmail.charleszq.ups.R;
import com.gmail.charleszq.ups.UPSApplication;
import com.gmail.charleszq.ups.model.MediaObject;
import com.gmail.charleszq.ups.ui.ImageDetailActivity.IActionBarVisibleListener;
import com.gmail.charleszq.ups.ui.command.ICommand;
import com.gmail.charleszq.ups.ui.command.ICommandDoneListener;
import com.gmail.charleszq.ups.ui.command.LikePhotoCommand;
import com.gmail.charleszq.ups.utils.IConstants;
import com.gmail.charleszq.ups.utils.ImageFetcher;
import com.gmail.charleszq.ups.utils.ImageUtils;
import com.gmail.charleszq.ups.utils.ImageWorker;

/**
 * This fragment will populate the children of the ViewPager from
 * {@link ImageDetailActivity}.
 */
public class ImageDetailFragment extends Fragment implements
		OnShareTargetSelectedListener {

	private static final int MENU_ITEM_LIKE = 1001;
	private static final int MENU_ITEM_WALLPAPER = 1002;
	private static final int MENU_ITEM_DETAIL = 1003;

	private static final String IMAGE_DATA_EXTRA = "extra_image_data"; //$NON-NLS-1$
	private static final String MEDIA_OBJ_POS = "media_object"; //$NON-NLS-1$
	private String mImageUrl;
	private MediaObject mPhoto;
	private ImageView mImageView;
	private ImageFetcher mImageFetcher;
	private ArcMenu mArcMenu;

	/**
	 * The current pos of the image in the photo list.
	 */
	private int mCurrentPos;

	private IActionBarVisibleListener mActionBarListener = new IActionBarVisibleListener() {

		@Override
		public void onActionBarShown(boolean show) {
			ImageDetailFragment.this.onActionBarShown(show);

		}
	};

	/**
	 * Factory method to generate a new instance of the fragment given an image
	 * number.
	 * 
	 * @param imageUrl
	 *            The image url to load
	 * @return A new instance of ImageDetailFragment with imageNum extras
	 */
	public static ImageDetailFragment newInstance(String imageUrl, int pos) {
		final ImageDetailFragment f = new ImageDetailFragment();

		final Bundle args = new Bundle();
		args.putString(IMAGE_DATA_EXTRA, imageUrl);
		args.putInt(MEDIA_OBJ_POS, pos);
		f.setArguments(args);

		return f;
	}

	private void onActionBarShown(boolean show) {
		if (mArcMenu != null) {
			mArcMenu.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
		}
	}

	/**
	 * Empty constructor as per the Fragment documentation
	 */
	public ImageDetailFragment() {

	}

	/**
	 * Populate image using a url from extras, use the convenience factory
	 * method {@link ImageDetailFragment#newInstance(String)} to create this
	 * fragment.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageUrl = getArguments() != null ? getArguments().getString(
				IMAGE_DATA_EXTRA) : null;
		int pos = (getArguments() != null ? getArguments()
				.getInt(MEDIA_OBJ_POS) : -1);
		mCurrentPos = pos;
		ImageDetailActivity act = (ImageDetailActivity) getActivity();
		UPSApplication app = (UPSApplication) act.getApplication();
		mPhoto = app.getPhotosProvider().getMediaObject(pos);
		setHasOptionsMenu(true);

		act.addActionBarListener(mActionBarListener);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate and locate the main ImageView
		final View v = inflater.inflate(R.layout.image_detail_fragment,
				container, false);
		mImageView = (ImageView) v.findViewById(R.id.imageView);
		mArcMenu = (ArcMenu) v.findViewById(R.id.arc_menu);
		initArcMenu();
		return v;
	}

	private void initArcMenu() {
		OnClickListener lis = new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bitmap bmp = mImageFetcher.getBitmapFromCache(mImageUrl);
				if (bmp == null) {
					Toast.makeText(getActivity(),
							R.string.wait_for_image_loading, Toast.LENGTH_SHORT)
							.show();
					return;
				}

				Integer tag = (Integer) v.getTag();
				if (tag != null) {
					saveBitmapToShare(bmp);
					if (tag == R.id.menu_item_share_action_provider_action_bar) {
						Intent i = createShareIntent();
						getActivity().startActivity(i);
					} else {
						menuItemClicked(tag);
					}
				}

			}
		};

		ImageView v0 = new ImageView(getActivity());
		v0.setTag(R.id.menu_item_share_action_provider_action_bar);
		v0.setImageResource(android.R.drawable.ic_menu_share);
		mArcMenu.addItem(v0, lis);

		ImageView v = new ImageView(getActivity());
		v.setTag(MENU_ITEM_LIKE);
		v.setImageResource(R.drawable.ic_menu_star);
		mArcMenu.addItem(v, lis);

		ImageView v1 = new ImageView(getActivity());
		v1.setTag(MENU_ITEM_WALLPAPER);
		v1.setImageResource(android.R.drawable.ic_menu_gallery);
		mArcMenu.addItem(v1, lis);

		ImageView v2 = new ImageView(getActivity());
		v2.setTag(MENU_ITEM_DETAIL);
		v2.setImageResource(R.drawable.ic_menu_find);
		mArcMenu.addItem(v2, lis);

		ActionBar bar = getActivity().getActionBar();
		mArcMenu.setVisibility(bar.isShowing() ? View.VISIBLE : View.INVISIBLE);
	}

	private boolean likePhoto() {

		Bitmap bmp = mImageFetcher.getBitmapFromCache(mImageUrl);
		if (bmp == null) {
			return false; // image not loaded yet.
		}

		ICommand<Boolean> cmd = new LikePhotoCommand(getActivity());
		cmd.addCommndDoneListener(new ICommandDoneListener<Boolean>() {
			@Override
			public void onCommandDone(ICommand<Boolean> command, Boolean t) {
				if (t) {
					Toast.makeText(getActivity(),
							getActivity().getString(R.string.like_photo_done),
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		boolean result = cmd.execute(mPhoto);
		if (!result) {
			Toast.makeText(getActivity(),
					getActivity().getString(R.string.pls_sing_in_first),
					Toast.LENGTH_SHORT).show();
		}
		return result;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Use the parent activity to load the image asynchronously into the
		// ImageView (so a single
		// cache can be used over all pages in the ViewPager
		if (ImageDetailActivity.class.isInstance(getActivity())) {
			mImageFetcher = ((ImageDetailActivity) getActivity())
					.getImageFetcher();
			mImageFetcher.loadImage(mImageUrl, mImageView);
		}

		// Pass clicks on the ImageView to the parent activity to handle
		if (OnClickListener.class.isInstance(getActivity())) {
			mImageView.setOnClickListener((OnClickListener) getActivity());
		}
	}

	@Override
	public void onDestroy() {
		if (mImageView != null) {
			// Cancel any pending image work
			ImageWorker.cancelWork(mImageView);
			mImageView.setImageDrawable(null);
		}
		ImageDetailActivity act = (ImageDetailActivity) getActivity();
		if (act != null)
			act.removeActionBarListener(mActionBarListener);
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		ImageDetailActivity act = (ImageDetailActivity) getActivity();
		act.removeActionBarListener(mActionBarListener);
		super.onDetach();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_photo_detail, menu);
		MenuItem actionItem = menu
				.findItem(R.id.menu_item_share_action_provider_action_bar);
		ShareActionProvider actionProvider = (ShareActionProvider) actionItem
				.getActionProvider();
		actionProvider
				.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
		// Note that you can set/change the intent any time,
		// say when the user has selected an image.
		actionProvider.setShareIntent(createShareIntent());
		actionProvider.setOnShareTargetSelectedListener(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (!menuItemClicked(item.getItemId())) {
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private boolean menuItemClicked(int itemid) {
		switch (itemid) {
		case android.R.id.home:
			getActivity().finish();
			return true;
		case MENU_ITEM_DETAIL:
			Intent i = new Intent(getActivity(), PhotoDetailActivity.class);
			i.putExtra(ImageDetailActivity.EXTRA_IMAGE, mCurrentPos);
			startActivity(i);
			return true;
		case MENU_ITEM_LIKE:
			likePhoto();
			return true;
		case MENU_ITEM_WALLPAPER:
			Bitmap bmp = mImageFetcher.getBitmapFromCache(mImageUrl);
			saveBitmapToShare(bmp);
			WallpaperManager wm = WallpaperManager.getInstance(getActivity());
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(getShareImageFile());
				wm.setStream(fis);
				Toast.makeText(
						getActivity(),
						getResources()
								.getString(R.string.msg_wallpaper_changed),
						Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
					}
				}
			}
			return true;
		}
		return false;
	}

	private File getShareImageFile() {
		File root = new File(Environment.getExternalStorageDirectory(),
				IConstants.SD_CARD_FOLDER_NAME);
		File saveFile = new File(root, IConstants.SHARE_TEMP_FILE_NAME);
		return saveFile;
	}

	private Intent createShareIntent() {
		File bsRoot = new File(Environment.getExternalStorageDirectory(),
				IConstants.SD_CARD_FOLDER_NAME);
		File shareFile = new File(bsRoot, IConstants.SHARE_TEMP_FILE_NAME);
		Uri uri = Uri.parse("file://" + shareFile.getAbsolutePath()); //$NON-NLS-1$

		StringBuilder sb = new StringBuilder(mImageUrl);
		sb.append(" ").append(getString(R.string.share_via)).append(" "); //$NON-NLS-1$//$NON-NLS-2$
		sb.append(getString(R.string.app_name));

		Intent shareIntent = ShareCompat.IntentBuilder.from(getActivity())
				.setText(sb.toString()).setType("image/*").setStream(uri) //$NON-NLS-1$
				.getIntent();
		shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

		return shareIntent;
	}

	@Override
	public boolean onShareTargetSelected(ShareActionProvider source,
			Intent intent) {
		Bitmap bmp = mImageFetcher.getBitmapFromCache(mImageUrl);
		if (bmp != null) {
			saveBitmapToShare(bmp);
		}
		return false;
	}

	private void saveBitmapToShare(Bitmap bitmap) {
		File bsRoot = new File(Environment.getExternalStorageDirectory(),
				IConstants.SD_CARD_FOLDER_NAME);
		if (!bsRoot.exists() && !bsRoot.mkdir()) {
			return;
		}
		File shareFile = new File(bsRoot, IConstants.SHARE_TEMP_FILE_NAME);
		if (shareFile.exists()) {
			shareFile.delete();
		}
		ImageUtils.saveImageToFile(shareFile, bitmap);
	}
}
