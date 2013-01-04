/**
 * 
 */
package com.gmail.charleszq.picorner.ui;

import java.io.File;
import java.io.FileInputStream;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.dp.IPhotosProvider;
import com.gmail.charleszq.picorner.model.Author;
import com.gmail.charleszq.picorner.model.MediaObject;
import com.gmail.charleszq.picorner.ui.helper.PhotoDetailViewPagerAdapter;
import com.gmail.charleszq.picorner.utils.IConstants;
import com.viewpagerindicator.TitlePageIndicator;

/**
 * Represents the activity to show all detail information of a photo.
 * 
 * @author charles(charleszq@gmail.com)
 * 
 */
public class PhotoDetailActivity extends FragmentActivity {

	private ViewPager mViewPager;
	private TitlePageIndicator mIndicator;
	private PhotoDetailViewPagerAdapter mAdapter;
	private ImageView mImageView;

	private int mCurrentPos;
	private MediaObject mCurrentPhoto;

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

}
