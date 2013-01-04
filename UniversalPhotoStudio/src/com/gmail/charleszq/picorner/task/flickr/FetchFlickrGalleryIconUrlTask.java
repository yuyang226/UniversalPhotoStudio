/**
 * 
 */
package com.gmail.charleszq.picorner.task.flickr;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.gmail.charleszq.picorner.PicornerApplication;
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
		PicornerApplication app = (PicornerApplication) ((Activity) this.mContext)
				.getApplication();

		String result = null;
		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(
				app.getFlickrToken(), app.getFlickrTokenSecret());
		PhotosInterface psi = f.getPhotosInterface();
		try {
			Photo photo = psi.getInfo(primaryPhotoId, app.getFlickrTokenSecret());
			result = photo.getSmallSquareUrl();
		} catch (Exception e) {
			Log.w(TAG, e.getMessage());
		}
		return result;
	}

}
