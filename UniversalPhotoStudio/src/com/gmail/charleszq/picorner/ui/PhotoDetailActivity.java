/**
 * 
 */
package com.gmail.charleszq.picorner.ui;

import java.io.File;
import java.io.FileInputStream;

import android.app.ProgressDialog;
import android.content.Intent;
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

import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.dp.IPhotosProvider;
import com.gmail.charleszq.picorner.model.Author;
import com.gmail.charleszq.picorner.model.MediaObject;
import com.gmail.charleszq.picorner.model.MediaSourceType;
import com.gmail.charleszq.picorner.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.picorner.task.ig.InstagramCheckRelationshipTask;
import com.gmail.charleszq.picorner.task.ig.InstagramFollowUserTask;
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

	/**
	 * 0: don't know yet; 1: following; 2: not following
	 */
	private int mIsFollowing = 0;

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
		if (mCurrentPhoto.getMediaSource() == MediaSourceType.INSTAGRAM) {
			checkRelationship();
		}
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void checkRelationship() {
		PicornerApplication app = (PicornerApplication) getApplication();
		if (app.getInstagramUserId() == null) {
			// not signed in
			return;
		} else {
			InstagramCheckRelationshipTask task = new InstagramCheckRelationshipTask(
					this);
			task.addTaskDoneListener(new IGeneralTaskDoneListener<Boolean>() {

				@Override
				public void onTaskDone(Boolean result) {
					mIsFollowing = result ? 1 : 2;
					invalidateOptionsMenu();
				}
			});
			task.execute(mCurrentPhoto.getAuthor().getUserId());
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_ig_follow, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		PicornerApplication app = (PicornerApplication) getApplication();
		// this menu item can only be visible if the photo is instagram and I've
		// signed in.
		MenuItem followItem = menu.findItem(R.id.menu_item_follow);
		if (!MediaSourceType.INSTAGRAM.equals(mCurrentPhoto.getMediaSource())) {
			followItem.setVisible(false);
		} else {
			followItem.setVisible(app.getInstagramUserId() != null
					&& !app.isMyOwnPhoto(mCurrentPhoto));
		}

		followItem.setEnabled(mIsFollowing != 0);
		if (mIsFollowing == 1) {
			followItem.setTitle(getString(R.string.menu_item_ig_unfollow_user));
		} else {
			followItem.setTitle(getString(R.string.menu_item_ig_follow_user));
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.menu_item_follow:
			final ProgressDialog dialog1 = ProgressDialog.show(this, "", //$NON-NLS-1$
					getString(R.string.msg_working));
			dialog1.setCanceledOnTouchOutside(true);
			IGeneralTaskDoneListener<Boolean> relationshipListener = new IGeneralTaskDoneListener<Boolean>() {

				@Override
				public void onTaskDone(Boolean result) {
					if (dialog1 != null && dialog1.isShowing()) {
						dialog1.dismiss();
					}
					if (result) {
						mIsFollowing = mIsFollowing == 1 ? 2 : 1;
						invalidateOptionsMenu();
					} else {
						Toast.makeText(
								PhotoDetailActivity.this,
								getString(R.string.msg_ig_chg_relationship_failed),
								Toast.LENGTH_SHORT).show();
					}
				}
			};
			InstagramFollowUserTask followTask = new InstagramFollowUserTask(
					this);
			followTask.addTaskDoneListener(relationshipListener);
			followTask.execute(
					mCurrentPhoto.getAuthor().getUserId(),
					mIsFollowing == 1 ? Boolean.FALSE.toString() : Boolean.TRUE
							.toString());
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
