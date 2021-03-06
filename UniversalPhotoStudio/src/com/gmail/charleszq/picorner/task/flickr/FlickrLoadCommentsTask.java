/**
 * 
 */
package com.gmail.charleszq.picorner.task.flickr;

import java.util.List;

import android.content.Context;

import com.gmail.charleszq.picorner.model.MediaObjectComment;
import com.gmail.charleszq.picorner.task.AbstractContextAwareTask;
import com.gmail.charleszq.picorner.utils.FlickrHelper;
import com.gmail.charleszq.picorner.utils.ModelUtils;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.photos.comments.Comment;
import com.googlecode.flickrjandroid.photos.comments.CommentsInterface;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class FlickrLoadCommentsTask extends
		AbstractContextAwareTask<String, Integer, List<MediaObjectComment>> {

	public FlickrLoadCommentsTask(Context ctx) {
		super(ctx);
	}

	@Override
	protected List<MediaObjectComment> doInBackground(String... params) {
		String photoId = params[0];
		Flickr f = FlickrHelper.getInstance().getFlickr();
		CommentsInterface ci = f.getCommentsInterface();
		try {
			List<Comment> comments = ci.getList(photoId, null, null);
			return ModelUtils.convertFlickrComments(comments);
		} catch (Exception e) {
		}
		return null;
	}

}
