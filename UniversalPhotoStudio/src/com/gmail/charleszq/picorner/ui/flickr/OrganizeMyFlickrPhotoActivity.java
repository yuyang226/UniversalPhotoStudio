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
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.utils.IConstants;

/**
 * @author charleszq
 * 
 */
public class OrganizeMyFlickrPhotoActivity extends FragmentActivity {

	private Button mCancelButton, mOkButton;
	private OnClickListener mButtonClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if( v == mCancelButton ) {
				finish();
			} else if( v == mOkButton ) {
				performOk();
			}
		}
	};
	private ImageView mImageView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.org_my_flickr_photo);
		
		mCancelButton = (Button) findViewById(R.id.button_org_my_f_cancel);
		mCancelButton.setOnClickListener(mButtonClickListener);
		mOkButton = (Button) findViewById(R.id.button_org_my_f_ok);
		mOkButton.setOnClickListener(mButtonClickListener);
		
		mImageView = (ImageView) findViewById(R.id.image_org_my_f_photo);
		
		loadImage();
		getActionBar().setDisplayHomeAsUpEnabled(true);
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
	
	

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if( item.getItemId() == android.R.id.home ) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void performOk() {
		
	}

}
