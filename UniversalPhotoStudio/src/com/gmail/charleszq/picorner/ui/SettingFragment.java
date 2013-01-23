/**
 * 
 */
package com.gmail.charleszq.picorner.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.utils.IConstants;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class SettingFragment extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {

	/**
	 * 
	 */
	public SettingFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PreferenceManager pm = this.getPreferenceManager();
		pm.setSharedPreferencesName(IConstants.DEF_PREF_NAME);
		pm.setSharedPreferencesMode(Context.MODE_PRIVATE);

		this.addPreferencesFromResource(R.xml.preferences);
	}

	@Override
	public void onStart() {
		super.onStart();
		PreferenceManager pm = getPreferenceManager();
		SharedPreferences sp = pm.getSharedPreferences();
		sp.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {

	}

	@Override
	public void onStop() {
		SharedPreferences sp = this.getPreferenceManager()
				.getSharedPreferences();
		sp.unregisterOnSharedPreferenceChangeListener(this);
		super.onStop();
	}

}
