/**
 * 
 */
package com.gmail.charleszq.ups.task.px500;

import com.gmail.charleszq.px500.PX500;
import com.gmail.charleszq.px500.model.Photo;
import com.gmail.charleszq.ups.task.AbstractGeneralTask;
import com.gmail.charleszq.ups.utils.IConstants;

/**
 * @author charles(charleszq@gmail.com)
 *
 */
public class PxFetchPhotoExifTask extends
		AbstractGeneralTask<String, Void, Photo> {

	@Override
	protected Photo doInBackground(String... params) {
		String photoId = params[0];
		PX500 px = new PX500(IConstants.PX500_CONSUMER_KEY);
		try {
			Photo p = px.getPhotoDetail(photoId);
			return p;
		} catch (Exception e) {
			return null;
		}
	}
}
