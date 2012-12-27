/**
 * 
 */
package com.gmail.charleszq.picorner.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

import com.gmail.charleszq.picorner.R;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class UserPhotoListActivity extends FragmentActivity {

	public static final String USER_KEY = "user"; //$NON-NLS-1$
	public static final String MD_TYPE_KEY = "photo.type"; //$NON-NLS-1$


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_photo_list_act);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if( item.getItemId() == android.R.id.home ) {
			Intent i = new Intent(this, MainSlideMenuActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
}
