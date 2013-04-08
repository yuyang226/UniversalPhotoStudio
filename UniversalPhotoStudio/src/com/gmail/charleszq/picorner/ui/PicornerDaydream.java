/**
 * 
 */
package com.gmail.charleszq.picorner.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.service.dreams.DreamService;
import android.util.Log;
import android.widget.ImageView;

import com.gmail.charleszq.picorner.BuildConfig;
import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.model.MediaObject;
import com.gmail.charleszq.picorner.model.MediaObjectCollection;
import com.gmail.charleszq.picorner.service.IPhotoService;
import com.gmail.charleszq.picorner.service.flickr.FlickrInterestingPhotosService;
import com.gmail.charleszq.picorner.service.ig.InstagramPopularsService;
import com.gmail.charleszq.picorner.service.px500.Px500EditorsPhotosService;
import com.gmail.charleszq.picorner.service.px500.Px500FreshTodayPhotosService;
import com.gmail.charleszq.picorner.service.px500.Px500PopularPhotosService;
import com.gmail.charleszq.picorner.service.px500.Px500UpcomingPhotosService;
import com.gmail.charleszq.picorner.task.AbstractGeneralTask;
import com.gmail.charleszq.picorner.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.picorner.utils.IConstants;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * @author charleszq@gmail.com
 * 
 */
@TargetApi(17)
public class PicornerDaydream extends DreamService {

