/**
 * 
 */
package com.gmail.charleszq.ups;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class AboutActivity extends FragmentActivity {

	private WebView mWebView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mWebView = new WebView(this);
		mWebView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		setContentView(mWebView);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onStart() {
		super.onStart();
		AssetManager am = getAssets();
		InputStream is = null;
		try {
			String htmlFile = getString(R.string.about_file_name);
			is = am.open(htmlFile);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			int ch = reader.read();
			while (ch != -1) {
				sb.append((char) ch);
				ch = reader.read();
			}
			mWebView.loadDataWithBaseURL(htmlFile, sb.toString(),
					"text/html", "utf-8", null); //$NON-NLS-1$//$NON-NLS-2$
		} catch (IOException e) {
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
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
