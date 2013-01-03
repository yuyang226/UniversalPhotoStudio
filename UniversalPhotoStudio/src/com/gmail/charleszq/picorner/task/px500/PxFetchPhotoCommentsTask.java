/**
 * 
 */
package com.gmail.charleszq.picorner.task.px500;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.gmail.charleszq.picorner.model.MediaObjectComment;
import com.gmail.charleszq.picorner.task.AbstractGeneralTask;
import com.gmail.charleszq.picorner.utils.IConstants;
import com.gmail.charleszq.picorner.utils.ModelUtils;
import com.gmail.charleszq.px500.PX500;
import com.gmail.charleszq.px500.model.Comment;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class PxFetchPhotoCommentsTask extends
		AbstractGeneralTask<String, Integer, List<MediaObjectComment>> {

	@Override
	protected List<MediaObjectComment> doInBackground(String... params) {
		String photoId = params[0];
		PX500 px = new PX500(IConstants.PX500_CONSUMER_KEY);
		try {
			List<Comment> pxComments = px.getPhotoComments(photoId);
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
