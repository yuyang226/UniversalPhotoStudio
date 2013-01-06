/**
 * 
 */
package com.gmail.charleszq.picorner.task.px500;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.github.yuyang226.j500px.J500px;
import com.github.yuyang226.j500px.photos.Comment;
import com.github.yuyang226.j500px.photos.Photo;
import com.gmail.charleszq.picorner.model.MediaObjectComment;
import com.gmail.charleszq.picorner.task.AbstractGeneralTask;
import com.gmail.charleszq.picorner.utils.IConstants;
import com.gmail.charleszq.picorner.utils.ModelUtils;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class PxFetchPhotoCommentsTask extends
		AbstractGeneralTask<String, Integer, List<MediaObjectComment>> {

	@Override
	protected List<MediaObjectComment> doInBackground(String... params) {
		String photoId = params[0];
		
		try {
			J500px px = new J500px(IConstants.PX500_CONSUMER_KEY);
			Photo photo = px.getPhotosInterface().getPhotoDetail(photoId, null, true, -1);
			List<Comment> pxComments = photo.getComments();
			List<MediaObjectComment> comments = new ArrayList<MediaObjectComment>();

			for (Comment c : pxComments) {
				comments.add(ModelUtils.convertPxPhotoComment(c));
			}
			return comments;
		} catch (Exception e) {
			Log.w(TAG, "Unable to get photo comments: " + e.getMessage()); //$NON-NLS-1$
		}
		return null;
	}

}
