/**
 * 
 */
package com.gmail.charleszq.picorner.ui;

import java.io.File;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.dp.IPhotosProvider;
import com.gmail.charleszq.picorner.model.Author;
import com.gmail.charleszq.picorner.model.GeoLocation;
import com.gmail.charleszq.picorner.model.MediaObject;
import com.gmail.charleszq.picorner.model.MediaSourceType;
import com.gmail.charleszq.picorner.msg.Message;
import com.gmail.charleszq.picorner.msg.MessageBus;
import com.gmail.charleszq.picorner.offline.OfflineControlFileUtil;
import com.gmail.charleszq.picorner.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.picorner.task.flickr.FetchGeoLocationTask;
import com.gmail.charleszq.picorner.ui.helper.PhotoDetailViewPagerAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.viewpagerindicator.TitlePageIndicator;

/**
 * Represents the activity to show all detail information of a photo.
 * 
 * @author charles(charleszq@gmail.com)
 * 
 */
public class PhotoDetailActivity extends FragmentActivity {

	private static final String TAG = PhotoDetailActivity.class.getSimpleName();

	private ViewPager mViewPager;
	private TitlePageIndicator mIndicator;
	private PhotoDetailViewPagerAdapter mAdapter;
	private ImageView mImageView;

	private int mCurrentPos;
	private MediaObject mCurrentPhoto;

	private DisplayImageOptions mImageDisplayOption;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.photo_detail_activity);

		mCurrentPos = getIntent().getIntExtra(
				ImageDetailActivity.LARGE_IMAGE_POSITION, -1);
		IPhotosProvider dp = (IPhotosProvider) getIntent()
				.getSerializableExtra(ImageDetailActivity.DP_KEY);
		mCurrentPhoto = dp.getMediaObject(mCurrentPos);

		mImageView = (ImageView) findViewById(R.id.imageThumb);
		mViewPager = (ViewPager) findViewById(R.id.pager_photo_detail);
		mAdapter = new PhotoDetailViewPagerAdapter(getSupportFragmentManager(),
				mCurrentPhoto, this);
		mViewPager.setAdapter(mAdapter);
		mIndicator = (TitlePageIndicator) findViewById(R.id.indicator_photo_detail);
		mIndicator.setViewPager(mViewPager);

		loadImage();
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void loadImage() {
		ImageLoader loader = ImageLoader.getInstance();
		if (mImageDisplayOption == null)
			mImageDisplayOption = new DisplayImageOptions.Builder()
					.cacheInMemory().cacheOnDisc()
					.bitmapConfig(Bitmap.Config.RGB_565)
					.imageScaleType(ImageScaleType.EXACTLY).build();
		
		String filename = OfflineControlFileUtil.getOfflinePhotoFileName(mCurrentPhoto);
		File offlineFile = this.getFileStreamPath(filename);
		String url = mCurrentPhoto.getLargeUrl();
		if( offlineFile.exists() ) {
			url = Uri.fromFile(offlineFile).toString();
		}
		loader.displayImage(url, mImageView,
				mImageDisplayOption);
	}

	/**
	 * Called by inside page to check, for example, if the photo has location
	 * information, then we will show the map page.
	 */
	public void notifyDataChanged() {
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * Called by inside fragments to show photos of the given
	 * <code>author</code>.
	 * 
	 * @param author
	 */
	void showUserPhotos(Author author) {

		boolean canClick = canClickUserAvator(author);
		if (!canClick) {
			return;
		}

		Intent i = new Intent(this, UserPhotoListActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.putExtra(UserPhotoListActivity.MD_TYPE_KEY, mCurrentPhoto
				.getMediaSource().ordinal());
		i.putExtra(UserPhotoListActivity.USER_KEY, author);
		startActivity(i);
	}

	/**
	 * Before trying to show photos of a given user, check this.
	 * 
	 * @return
	 */
	private boolean canClickUserAvator(Author author) {
		boolean result = true;

		PicornerApplication app = (PicornerApplication) getApplication();
		switch (mCurrentPhoto.getMediaSource()) {
		case INSTAGRAM:
			if (app.getInstagramUserId() == null) {
				Toast.makeText(this, getString(R.string.pls_sing_in_first),
						Toast.LENGTH_SHORT).show();
				result = false;
			} else {
				if (app.getInstagramUserId().equals(author.getUserId())) {
					Toast.makeText(this,
							getString(R.string.msg_your_own_photo),
							Toast.LENGTH_SHORT).show();
					result = false;
				}
			}
			break;
		case FLICKR:
			if (app.getFlickrUserId() == null) {
				Toast.makeText(this, getString(R.string.pls_sing_in_first),
						Toast.LENGTH_SHORT).show();
				result = false;
			} else {
				if (app.getFlickrUserId().equals(author.getUserId())) {
					Toast.makeText(this,
							getString(R.string.msg_your_own_photo),
							Toast.LENGTH_SHORT).show();
					result = false;
				}
			}
			break;
		case PX500:
			break;
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		// for flickr photos, some api calls just cannot return the geo
		// information,
		// then check if there is geo information
		if (mCurrentPhoto == null
				|| !MediaSourceType.FLICKR.equals(mCurrentPhoto
						.getMediaSource())) {
			return;
		}

		GeoLocation loc = mCurrentPhoto.getLocation();
		if (loc != null) {
			return;
		}

		FetchGeoLocationTask task = new FetchGeoLocationTask();
		task.addTaskDoneListener(new IGeneralTaskDoneListener<GeoLocation>() {
			@Override
			public void onTaskDone(GeoLocation result) {
				onFlickrGeoInformationFetch(result);
			}
		});
		Log.d(TAG, "call server to fetch geo information for flickr photo."); //$NON-NLS-1$
		task.execute(mCurrentPhoto.getId());

	}

	/**
	 * After geo location fetched for flikcr photos.
	 * 
	 * @param result
	 */
	private void onFlickrGeoInformationFetch(GeoLocation result) {
		if (result == null) {
			return;
		}
		mCurrentPhoto.setLocation(result);
		notifyDataChanged();

		// broadcast the message.
		Message msg = new Message(Message.GEO_INFO_FETCHED,
				mCurrentPhoto.getMediaSource(), mCurrentPhoto.getId(), result);
		MessageBus.broadcastMessage(msg);
	}

}
