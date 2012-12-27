/**
 * 
 */
package com.gmail.charleszq.picorner.task.flickr;

import android.app.Activity;
import android.content.Context;

import com.gmail.charleszq.picorner.UPSApplication;
import com.gmail.charleszq.picorner.task.AbstractContextAwareTask;
import com.gmail.charleszq.picorner.utils.FlickrHelper;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.photos.comments.CommentsInterface;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class FlickrAddPhotoCommentTask extends
		AbstractContextAwareTask<String, Integer, Boolean> {

	public FlickrAddPhotoCommentTask(Context ctx) {
		super(ctx);
	}

	@Override
	protected Boolean doInBackground(String... params) {
		String photoId = params[0];
		String comment = params[1];

		Activity act = (Activity) mContext;
		UPSApplication app = (UPSApplication) act.getApplication();
		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(
				app.getFlickrToken(), app.getFlickrTokenSecret());
		CommentsInterface ci = f.getCommentsInterface();
		try {
			ci.addComment(photoId, comment);
			return true;
		} catch (Exception e) {
		}
		return false;
	}

}