	private List<String> mPhotoUrls;
	private DisplayImageOptions mImageDisplayOption = null;
	private ImageLoader mImageLoader;
	private ImageView mImageView1, mImageView2;
	private int mCurrentShowingPhotoIndex = 0;
	private AnimatorSet mAnimatorSet;
	private ImageView mFadeView, mInView;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.service.dreams.DreamService#onDreamingStarted()
	 */
	@Override
	public void onDreamingStarted() {
		super.onDreamingStarted();
		preparePhotos();
		mImageDisplayOption = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.ic_launcher).cacheInMemory()
				.cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565)
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
		mImageLoader = ImageLoader.getInstance();
	}

	private void start() {
		if (mPhotoUrls.isEmpty())
			return;
		int secondaryIndex = mCurrentShowingPhotoIndex + 1;
		if (secondaryIndex >= mPhotoUrls.size()) {
			secondaryIndex = 0;
		}

		mImageLoader.displayImage(mPhotoUrls.get(mCurrentShowingPhotoIndex),
				mImageView1, mImageDisplayOption);
		mImageLoader.displayImage(mPhotoUrls.get(secondaryIndex), mImageView2,
				mImageDisplayOption);
		startAnimation();
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		this.setContentView(R.layout.day_dream);
		mImageView1 = (ImageView) findViewById(R.id.img_dream_1);
		mImageView2 = (ImageView) findViewById(R.id.img_dream_2);
		mFadeView = mImageView1;
		mInView = mImageView2;
	}

	@Override
	public void onDreamingStopped() {
		mImageDisplayOption = null;
		mPhotoUrls = null;
		if (mAnimatorSet != null)
			mAnimatorSet.cancel();
		super.onDreamingStopped();
	}

	private void preparePhotos() {

		PhotoTask task = new PhotoTask(this);
		task.addTaskDoneListener(new IGeneralTaskDoneListener<List<String>>() {
			@Override
			public void onTaskDone(List<String> result) {
				if (result != null) {
					mPhotoUrls = new ArrayList<String>();
					mPhotoUrls.addAll(result);
					start();
				}
			}
		});
		task.execute();
	}

	private void startAnimation() {

		ObjectAnimator o1 = ObjectAnimator
				.ofFloat(mFadeView, "alpha", 1.0f, 0f).setDuration(10000); //$NON-NLS-1$
		ObjectAnimator o2 = ObjectAnimator
				.ofFloat(mInView, "alpha", 0f, 1f).setDuration(10000); //$NON-NLS-1$
		if (mAnimatorSet == null) {
			mAnimatorSet = new AnimatorSet();
			mAnimatorSet.addListener(new AnimatorListener() {

				@Override
				public void onAnimationStart(Animator animation) {
					int secondaryIndex = mCurrentShowingPhotoIndex + 1;
					if (secondaryIndex >= mPhotoUrls.size()) {
						secondaryIndex = 0;
					}
					mImageLoader.displayImage(mPhotoUrls.get(secondaryIndex),
							mInView, mImageDisplayOption);
				}

				@Override
				public void onAnimationEnd(Animator animation) {
					mCurrentShowingPhotoIndex++;
					if (mCurrentShowingPhotoIndex >= mPhotoUrls.size()) {
						mCurrentShowingPhotoIndex = 0;
					}
					mImageLoader.displayImage(
							mPhotoUrls.get(mCurrentShowingPhotoIndex),
							mFadeView, mImageDisplayOption);
					mFadeView = mFadeView == mImageView1 ? mImageView2
							: mImageView1;
					mInView = mInView == mImageView1 ? mImageView2
							: mImageView1;
					startAnimation();
				}

				@Override
				public void onAnimationCancel(Animator animation) {

				}

				@Override
				public void onAnimationRepeat(Animator animation) {

				}
			});
		}
		mAnimatorSet.playTogether(o1, o2);
		mAnimatorSet.start();
	}

	private static class PhotoTask extends
			AbstractGeneralTask<Void, Integer, List<String>> {

		private Context mContext;

		PhotoTask(Context ctx) {
			this.mContext = ctx;
		}

		private int getSecondaryPhotoSource() {
			SharedPreferences sp = mContext.getSharedPreferences(
					IConstants.DEF_PREF_NAME, Context.MODE_APPEND);
			String src = sp.getString(IConstants.PREF_DEFAULT_PHOTO_LIST,
					"1"); //$NON-NLS-1$
			return Integer.parseInt(src);
		}

		private IPhotoService getNetworkPhotoSourceService(int which) {
			switch (which) {
			case 1:
				return new Px500PopularPhotosService();
			case 2:
				return new Px500EditorsPhotosService();
			case 3:
				return new Px500UpcomingPhotosService();
			case 4:
				return new Px500FreshTodayPhotosService();
			case 5:
				return new FlickrInterestingPhotosService();
			default:
				return new InstagramPopularsService();
			}
		}

		private int getPageSize(int which) {
			switch (which) {
			case 5:
				return IConstants.DEF_SERVICE_PAGE_SIZE;
			case 6:
				return IConstants.DEF_IG_PAGE_SIZE;
			default:
				return IConstants.DEF_500PX_PAGE_SIZE;
			}
		}

		@Override
		protected List<String> doInBackground(Void... params) {
			List<String> urls = new ArrayList<String>();
			File[] files = mContext.getFilesDir().listFiles();
			for (File f : files) {
				String name = f.getName();
				if (name.contains(".png")) { //$NON-NLS-1$
					String url = Uri.fromFile(f).toString();
					urls.add(url);
					if (BuildConfig.DEBUG) {
						Log.d(TAG, "foudn offline photo: " + url); //$NON-NLS-1$
					}
				}
			}

			if (urls.isEmpty()) {
				return getNetworkPhotos(getSecondaryPhotoSource());
			} else
				return urls;

		}

		private List<String> getNetworkPhotos(int which) {
			List<String> urls = new ArrayList<String>();
			MediaObjectCollection col = null;
			IPhotoService ps = getNetworkPhotoSourceService(which);
			try {
				col = ps.getPhotos(getPageSize(which), 0);
				for (MediaObject photo : col.getPhotos()) {
					urls.add(photo.getLargeUrl());
				}
			} catch (Exception e) {
				if (BuildConfig.DEBUG) {
					Log.e(TAG,
							"Unable to get the photos for daydream: " + e.getMessage()); //$NON-NLS-1$
				}
			}
			return urls;
		}
	}
}
