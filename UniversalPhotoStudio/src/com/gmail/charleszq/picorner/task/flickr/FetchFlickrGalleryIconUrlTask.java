/**
 * 
 */
package com.gmail.charleszq.picorner.task.flickr;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.gmail.charleszq.picorner.SPUtil;
import com.gmail.charleszq.picorner.task.AbstractFetchIconUrlTask;
import com.gmail.charleszq.picorner.utils.FlickrHelper;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.galleries.Gallery;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotosInterface;

/**
 * @author Charles(charleszq@gmail.com)
 * 
 */
public class FetchFlickrGalleryIconUrlTask extends AbstractFetchIconUrlTask {

	private Gallery mGallery;

	public FetchFlickrGalleryIconUrlTask(Context ctx) {
		super(ctx);
	}

	/**
	 * @param ctx
	 */
	public FetchFlickrGalleryIconUrlTask(Context ctx, Gallery gallery) {
		super(ctx);
		this.mGallery = gallery;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected String doInBackground(Object... params) {
		this.beforeExecute(params);
		String primaryPhotoId = mGallery.getPrimaryPhotoId();

		String result = null;
		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(mContext);
		PhotosInterface psi = f.getPhotosInterface();
		try {
			Photo photo = psi.getInfo(primaryPhotoId,
					SPUtil.getFlickrAuthTokenSecret(mContext));
			result = photo.getSmallSquareUrl();
		} catch (Exception e) {
			Log.w(TAG, e.getMessage());
		}
		return result;
	}

	@Override
	protected void beforeExecute(Object... params) {
		if (params.length == 1)
			super.beforeExecute(params);
		else {
			mGallery = (Gallery) params[0];
			mIconViewRef = new WeakReference<View>((View) params[1]);
		}
	}

}
