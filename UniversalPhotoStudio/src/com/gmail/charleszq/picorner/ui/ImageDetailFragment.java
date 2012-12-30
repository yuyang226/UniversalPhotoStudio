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
import android.util.Log;
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
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.dp.IPhotosProvider;
import com.gmail.charleszq.picorner.model.Author;
import com.gmail.charleszq.picorner.model.MediaObject;
import com.gmail.charleszq.picorner.ui.ImageDetailActivity.IActionBarVisibleListener;
import com.gmail.charleszq.picorner.ui.command.ICommand;
import com.gmail.charleszq.picorner.ui.command.ICommandDoneListener;
import com.gmail.charleszq.picorner.ui.command.LikePhotoCommand;
import com.gmail.charleszq.picorner.utils.IConstants;
import com.gmail.charleszq.picorner.utils.ImageUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

/**
 * This fragment will populate the children of the ViewPager from
 * {@link ImageDetailActivity}.
 */
public class ImageDetailFragment extends Fragment implements
		OnShareTargetSelectedListener {

	private static final String IMAGE_DATA_EXTRA = "extra_image_data"; //$NON-NLS-1$
	private static final String MEDIA_OBJ_POS = "media_object"; //$NON-NLS-1$
	private static final String TAG = ImageDetailFragment.class.getSimpleName();
	private String mImageUrl;
	private MediaObject mPhoto;
	private ImageView mImageView;
	private ImageLoader mImageFetcher;
	private View mUserInfoContainer;
	private TextView mPhotoTitle;
	private TextView mUserName;

	/**
	 * The current pos of the image in the photo list.
	 */
	private int mCurrentPos;

	/**
	 * The image display options
	 */
	private DisplayImageOptions mImageDisplayOptions;

	/**
	 * The loaded bitmap
	 */
	private Bitmap mLoadedBitmap = null;

	/**
	 * 
	 */
	private String mCurrentShareIntentFileName = null;

	/**
	 * The image laoder listener.
	 */
	private ImageLoadingListener mImageLoaderListener = new ImageLoadingListener() {

		@Override
		public void onLoadingStarted() {
			mLoadedBitmap = null;
		}

		@Override
		public void onLoadingFailed(FailReason failReason) {
			mLoadedBitmap = null;
		}

		@Override
		public void onLoadingComplete(Bitmap loadedImage) {
			mLoadedBitmap = loadedImage;
		}

		@Override
		public void onLoadingCancelled() {
			mLoadedBitmap = null;
		}
	};

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
	public static ImageDetailFragment newInstance(String imageUrl,
			IPhotosProvider dp, int pos) {
		final ImageDetailFragment f = new ImageDetailFragment();

		final Bundle args = new Bundle();
		args.putString(IMAGE_DATA_EXTRA, imageUrl);
		args.putInt(MEDIA_OBJ_POS, pos);
		args.putSerializable(ImageDetailActivity.DP_KEY, dp);
		f.setArguments(args);

		return f;
	}

	private void onActionBarShown(boolean show) {
		if (mUserInfoContainer != null) {
			mUserInfoContainer.setVisibility(show ? View.VISIBLE
					: View.INVISIBLE);
		}
	}

	/**
	 * Empty constructor as per the Fragment documentation
	 */
	public ImageDetailFragment() {
		mLoadedBitmap = null;
	}

	/**
	 * Populate image using a url from extras, use the convenience factory
	 * method {@link ImageDetailFragment#newInstance(String)} to create this
	 * fragment.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mImageDisplayOptions = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.empty_photo).cacheInMemory()
				.cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565).build();

		mImageUrl = getArguments() != null ? getArguments().getString(
				IMAGE_DATA_EXTRA) : null;
		int pos = (getArguments() != null ? getArguments()
				.getInt(MEDIA_OBJ_POS) : -1);
		IPhotosProvider dp = (IPhotosProvider) (getArguments() != null ? getArguments()
				.getSerializable(ImageDetailActivity.DP_KEY) : null);
		mCurrentPos = pos;
		ImageDetailActivity act = (ImageDetailActivity) getActivity();
		mPhoto = dp.getMediaObject(pos);
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

		// photo title and author name
		mUserInfoContainer = v.findViewById(R.id.photo_detail_user_info);
		ActionBar bar = getActivity().getActionBar();
		mUserInfoContainer.setVisibility(bar.isShowing() ? View.VISIBLE
				: View.INVISIBLE);
		mPhotoTitle = (TextView) v.findViewById(R.id.photo_detail_photo_title);
		mPhotoTitle.setText(mPhoto.getTitle() == null ? "" : mPhoto.getTitle()); //$NON-NLS-1$
		mUserName = (TextView) v.findViewById(R.id.photo_detail_author_name);
		StringBuilder sb = new StringBuilder();
		sb.append(getString(R.string.msg_by_author_name));
		sb.append(" "); //$NON-NLS-1$
		Author a = mPhoto.getAuthor();
		if (a != null) {
			sb.append(mPhoto.getAuthor().getUserName() == null ? mPhoto
					.getAuthor().getUserId() : mPhoto.getAuthor().getUserName());
			mUserName.setText(sb.toString());
		}

		return v;
	}

	private boolean likePhoto() {

		switch (mPhoto.getMediaSource()) {
		case PX500:
			Toast.makeText(getActivity(),
					getString(R.string.msg_not_support_yet), Toast.LENGTH_SHORT)
					.show();
			return false;
		case FLICKR:
			PicornerApplication app = (PicornerApplication) getActivity()
					.getApplication();
			if (app.getFlickrUserId() == null) {
				Toast.makeText(getActivity(),
						getString(R.string.pls_sing_in_first),
						Toast.LENGTH_SHORT).show();
				return false;
			}
			break;
		case INSTAGRAM:
			app = (PicornerApplication) getActivity().getApplication();
			if (app.getInstagramUserId() == null) {
				Toast.makeText(getActivity(),
						getString(R.string.pls_sing_in_first),
						Toast.LENGTH_SHORT).show();
				return false;
			}
			break;
		}

		if (mLoadedBitmap == null) {
			return false; // image not loaded yet.
		}

		ICommand<Boolean> cmd = new LikePhotoCommand(getActivity());
		cmd.setCommndDoneListener(new ICommandDoneListener<Boolean>() {
			@Override
			public void onCommandDone(ICommand<Boolean> command, Boolean t) {
				if (t) {
					Toast.makeText(getActivity(),
							getActivity().getString(R.string.like_photo_done),
							Toast.LENGTH_SHORT).show();
					mPhoto.setUserLiked(true);
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
			mImageFetcher.displayImage(mImageUrl, mImageView,
					mImageDisplayOptions, mImageLoaderListener);
		}

		// Pass clicks on the ImageView to the parent activity to handle
		if (OnClickListener.class.isInstance(getActivity())) {
			mImageView.setOnClickListener((OnClickListener) getActivity());
		}
	}

	@Override
	public void onDestroy() {
		if (mImageView != null) {
			mImageView.setImageDrawable(null);
		}
		ImageDetailActivity act = (ImageDetailActivity) getActivity();
		if (act != null)
			act.removeActionBarListener(mActionBarListener);
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		mLoadedBitmap = null;
		ImageDetailActivity act = (ImageDetailActivity) getActivity();
		act.removeActionBarListener(mActionBarListener);
		super.onDetach();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_photo_detail, menu);
		// MenuItem actionItem = menu
		// .findItem(R.id.menu_item_share_action_provider_action_bar);
		// ShareActionProvider actionProvider = (ShareActionProvider) actionItem
		// .getActionProvider();
		// actionProvider
		// .setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
		// // Note that you can set/change the intent any time,
		// // say when the user has selected an image.
		// actionProvider.setShareIntent(createShareIntent());
		// actionProvider.setOnShareTargetSelectedListener(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (mLoadedBitmap == null) {
			Toast.makeText(getActivity(), R.string.wait_for_image_loading,
					Toast.LENGTH_SHORT).show();
			return false;
		} else {
			saveBitmapToShare(mLoadedBitmap, null);
		}

		switch (item.getItemId()) {
		case android.R.id.home:
			getActivity().finish();
			return true;
		case R.id.menu_item_share_action_provider_action_bar:
			sharePhoto();
			return true;
		case R.id.menu_item_like:
			likePhoto();
			return true;
		case R.id.menu_item_save:
			savePhotoLocally();
			return true;
		case R.id.menu_item_set_wallpaper:
			WallpaperManager wm = WallpaperManager.getInstance(getActivity());
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(getShareImageFile(null));
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
		case R.id.menu_item_detail:
			Intent i = new Intent(getActivity(), PhotoDetailActivity.class);
			IPhotosProvider dp = ((ImageDetailActivity) getActivity()).mPhotosProvider;
			i.putExtra(ImageDetailActivity.DP_KEY, dp);
			i.putExtra(ImageDetailActivity.LARGE_IMAGE_POSITION, mCurrentPos);
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void sharePhoto() {
		// delete previous share files
		File root = new File(Environment.getExternalStorageDirectory(),
				IConstants.SD_CARD_FOLDER_NAME);
		if (root.exists())
			for (File f : root.listFiles()) {
				if (f.getName().startsWith(
						IConstants.SHARE_INTENT_TMP_FILE_PREFIX)) {
					if (f.delete()) {
						Log.d(TAG, "file deleted: " + f.getName()); //$NON-NLS-1$
					}
				}
			}

		// share this one
		StringBuilder sb = new StringBuilder();
		sb.append(IConstants.SHARE_INTENT_TMP_FILE_PREFIX);
		sb.append(String.valueOf(Math.random()));
		sb.append(".png"); //$NON-NLS-1$
		mCurrentShareIntentFileName = sb.toString();
		saveBitmapToShare(mLoadedBitmap, mCurrentShareIntentFileName);
		Intent shareIntent = createShareIntent(mCurrentShareIntentFileName);
		getActivity().startActivity(shareIntent);
	}

	/**
	 * Saves the photo locally.
	 */
	private void savePhotoLocally() {
		File root = new File(Environment.getExternalStorageDirectory(),
				IConstants.SD_CARD_FOLDER_NAME);
		StringBuilder sb = new StringBuilder();
		sb.append(mPhoto.getMediaSource().toString());
		sb.append("_").append(mPhoto.getId()); //$NON-NLS-1$
		sb.append(".png"); //$NON-NLS-1$
		File saveFile = new File(root, sb.toString());
		if (!saveFile.exists()) {
			ImageUtils.saveImageToFile(saveFile, mLoadedBitmap);
			String msg = getString(R.string.msg_photo_saved_locally);
			msg = String.format(msg, sb.toString());
			Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getActivity(),
					getString(R.string.msg_photo_save_exists),
					Toast.LENGTH_SHORT).show();
		}
	}

	private File getShareImageFile(String fileName) {
		File root = new File(Environment.getExternalStorageDirectory(),
				IConstants.SD_CARD_FOLDER_NAME);
		if (fileName == null) {
			fileName = IConstants.SHARE_TEMP_FILE_NAME;
		}
		File saveFile = new File(root, fileName);
		return saveFile;
	}

	private Intent createShareIntent(String filename) {
		File shareFile = getShareImageFile(filename);
		Uri uri = Uri.fromFile(shareFile);

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
		if (mLoadedBitmap != null) {
			saveBitmapToShare(mLoadedBitmap, null);
			return true;
		}
		return false;
	}

	private void saveBitmapToShare(Bitmap bitmap, String filename) {
		File bsRoot = new File(Environment.getExternalStorageDirectory(),
				IConstants.SD_CARD_FOLDER_NAME);
		if (!bsRoot.exists() && !bsRoot.mkdir()) {
			return;
		}
		if (filename == null) {
			filename = IConstants.SHARE_TEMP_FILE_NAME;
		}
		File shareFile = new File(bsRoot, filename);
		if (shareFile.exists()) {
			shareFile.delete();
		}

		ImageUtils.saveImageToFile(shareFile, bitmap);
	}
}
