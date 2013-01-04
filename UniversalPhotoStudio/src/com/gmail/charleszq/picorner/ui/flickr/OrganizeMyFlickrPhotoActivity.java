/**
 * 
 */
package com.gmail.charleszq.picorner.ui.flickr;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.gmail.charleszq.picorner.R;

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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.org_my_flickr_photo);
		
		mCancelButton = (Button) findViewById(R.id.button_org_my_f_cancel);
		mCancelButton.setOnClickListener(mButtonClickListener);
		mOkButton = (Button) findViewById(R.id.button_org_my_f_ok);
		mOkButton.setOnClickListener(mButtonClickListener);
	}

	private void performOk() {
		
	}

}
