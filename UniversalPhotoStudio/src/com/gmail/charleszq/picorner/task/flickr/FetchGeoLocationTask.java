/**
 * 
 */
package com.gmail.charleszq.picorner.task.flickr;

import android.util.Log;

import com.gmail.charleszq.picorner.model.GeoLocation;
import com.gmail.charleszq.picorner.task.AbstractGeneralTask;
import com.gmail.charleszq.picorner.utils.FlickrHelper;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.photos.GeoData;

/**
 * Represents the task to fetch the GEO location information of a photo
 * 
 * @author charleszq
 * 
 */
public class FetchGeoLocationTask extends
		AbstractGeneralTask<String, Integer, GeoLocation> {

	@Override
	protected GeoLocation doInBackground(String... params) {
		String photoId = params[0];
		Flickr f = FlickrHelper.getInstance().getFlickr();
		try {
			GeoData geo = f.getGeoInterface().getLocation(photoId);
			if (geo != null) {
				GeoLocation loc = new GeoLocation();
				loc.setLatitude(geo.getLatitude());
				loc.setLongitude(geo.getLongitude());
				loc.setAccuracy(geo.getAccuracy());
				return loc;
			}
		} catch (Exception e) {
			Log.w(TAG,
					"unable to get the geo location information: " + e.getMessage()); //$NON-NLS-1$
		}
		Log.d(TAG, "no geo information in this photo."); //$NON-NLS-1$
		return null;
	}

}
