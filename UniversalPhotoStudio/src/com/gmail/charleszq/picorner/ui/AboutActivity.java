/**
 * 
 */
package com.gmail.charleszq.picorner.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;

import com.gmail.charleszq.picorner.utils.IConstants;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class AboutActivity extends FragmentActivity {
	private WebView mWebView;
	private String mFileName;
	private String mFileEncoding;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent i = getIntent();
		mFileName = i.getStringExtra(IConstants.ABOUT_FILE_FRG_ARG_KEY);
		mFileEncoding = i.getStringExtra(IConstants.ABOUT_FILE_ENCODING_KEY);
		if (mFileEncoding == null || mFileEncoding == "") { //$NON-NLS-1$
			mFileEncoding = IConstants.ABOUT_FILE_DEFAULT_ENCODING;
		}
		
		mWebView = new WebView(this);
		mWebView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		setContentView(mWebView);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onStart() {
		super.onStart();
		mWebView.getSettings().setDefaultTextEncodingName(mFileEncoding);
		mWebView.loadUrl(IConstants.ASSET_FOLDER + mFileName);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if( item.getItemId() == android.R.id.home ) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	

}
