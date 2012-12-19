/**
 * 
 */
package com.gmail.charleszq.ups.ui;

import java.io.File;
import java.io.FileInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.gmail.charleszq.ups.R;
import com.gmail.charleszq.ups.UPSApplication;
import com.gmail.charleszq.ups.model.MediaObject;
import com.gmail.charleszq.ups.model.MediaSourceType;
import com.gmail.charleszq.ups.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.ups.task.flickr.CheckUserLikePhotoTask;
import com.gmail.charleszq.ups.task.flickr.FlickrLikeTask;
import com.gmail.charleszq.ups.task.ig.InstagramLikePhotoTask;
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

	private int mCurrentPos;
	private MediaObject mCurrentPhoto;

	/**
	 * the marker to invalidate the option menu.
	 */
	private boolean mUserLikeThePhoto = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.photo_detail_activity);

		mCurrentPos = getIntent().getIntExtra(ImageDetailActivity.EXTRA_IMAGE,
				-1);
		UPSApplication app = (UPSApplication) getApplication();
		mCurrentPhoto = app.getPhotosProvider().getMediaObject(mCurrentPos);
		mUserLikeThePhoto = mCurrentPhoto.isUserLiked();

		mImageView = (ImageView) findViewById(R.id.imageThumb);
		mViewPager = (ViewPager) findViewById(R.id.pager_photo_detail);
		mAdapter = new PhotoDetailViewPagerAdapter(getSupportFragmentManager(),
				mCurrentPhoto, this);
		mViewPager.setAdapter(mAdapter);
		mIndicator = (TitlePageIndicator) findViewById(R.id.indicator_photo_detail);
		mIndicator.setViewPager(mViewPager);

		loadImage();
		checkUserLikeOrNot();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_photo_detail_2, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem item = menu.findItem(R.id.menu_item_like);
		if (mUserLikeThePhoto) {
			item.setIcon(R.drawable.ic_fav_yes);
		} else {
			item.setIcon(R.drawable.ic_fav_no);
		}

		if (mCurrentPhoto.getMediaSource() == MediaSourceType.PX500) {
			// disable like for px500 at this time.
			item.setVisible(false);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		if (R.id.menu_item_like == item.getItemId()) {

			final ProgressDialog dialog = ProgressDialog.show(this, "", //$NON-NLS-1$
					getString(R.string.msg_working));
			dialog.setCanceledOnTouchOutside(true);
			IGeneralTaskDoneListener<Boolean> lis = new IGeneralTaskDoneListener<Boolean>() {
				@Override
				public void onTaskDone(Boolean result) {
					if (dialog != null && dialog.isShowing()) {
						dialog.dismiss();
					}
					if (result) {
						mUserLikeThePhoto = !mUserLikeThePhoto;
						item.setIcon(mUserLikeThePhoto ? R.drawable.ic_fav_yes
								: R.drawable.ic_fav_no);
					} else {
						Toast.makeText(PhotoDetailActivity.this,
								getString(R.string.msg_like_photo_fail),
								Toast.LENGTH_SHORT).show();
					}
				}
			};

			String likeActionString = Boolean.toString(!mUserLikeThePhoto);
			switch (this.mCurrentPhoto.getMediaSource()) {
			case FLICKR:
				FlickrLikeTask ftask = new FlickrLikeTask(this, lis);
				ftask.execute(mCurrentPhoto.getId(), likeActionString);
				break;
			case INSTAGRAM:
				InstagramLikePhotoTask igtask = new InstagramLikePhotoTask(
						this, lis);
				igtask.execute(mCurrentPhoto.getId(), likeActionString);
				break;
			}
		}
		return super.onOptionsItemSelected(item);
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
			mUserLikeThePhoto = mCurrentPhoto.isUserLiked();
			return;
		}
		CheckUserLikePhotoTask task = new CheckUserLikePhotoTask(this);
		task.addTaskDoneListener(new IGeneralTaskDoneListener<Boolean>() {

			@Override
			public void onTaskDone(Boolean result) {
				mCurrentPhoto.setUserLiked(result);
				logger.debug("Do I like this photo? " + result.toString()); //$NON-NLS-1$
				mUserLikeThePhoto = mCurrentPhoto.isUserLiked();
				PhotoDetailActivity.this.invalidateOptionsMenu();
			}
		});
		task.execute(mCurrentPhoto.getId(), mCurrentPhoto.getSecret());
	}

}
