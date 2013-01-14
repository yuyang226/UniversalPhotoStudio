/**
 * 
 */
package com.gmail.charleszq.picorner.ui.flickr;

import java.io.File;
import java.io.FileInputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.picorner.task.flickr.FetchPhotoPermissionTask;
import com.gmail.charleszq.picorner.task.flickr.FlickrGetPhotoGeneralInfoTask;
import com.gmail.charleszq.picorner.utils.IConstants;
import com.googlecode.flickrjandroid.photos.Permissions;
import com.googlecode.flickrjandroid.photos.Photo;

/**
 * @author charleszq
 * 
 */
public class FlickrPhotoPropertyActivity extends FragmentActivity {

	public static final String PHOTO_ID_KEY = "photo.id"; //$NON-NLS-1$
	public static final String PHOTO_SECRET_KEY = "photo.secret"; //$NON-NLS-1$

	private ImageView mImageView;
	private EditText mEditTitle, mEditDescription;
	private RadioButton mPrivateButton, mPublicButton;
	private CheckBox mFriendButton, mFamilyButton;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flickr_photo_prop_activity);

		// show the image
		mImageView = (ImageView) findViewById(R.id.image_prop_my_f_photo);
		loadImage();

		// find the ui controls
		mEditTitle = (EditText) findViewById(R.id.edit_f_p_title);
		mEditDescription = (EditText) findViewById(R.id.edit_f_p_desc);
		mPrivateButton = (RadioButton) findViewById(R.id.rb_f_p_private);
		mPublicButton = (RadioButton) findViewById(R.id.rb_f_p_public);
		mFriendButton = (CheckBox) findViewById(R.id.cb_f_p_friend);
		mFamilyButton = (CheckBox) findViewById(R.id.cb_f_p_family);

		// get data from intent
		String photoId = getIntent().getStringExtra(PHOTO_ID_KEY);
		String photoSecret = getIntent().getStringExtra(PHOTO_SECRET_KEY);

		// load photo details
		FlickrGetPhotoGeneralInfoTask task = new FlickrGetPhotoGeneralInfoTask();
		task.addTaskDoneListener(new IGeneralTaskDoneListener<Photo>() {

			@Override
			public void onTaskDone(Photo result) {
				onPhotoInformationFetched(result);
			}
		});
		task.execute(photoId, photoSecret);

		// get photo permissions
		FetchPhotoPermissionTask permTask = new FetchPhotoPermissionTask(this);
		permTask.addTaskDoneListener(new IGeneralTaskDoneListener<Permissions>() {

			@Override
			public void onTaskDone(Permissions result) {
				onPhotoPermissionFetched(result);
			}
		});
		permTask.execute(photoId);
	}

	private void onPhotoPermissionFetched(Permissions perm) {
		if (perm != null) {
			mFriendButton.setChecked(perm.isFriendFlag());
			mFamilyButton.setChecked(perm.isFamilyFlag());
			mPrivateButton.setChecked(!perm.isPublicFlag());
			mPublicButton.setChecked(perm.isPublicFlag());
			mFriendButton.setEnabled(mPrivateButton.isChecked());
			mFamilyButton.setEnabled(mPrivateButton.isChecked());
		}
	}

	/**
	 * Binds data to UI controls
	 * 
	 * @param result
	 */
	private void onPhotoInformationFetched(Photo result) {
		if (result != null) {
			mEditTitle.setText(result.getTitle() != null ? result.getTitle()
					: ""); //$NON-NLS-1$
			mEditDescription.setText(result.getDescription() != null ? result
					.getDescription() : ""); //$NON-NLS-1$
		} else {
			// TODO handle this case.
		}
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

}
