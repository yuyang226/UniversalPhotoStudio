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
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.service.dreams.DreamService;
import android.util.Log;
import android.widget.ImageView;

import com.gmail.charleszq.picorner.BuildConfig;
import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.model.MediaObject;
import com.gmail.charleszq.picorner.model.MediaObjectCollection;
import com.gmail.charleszq.picorner.service.IPhotoService;
import com.gmail.charleszq.picorner.service.flickr.FlickrInterestingPhotosService;
import com.gmail.charleszq.picorner.utils.IConstants;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * @author charleszq
 * 
 */
@TargetApi(17)
public class PicornerDaydream extends DreamService {

	private static final String	TAG							= PicornerDaydream.class
																	.getSimpleName();
	private List<String>		mPhotoUrls;
	private DisplayImageOptions	mImageDisplayOption			= null;
	private ImageLoader			mImageLoader;
	private ImageView			mImageView1, mImageView2;
	private int					mCurrentShowingPhotoIndex	= 0;
	private AnimatorSet			mAnimatorSet;
	private ImageView			mFadeView, mInView;

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
				.showStubImage(R.drawable.empty_photo).cacheInMemory()
				.cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565)
				.imageScaleType(ImageScaleType.EXACTLY).build();
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
		mPhotoUrls = new ArrayList<String>();

		if (mPhotoUrls.isEmpty()) {
			AsyncTask<Void, Integer, List<String>> task = new AsyncTask<Void, Integer, List<String>>() {
				@Override
				protected List<String> doInBackground(Void... params) {
					List<String> urls = new ArrayList<String>();
					File[] files = PicornerDaydream.this.getFilesDir()
							.listFiles();
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
						MediaObjectCollection col = null;
						IPhotoService ps = new FlickrInterestingPhotosService();
						try {
							col = ps.getPhotos(IConstants.DEF_500PX_PAGE_SIZE,
									0);
							for( MediaObject photo : col.getPhotos()) {
								urls.add(photo.getLargeUrl());
							}
						} catch (Exception e) {
							if (BuildConfig.DEBUG) {
								Log.e(TAG,
										"Unable to get the photos for daydream: " + e.getMessage()); //$NON-NLS-1$
							}
						}
					}
					return urls;
				}

				@Override
				protected void onPostExecute(List<String> result) {
					if (result != null) {
						mPhotoUrls.addAll(result);
						start();
					}
				}
			};
			task.execute();
		}
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
}
