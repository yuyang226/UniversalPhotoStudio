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
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.gmail.charleszq.picorner.BuildConfig;
import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.SPUtil;
import com.gmail.charleszq.picorner.dp.IPhotosProvider;
import com.gmail.charleszq.picorner.model.Author;
import com.gmail.charleszq.picorner.model.MediaObject;
import com.gmail.charleszq.picorner.model.MediaSourceType;
import com.gmail.charleszq.picorner.msg.Message;
import com.gmail.charleszq.picorner.msg.MessageBus;
import com.gmail.charleszq.picorner.offline.OfflineControlFileUtil;
import com.gmail.charleszq.picorner.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.picorner.task.flickr.CheckUserLikePhotoTask;
import com.gmail.charleszq.picorner.task.flickr.FlickrLikeTask;
import com.gmail.charleszq.picorner.task.ig.InstagramLikePhotoTask;
import com.gmail.charleszq.picorner.task.px500.PxLikePhotoTask;
import com.gmail.charleszq.picorner.task.px500.PxVotePhotoTask;
import com.gmail.charleszq.picorner.ui.ImageDetailActivity.IActionBarVisibleListener;
import com.gmail.charleszq.picorner.utils.IConstants;
import com.gmail.charleszq.picorner.utils.ImageUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * This fragment will populate the children of the ViewPager from
 * {@link ImageDetailActivity}.
 */
