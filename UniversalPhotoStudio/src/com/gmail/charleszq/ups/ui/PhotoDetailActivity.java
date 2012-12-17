/**
 * 
 */
package com.gmail.charleszq.ups.ui;

import java.io.File;
import java.io.FileInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;

import com.gmail.charleszq.ups.R;
import com.gmail.charleszq.ups.UPSApplication;
import com.gmail.charleszq.ups.model.MediaObject;
import com.gmail.charleszq.ups.model.MediaSourceType;
import com.gmail.charleszq.ups.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.ups.task.flickr.CheckUserLikePhotoTask;
import com.gmail.charleszq.ups.ui.adapter.PhotoDetailViewPagerAdapter;
import com.gmail.charleszq.ups.utils.IConstants;
import com.viewpagerindicator.TitlePageIndicator;

/**
 * Represents the activity to show all detail information of a photo.
 * 
 * @author charles(charleszq@gmail.com)
 * 
 */
public class PhotoDetailActivity extends FragmentActivity {

	private static Logger logger = LoggerFactory
			.getLogger(PhotoDetailActivity.class);

	private ViewPager mViewPager;
	private TitlePageIndicator mIndicator;
	private PhotoDetailViewPagerAdapter mAdapter;
	private ImageView mImageView;
	private ImageView mImageFavOrNot;

	private int mCurrentPos;
	private MediaObject mCurrentPhoto;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.photo_detail_activity);

		mCurrentPos = getIntent().getIntExtra(ImageDetailActivity.EXTRA_IMAGE,
				-1);
		UPSApplication app = (UPSApplication) getApplication();
		mCurrentPhoto = app.getPhotosProvider().getMediaObject(mCurrentPos);

		mImageView = (ImageView) findViewById(R.id.imageThumb);
		mImageFavOrNot = (ImageView) findViewById(R.id.img_fav_or_not);
		mViewPager = (ViewPager) findViewById(R.id.pager_photo_detail);
		mAdapter = new PhotoDetailViewPagerAdapter(getSupportFragmentManager(),
				mCurrentPhoto, this);
		mViewPager.setAdapter(mAdapter);
		mIndicator = (TitlePageIndicator) findViewById(R.id.indicator_photo_detail);
		mIndicator.setViewPager(mViewPager);

		getActionBar().hide();

		loadImage();
		checkUserLikeOrNot();
	}

	private void loadImage() {
		File bsRoot = new File(Environment.getExternalStorageDirectory(),
				IConstants.SD_CARD_FOLDER_NAME);
		if (!bsRoot.exists() && !bsRoot.mkdir()) {
			return;
		}
		File shareFile = new File(bsRoot, IConstants.SHARE_TEMP_FILE_NAME);
		FileInputStream fis;
		try {
			fis = new FileInputStream(shareFile);
			Bitmap bmp = BitmapFactory.decodeFileDescriptor(fis.getFD());
			mImageView.setImageBitmap(bmp);
		} catch (Exception e) {
		}
	}

	private void checkUserLikeOrNot() {
		if (mCurrentPhoto.getMediaSource() == MediaSourceType.INSTAGRAM) {
			logger.debug("Do I like this photo? " + mCurrentPhoto.isUserLiked()); //$NON-NLS-1$
			if (mCurrentPhoto.isUserLiked()) {
				mImageFavOrNot.setImageResource(R.drawable.ic_fav_yes);
			} else {
				mImageFavOrNot.setImageResource(R.drawable.ic_fav_no);
			}
			return;
		}
		CheckUserLikePhotoTask task = new CheckUserLikePhotoTask(this);
		task.addTaskDoneListener(new IGeneralTaskDoneListener<Boolean>() {

			@Override
			public void onTaskDone(Boolean result) {
				mCurrentPhoto.setUserLiked(result);
				logger.debug("Do I like this photo? " + result.toString()); //$NON-NLS-1$
				if (result) {
					mImageFavOrNot.setImageResource(R.drawable.ic_fav_yes);
				} else {
					mImageFavOrNot.setImageResource(R.drawable.ic_fav_no);
				}
			}
		});
		task.execute(mCurrentPhoto.getId(), mCurrentPhoto.getSecret());
	}

}
