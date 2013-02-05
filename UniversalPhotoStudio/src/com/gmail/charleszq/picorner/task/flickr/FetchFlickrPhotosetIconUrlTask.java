/**
 * 
 */
package com.gmail.charleszq.picorner.task.flickr;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.gmail.charleszq.picorner.task.AbstractFetchIconUrlTask;
import com.gmail.charleszq.picorner.utils.FlickrHelper;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.photosets.Photoset;
import com.googlecode.flickrjandroid.photosets.PhotosetsInterface;

/**
 * @author Charles(charleszq@gmail.com)
 * 
 */
public class FetchFlickrPhotosetIconUrlTask extends AbstractFetchIconUrlTask {

	private Photoset mPhotoset;

	public FetchFlickrPhotosetIconUrlTask(Context ctx) {
		super(ctx);
	}

	/**
	 * @param ctx
	 */
	public FetchFlickrPhotosetIconUrlTask(Context ctx, Photoset ps) {
		super(ctx);
		this.mPhotoset = ps;
	}

	@Override
	protected void beforeExecute(Object... params) {
		if (params.length == 1)
			super.beforeExecute(params);
		else {
			mPhotoset = (Photoset) params[0];
			mIconViewRef = new WeakReference<View>((View) params[1]);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected String doInBackground(Object... params) {

		String result = null;
		beforeExecute(params);

		if (mPhotoset.getPrimaryPhoto() != null) {
			result = mPhotoset.getPrimaryPhoto().getSmallSquareUrl();
		}

		if (result != null) {
			return result;
		}

		Flickr f = FlickrHelper.getInstance().getFlickr();
		PhotosetsInterface psi = f.getPhotosetsInterface();
		try {
			Photoset ps = psi.getInfo(mPhotoset.getId());
			mPhotoset.setPrimaryPhoto(ps.getPrimaryPhoto());
			result = mPhotoset.getPrimaryPhoto().getSmallSquareUrl();
		} catch (Exception e) {
			Log.w(TAG, e.getMessage());
		}

		return result;
	}

}
