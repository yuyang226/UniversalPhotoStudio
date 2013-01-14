/**
 * 
 */
package com.gmail.charleszq.picorner.task.px500;

import android.util.Log;

import com.github.yuyang226.j500px.J500px;
import com.github.yuyang226.j500px.photos.ImageSize;
import com.github.yuyang226.j500px.photos.Photo;
import com.gmail.charleszq.picorner.task.AbstractGeneralTask;
import com.gmail.charleszq.picorner.utils.J500pxHelper;

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
			J500px px = J500pxHelper.getJ500pxInstance();
			Photo p = px.getPhotosInterface().getPhotoDetail(Integer.parseInt(photoId), ImageSize.LARGEST, false, -1);
			return p;
		} catch (Exception e) {
			Log.w(TAG, "unable to get the  photo detail: " + e.getMessage()); //$NON-NLS-1$
			return null;
		}
	}
}
