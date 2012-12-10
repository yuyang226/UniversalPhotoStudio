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

import com.gmail.charleszq.ups.R;
import com.gmail.charleszq.ups.UPSApplication;
import com.gmail.charleszq.ups.model.MediaObject;
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

	private static final String IMAGE_DATA_EXTRA = "extra_image_data"; //$NON-NLS-1$
	private static final String MEDIA_OBJ_POS = "media_object"; //$NON-NLS-1$
	private String mImageUrl;
	private MediaObject mPhoto;
	private ImageView mImageView;
	private ImageFetcher mImageFetcher;

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
		UPSApplication app = (UPSApplication) getActivity().getApplication();
		mPhoto = app.getPhotosProvider().getMediaObject(pos);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate and locate the main ImageView
		final View v = inflater.inflate(R.layout.image_detail_fragment,
				container, false);
		mImageView = (ImageView) v.findViewById(R.id.imageView);
		return v;
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
		return cmd.execute(mPhoto);
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
		super.onDestroy();
		if (mImageView != null) {
			// Cancel any pending image work
			ImageWorker.cancelWork(mImageView);
			mImageView.setImageDrawable(null);
		}
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
	public void onPrepareOptionsMenu(Menu menu) {

		UPSApplication app = (UPSApplication) getActivity().getApplication();

		Bitmap bmp = mImageFetcher.getBitmapFromCache(mImageUrl);
		MenuItem wallPaperItem = menu.findItem(R.id.menu_item_wallpaper);
		MenuItem likeItem = menu.findItem(R.id.menu_item_like_photo);
		if (bmp == null) {
			wallPaperItem.setEnabled(false);
			likeItem.setEnabled(false);
		} else {
			wallPaperItem.setEnabled(true);
			if (this.mPhoto != null) {

				switch( mPhoto.getMediaSource() ) {
				case FLICKR:
					likeItem.setEnabled(app.getUserId() != null);
					break;
				case INSTAGRAM:
					likeItem.setEnabled(app.getInstagramAuthToken()!=null);
					break;
				}
			} else {
				likeItem.setEnabled(false);
			}
		}
		getActivity().invalidateOptionsMenu();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			getActivity().finish();
			return true;
		case R.id.menu_item_like_photo:
			likePhoto();
			return true;
		case R.id.menu_item_wallpaper:
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
		return super.onOptionsItemSelected(item);
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
