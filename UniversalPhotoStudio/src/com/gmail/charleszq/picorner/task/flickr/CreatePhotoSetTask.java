/**
 * 
 */
package com.gmail.charleszq.picorner.task.flickr;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.task.AbstractContextAwareTask;
import com.gmail.charleszq.picorner.utils.FlickrHelper;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.photosets.Photoset;
import com.googlecode.flickrjandroid.photosets.PhotosetsInterface;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class CreatePhotoSetTask extends
		AbstractContextAwareTask<String, Integer, Photoset> {

	private ProgressDialog mProgressDialog;

	public CreatePhotoSetTask(Context ctx) {
		super(ctx);
	}

	@Override
	protected void onPostExecute(Photoset result) {
		if (mProgressDialog != null) {
			try {
				mProgressDialog.dismiss();
			} catch (Exception e) {
			}
		}
		if (result != null) {
			String msg = mContext.getString(R.string.msg_photo_set_crted);
			msg = String.format(msg, result.getTitle());
			Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
		}
		super.onPostExecute(result);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mProgressDialog = ProgressDialog.show(mContext,
				"", mContext.getString(R.string.msg_creating_photo_set)); //$NON-NLS-1$
	}

	@Override
	protected Photoset doInBackground(String... params) {
		String name = params[0];
		String photoId = params[1];

		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(mContext);
		PhotosetsInterface pi = f.getPhotosetsInterface();
		try {
			return pi.create(name, name, photoId);
		} catch (Exception e) {
			return null;
		}
	}

}
