/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command.ig;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.service.IPhotoService;
import com.gmail.charleszq.picorner.service.ig.InstagramSearchNearbyPhotosService;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class InstagramSearchNearPhotosCommand extends AbstractInstagramPhotoListCommand {

	private static final String TAG = InstagramSearchNearPhotosCommand.class
			.getSimpleName();

	/**
	 * the current state 0: find the current location; 1: current location
	 * found; other: can not find the current location.
	 */
	private int mCurrentState = 0;

	/**
	 * Location manager.
	 */
	private LocationManager mLocationManager;

	/**
	 * Location listener to receive the location update.
	 */
	private LocationListener mLocationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {
				mLocationManager.removeUpdates(this);
				InstagramSearchNearPhotosCommand.this.mCurrentState = 1;
				mCurrentPhotoService = new InstagramSearchNearbyPhotosService(location);
				Log.d(TAG, "lat = " + location.getAltitude() + ", lng = " //$NON-NLS-1$//$NON-NLS-2$
						+ location.getLongitude());
				InstagramSearchNearPhotosCommand.this.execute();
			}
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

		@Override
		public void onProviderEnabled(String provider) {

		}

		@Override
		public void onProviderDisabled(String provider) {

		}
	};

	/**
	 * @param context
	 */
	public InstagramSearchNearPhotosCommand(Context context) {
		super(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.picorner.ui.command.ICommand#getIconResourceId()
	 */
	@Override
	public int getIconResourceId() {
		return android.R.drawable.ic_menu_myplaces;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.picorner.ui.command.ICommand#getLabel()
	 */
	@Override
	public String getLabel() {
		return mContext.getString(R.string.cmd_name_ig_loc_search);
	}

	@Override
	public boolean execute(Object... params) {
		if (mCurrentState != 1) {
			findCurrentLocation();
			return false;
		} else {
			// restore the current state
			mCurrentState = 0;
			return super.execute(params);
		}
	}
	
	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == IPhotoService.class) {
			return mCurrentPhotoService;
		}
		return super.getAdapter(adapterClass);
	}
	
	@Override
	public String getDescription() {
		return mContext.getString(R.string.cd_ig_photos_nearby);
	}

	private void findCurrentLocation() {
		mLocationManager = (LocationManager) mContext
				.getSystemService(Context.LOCATION_SERVICE);

		Location loc = mLocationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (loc == null) {
			Toast.makeText(mContext,
					mContext.getString(R.string.msg_pls_turn_on_location),
					Toast.LENGTH_SHORT).show();
			Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			mContext.startActivity(i);
		} else {
			mLocationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 3000L, 10,
					mLocationListener);
		}
	}

}
