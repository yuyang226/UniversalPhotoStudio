/**
 * 
 */
package com.gmail.charleszq.picorner.task.px500;

import com.github.yuyang226.j500px.J500px;
import com.github.yuyang226.j500px.photos.ImageSize;
import com.github.yuyang226.j500px.photos.Photo;
import com.gmail.charleszq.picorner.task.AbstractGeneralTask;
import com.gmail.charleszq.picorner.utils.IConstants;

/**
 * @author charles(charleszq@gmail.com)
 *
 */
public class PxFetchPhotoExifTask extends
		AbstractGeneralTask<String, Void, Photo> {

	@Override
	protected Photo doInBackground(String... params) {
		String photoId = params[0];
		try {
			J500px px = new J500px(IConstants.PX500_CONSUMER_KEY);
			Photo p = px.getPhotosInterface().getPhotoDetail(Integer.parseInt(photoId), ImageSize.LARGEST, false, -1);
			return p;
		} catch (Exception e) {
			return null;
		}
	}
}
