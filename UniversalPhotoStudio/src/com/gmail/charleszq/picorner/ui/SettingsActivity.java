/**
 * 
 */
package com.gmail.charleszq.picorner.ui;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

import com.gmail.charleszq.picorner.R;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class SettingsActivity extends PreferenceActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
