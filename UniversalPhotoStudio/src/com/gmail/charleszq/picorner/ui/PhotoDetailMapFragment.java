/**
 * 
 */
package com.gmail.charleszq.picorner.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.charleszq.picorner.model.GeoLocation;
import com.gmail.charleszq.picorner.model.MediaObject;
import com.gmail.charleszq.picorner.utils.IConstants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class PhotoDetailMapFragment extends SupportMapFragment {

	private MediaObject mCurrentPhoto;

	/**
	 * 
	 */
	public PhotoDetailMapFragment() {
		super();
	}

	public static PhotoDetailMapFragment newMyInstance(MediaObject photo) {
		PhotoDetailMapFragment f = new PhotoDetailMapFragment();
		final Bundle bundle = new Bundle();
		bundle.putSerializable(IConstants.DETAIL_PAGE_PHOTO_ARG_KEY, photo);
		f.setArguments(bundle);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = this.getArguments();
		mCurrentPhoto = (MediaObject) bundle
				.getSerializable(IConstants.DETAIL_PAGE_PHOTO_ARG_KEY);
		this.setRetainInstance(true);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mCurrentPhoto != null) {
			goLocation(mCurrentPhoto.getLocation());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = super.onCreateView(inflater, container, savedInstanceState);
		GeoLocation loc = mCurrentPhoto.getLocation();
		goLocation(loc);
		return v;
	}

	private void goLocation(GeoLocation loc) {
		GoogleMap map = getMap();
		if (map != null) {
			if (loc != null) {
				LatLng pos = new LatLng(loc.getLatitude(), loc.getLongitude());
				map.addMarker(new MarkerOptions().position(pos));
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 8));
			} else {
			}
		}
	}
}