public class ImageDetailFragment extends Fragment implements
		OnShareTargetSelectedListener {

	private static final String IMAGE_DATA_EXTRA = "extra_image_data"; //$NON-NLS-1$
	private static final String MEDIA_OBJ_POS = "media_object"; //$NON-NLS-1$
	private static final String TAG = ImageDetailFragment.class.getSimpleName();

	// ui controls.
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
	 * The current file name to store image so the share action can get the
	 * image from it.
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
			if (mIsOfflineEnabled) {
				OfflineViewSavePhotoTask task = new OfflineViewSavePhotoTask(
						getActivity(), loadedImage, mPhoto);
				task.execute();
			}
		}

		@Override
		public void onLoadingCancelled() {
			mLoadedBitmap = null;
		}
	};

	private static class OfflineViewSavePhotoTask extends
			AsyncTask<Void, Integer, Void> {

		private Context mContext;
		private Bitmap mBitmap;
		private MediaObject mPhoto;

		OfflineViewSavePhotoTask(Context context, Bitmap bitmap,
				MediaObject photo) {
			this.mContext = context;
			this.mBitmap = bitmap;
			this.mPhoto = photo;
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (mContext != null) {
				String filename = OfflineControlFileUtil
						.getOfflinePhotoFileName(mPhoto);
				File file = mContext.getFileStreamPath(filename);
				if (file.exists())
					return null;
				OfflineControlFileUtil.saveBitmapForOfflineView(mContext,
						mBitmap, mPhoto);
				if (BuildConfig.DEBUG) {
					Log.d(ImageDetailFragment.class.getSimpleName(),
							"Photo saved for offline view."); //$NON-NLS-1$
				}
			}
			return null;
		}

	}

	private IActionBarVisibleListener mActionBarListener = new IActionBarVisibleListener() {

		@Override
		public void onActionBarShown(boolean show) {
			ImageDetailFragment.this.onActionBarShown(show);

		}
	};

	/**
	 * If user likes this photo or not.
	 */
	private boolean mUserLikeThePhoto = false;

	/**
	 * offline enable?
	 */
	private boolean mIsOfflineEnabled = false;

	/**
	 * Factory method to generate a new instance of the fragment given an image
	 * number.
	 * 
	 * @param imageUrl
	 *            The image url to load
	 * @return A new instance of ImageDetailFragment with imageNum extras
	 */
	public static ImageDetailFragment newInstance(String imageUrl,
			IPhotosProvider dp, int pos, boolean offlineEnabled) {
		final ImageDetailFragment f = new ImageDetailFragment();

		final Bundle args = new Bundle();
		args.putString(IMAGE_DATA_EXTRA, imageUrl);
		args.putInt(MEDIA_OBJ_POS, pos);
		args.putSerializable(ImageDetailActivity.DP_KEY, dp);
		args.putBoolean(ImageDetailActivity.OFFLINE_COMMAND_KEY, offlineEnabled);
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
				.showStubImage(R.drawable.empty_photo).cacheOnDisc()
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		mIsOfflineEnabled = getArguments() != null ? (getArguments()
				.getBoolean(ImageDetailActivity.OFFLINE_COMMAND_KEY, false))
				: false;
		mImageUrl = getArguments() != null ? getArguments().getString(
				IMAGE_DATA_EXTRA) : null;
		int pos = (getArguments() != null ? getArguments()
				.getInt(MEDIA_OBJ_POS) : -1);
		IPhotosProvider dp = (IPhotosProvider) (getArguments() != null ? getArguments()
				.getSerializable(ImageDetailActivity.DP_KEY) : null);
		mCurrentPos = pos;
		ImageDetailActivity act = (ImageDetailActivity) getActivity();
		mPhoto = dp.getMediaObject(pos);
		if( BuildConfig.DEBUG)
			Log.d(TAG, "large url: " + mPhoto.getLargeUrl()); //$NON-NLS-1$

		setHasOptionsMenu(true);
		act.addActionBarListener(mActionBarListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		checkUserLikeOrNot();
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
		String photoTitle = mPhoto.getTitle();
		if (photoTitle == null) {
			photoTitle = ""; //$NON-NLS-1$
		} else {
			if (photoTitle.length() > IConstants.MAX_PHOTO_TITLE_LEN) {
				photoTitle = photoTitle.substring(0,
						IConstants.MAX_PHOTO_TITLE_LEN) + "..."; //$NON-NLS-1$
			}
		}
		mPhotoTitle.setText(photoTitle);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();
		Activity act = getActivity();
		// set the actionbar title.
		StringBuilder sb = new StringBuilder();
		switch (mPhoto.getMediaSource()) {
		case FLICKR:
			sb.append(getString(R.string.menu_header_flickr));
			break;
		case INSTAGRAM:
			sb.append(getString(R.string.menu_header_ig));
			break;
		case PX500:
			sb.append(getString(R.string.menu_header_px500));
			break;
		}
		sb.append(" ").append(getString(R.string.msg_photo)); //$NON-NLS-1$
		act.getActionBar().setSubtitle(sb.toString().toLowerCase());
	}

	private boolean likePhoto() {
		PicornerApplication app = (PicornerApplication) getActivity()
				.getApplication();
		switch (mPhoto.getMediaSource()) {
		case PX500:
			if (SPUtil.getPx500OauthTokenSecret(getActivity()) == null) {
				Toast.makeText(getActivity(),
						getString(R.string.pls_sing_in_first),
						Toast.LENGTH_SHORT).show();
				return false;
			}
			break;
		case FLICKR:
			if (!SPUtil.isFlickrAuthed(getActivity())) {
				Toast.makeText(getActivity(),
						getString(R.string.pls_sing_in_first),
						Toast.LENGTH_SHORT).show();
				return false;
			}
			break;
		case INSTAGRAM:
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

		IGeneralTaskDoneListener<Boolean> lis = new IGeneralTaskDoneListener<Boolean>() {
			@Override
			public void onTaskDone(Boolean result) {
				if (result) {
					mUserLikeThePhoto = !mUserLikeThePhoto;
					mPhoto.setUserLiked(mUserLikeThePhoto);
					Activity act = ImageDetailFragment.this.getActivity();
					if (act != null) {
						act.invalidateOptionsMenu();
					}
					// broadcast messages
					Message msg = new Message(Message.LIKE_PHOTO,
							mPhoto.getMediaSource(), mPhoto.getId(),
							mUserLikeThePhoto);
					MessageBus.broadcastMessage(msg);
				} else {
					Toast.makeText(getActivity(),
							getString(R.string.msg_like_photo_fail),
							Toast.LENGTH_SHORT).show();
				}
			}
		};
		String likeActionString = Boolean.toString(!mUserLikeThePhoto);
		switch (this.mPhoto.getMediaSource()) {
		case FLICKR:
			FlickrLikeTask ftask = new FlickrLikeTask(getActivity(), lis);
			ftask.execute(mPhoto.getId(), likeActionString);
			break;
		case INSTAGRAM:
			InstagramLikePhotoTask igtask = new InstagramLikePhotoTask(
					getActivity(), lis);
			igtask.execute(mPhoto.getId(), likeActionString);
			break;
		case PX500:
			PxLikePhotoTask pxTask = new PxLikePhotoTask(getActivity());
			pxTask.addTaskDoneListener(lis);
			pxTask.execute(mPhoto.getId(), likeActionString);
			break;
		}
		return true;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mImageFetcher = ((ImageDetailActivity) getActivity()).getImageFetcher();
		// show the image either from offline cache or from network.
		String filename = OfflineControlFileUtil
				.getOfflinePhotoFileName(mPhoto);
		if (OfflineControlFileUtil.isFileExist(getActivity(), filename)) {
			File f = getActivity().getFileStreamPath(filename);
			Uri uri = Uri.fromFile(f);
			mImageFetcher.displayImage(uri.toString(), mImageView,
					mImageDisplayOptions, mImageLoaderListener);
			if (BuildConfig.DEBUG)
				Log.d(TAG, "Load thumb image from offline cache."); //$NON-NLS-1$
		} else {
			mImageFetcher.displayImage(mImageUrl, mImageView,
					mImageDisplayOptions, mImageLoaderListener);
		}

		// Pass clicks on the ImageView to the parent activity to handle
		if (OnClickListener.class.isInstance(getActivity())) {
			mImageView.setOnClickListener((OnClickListener) getActivity());
			mUserInfoContainer
					.setOnClickListener((OnClickListener) getActivity());
			this.mPhotoTitle
					.setOnClickListener((OnClickListener) getActivity());
			this.mUserName.setOnClickListener((OnClickListener) getActivity());
		}
	}

	@Override
	public void onDestroy() {
		if (mImageView != null) {
			mImageFetcher.cancelDisplayTask(mImageView);
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
		inflater.inflate(R.menu.my_flickr_photo_menus, menu);
		inflater.inflate(R.menu.photo_detail_common, menu);
	}

	private void checkUserLikeOrNot() {

		switch (mPhoto.getMediaSource()) {
		case INSTAGRAM:
		case PX500:
			Log.d(TAG, "Do I like this photo? " + mPhoto.isUserLiked()); //$NON-NLS-1$
			mUserLikeThePhoto = mPhoto.isUserLiked();
			getActivity().invalidateOptionsMenu();
			break;
		case FLICKR:
			if (mPhoto.isUserLiked()) {
				// if it's true, it means we've already checked again the
				// server, so we don't need to check again.
				mUserLikeThePhoto = true;
				getActivity().invalidateOptionsMenu();
			} else {
				CheckUserLikePhotoTask task = new CheckUserLikePhotoTask(
						getActivity());
				task.addTaskDoneListener(new IGeneralTaskDoneListener<Boolean>() {

					@Override
					public void onTaskDone(Boolean result) {
						mPhoto.setUserLiked(result);
						Log.d(TAG, "Do I like this photo? " + result.toString()); //$NON-NLS-1$
						mUserLikeThePhoto = mPhoto.isUserLiked();
						Activity act = ImageDetailFragment.this.getActivity();
						if (act != null) {
							act.invalidateOptionsMenu();
						}
					}
				});
				task.execute(mPhoto.getId(), mPhoto.getSecret());
			}
			break;
		}
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		MenuItem likeItem = menu.findItem(R.id.menu_item_like);
		MenuItem ownerPhotoItem = menu
				.findItem(R.id.menu_item_see_owner_photos);
		MenuItem commentItem = menu.findItem(R.id.menu_item_comment);
		MenuItem mapItem = menu.findItem(R.id.menu_item_view_on_map);
		MenuItem exifItem = menu.findItem(R.id.menu_item_view_exif);

		PicornerApplication app = (PicornerApplication) getActivity()
				.getApplication();

		boolean ismyphoto = app.isMyOwnPhoto(mPhoto);
		ownerPhotoItem.setVisible(!ismyphoto);
		switch (mPhoto.getMediaSource()) {
		case FLICKR:
			menu.setGroupVisible(R.id.group_my_flickr_photo, ismyphoto);
			likeItem.setVisible(!ismyphoto);
			break;
		case PX500:
			menu.setGroupVisible(R.id.group_my_flickr_photo, false);
			likeItem.setVisible(!ismyphoto);
			break;
		case INSTAGRAM:
			commentItem.setVisible(false);
			exifItem.setVisible(false);
			menu.setGroupVisible(R.id.group_my_flickr_photo, false);
			break;
		}

		mapItem.setVisible(mPhoto.getLocation() != null);

		if (mUserLikeThePhoto) {
			likeItem.setIcon(R.drawable.star_big_on);
		} else {
			likeItem.setIcon(R.drawable.ic_menu_star);
		}

		// 500px menu group
		MenuItem voteItem = menu.findItem(R.id.menu_item_vote);
		if (mPhoto.getMediaSource() != MediaSourceType.PX500) {
			voteItem.setVisible(false);
		} else {
			voteItem.setVisible(SPUtil.getPx500OauthToken(getActivity()) != null
					&& !app.isMyOwnPhoto(mPhoto) && !mPhoto.isUserVoted());
		}
	}

	private boolean waitForImageLoaded() {
		if (mLoadedBitmap == null) {
			Toast.makeText(getActivity(), R.string.wait_for_image_loading,
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (mLoadedBitmap != null) {
			saveBitmapToShare(mLoadedBitmap, null);
		}

		switch (item.getItemId()) {
		case android.R.id.home:
			getActivity().finish();
			return true;
		case R.id.menu_item_share_action_provider_action_bar:
			boolean ready = waitForImageLoaded();
			if (ready) {
				sharePhoto();
			}
			return ready;
		case R.id.menu_item_slide_show:
			ImageDetailActivity act = (ImageDetailActivity) getActivity();
			if (act != null) {
				act.startSlideShow(0);
			}
			return true;
		case R.id.menu_item_see_owner_photos:
			Intent i = new Intent(getActivity(), UserPhotoListActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			i.putExtra(UserPhotoListActivity.MD_TYPE_KEY, mPhoto
					.getMediaSource().ordinal());
			i.putExtra(UserPhotoListActivity.USER_KEY, mPhoto.getAuthor());
			startActivity(i);
			return true;
		case R.id.menu_item_like:
			ready = waitForImageLoaded();
			if (ready)
				likePhoto();
			return ready;
		case R.id.menu_item_vote:
			ready = waitForImageLoaded();
			if (ready)
				votePhoto();
			return ready;
		case R.id.menu_item_save:
			ready = waitForImageLoaded();
			if (ready)
				savePhotoLocally();
			return ready;
		case R.id.menu_item_set_wallpaper:
			ready = waitForImageLoaded();
			if (!ready)
				return false;

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
			showPhotoDetailWithPage(null);
			return true;
		case R.id.menu_item_comment:
			showPhotoDetailWithPage(PhotoDetailActivity.COMMENT_PAGE);
			return true;
		case R.id.menu_item_photo_set:
			showPhotoDetailWithPage(PhotoDetailActivity.MY_F_ORG_PHOTO_SET_PAGE);
			return true;
		case R.id.menu_item_add_to_group:
			showPhotoDetailWithPage(PhotoDetailActivity.MY_F_ORG_GROUP_PAGE);
			return true;
		case R.id.menu_item_view_exif:
			showPhotoDetailWithPage(PhotoDetailActivity.EXIF_PAGE);
			return true;
		case R.id.menu_item_view_on_map:
			showPhotoDetailWithPage(PhotoDetailActivity.MAP_PAGE);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showPhotoDetailWithPage(String pageIndex) {
		Intent detailIntent = new Intent(getActivity(),
				PhotoDetailActivity.class);
		IPhotosProvider dp = ((ImageDetailActivity) getActivity()).mPhotosProvider;
		detailIntent.putExtra(ImageDetailActivity.DP_KEY, dp);
		detailIntent.putExtra(ImageDetailActivity.LARGE_IMAGE_POSITION,
				mCurrentPos);
		if (pageIndex != null)
			detailIntent.putExtra(PhotoDetailActivity.DETAIL_PAGE_KEY,
					pageIndex);
		startActivity(detailIntent);
	}

	/**
	 * Votes a 500px photo.
	 */
	private void votePhoto() {
		PxVotePhotoTask task = new PxVotePhotoTask(getActivity());
		task.addTaskDoneListener(new IGeneralTaskDoneListener<Boolean>() {

			@Override
			public void onTaskDone(Boolean result) {
				if (result) {
					Toast.makeText(getActivity(),
							getString(R.string.msg_500px_photo_voted),
							Toast.LENGTH_SHORT).show();
					mPhoto.setUserVoted(true);
					getActivity().invalidateOptionsMenu();

					// broadcast messages
					Message msg = new Message(Message.VOTE_PHOTO, mPhoto
							.getMediaSource(), mPhoto.getId(), true);
					MessageBus.broadcastMessage(msg);
				} else {
					Toast.makeText(getActivity(),
							getString(R.string.msg_500px_photo_vote_failed),
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		task.execute(mPhoto.getId());
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
			msg = String.format(msg, IConstants.SD_CARD_FOLDER_NAME
					+ File.separator + sb.toString());
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
		sb.append(" ").append(IConstants.APP_GL_STORE_URL); //$NON-NLS-1$
		saveToClipboard(sb.toString());

		Intent shareIntent = ShareCompat.IntentBuilder.from(getActivity())
				.setText(sb.toString()).setType("image/*").setStream(uri) //$NON-NLS-1$
				.createChooserIntent();
		shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		return shareIntent;
	}

	private void saveToClipboard(String s) {
		ClipboardManager cm = (ClipboardManager) getActivity()
				.getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData data = ClipData.newPlainText(getString(R.string.app_name), s);
		cm.setPrimaryClip(data);
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
