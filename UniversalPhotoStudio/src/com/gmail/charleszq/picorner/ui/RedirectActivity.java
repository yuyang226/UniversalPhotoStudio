/**
 * 
 */
package com.gmail.charleszq.picorner.ui;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.gmail.charleszq.picorner.dp.SinglePagePhotosProvider;
import com.gmail.charleszq.picorner.model.MediaObjectCollection;
import com.gmail.charleszq.picorner.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.picorner.task.flickr.FlickrGetPhotoGeneralInfoTask;
import com.gmail.charleszq.picorner.task.px500.PxFetchPhotoExifTask;
import com.gmail.charleszq.picorner.utils.IConstants;

/**
 * @author yayu
 *
 */
public class RedirectActivity extends FragmentActivity {

	/**
	 * 
	 */
	public RedirectActivity() {
		super();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		Intent intent = getIntent();
		if (IConstants.HTTP_SCHME.equals(intent.getScheme())) {
			String host = intent.getData().getHost();
			List<String> segments = intent.getData().getPathSegments();
			if (IConstants.HOST_500PX.equals(host)) {
				//500px
				if (segments.size() > 1 && IConstants.SEGMENT_PHOTO.equals(segments.get(0))) {
					//the second segment should be the photo ID
					String photoId = segments.get(1);
					PxFetchPhotoExifTask pxTask = new PxFetchPhotoExifTask();
					pxTask.addTaskDoneListener(new IGeneralTaskDoneListener<com.github.yuyang226.j500px.photos.Photo>() {

						@Override
						public void onTaskDone(com.github.yuyang226.j500px.photos.Photo result) {
							if (result != null) {
								final MediaObjectCollection col = new MediaObjectCollection();
								col.addPhoto(com.gmail.charleszq.picorner.utils.ModelUtils.convertPx500Photo(result));
								startImageActivity(new SinglePagePhotosProvider(col));
							}
						}
					});
					pxTask.execute(photoId);
				}
			} else if (IConstants.HOST_FLICKR.equals(host) || IConstants.HOST_FLICKR_MOBILE.equals(host)) {
				//Flickr
				if (segments.size() > 2 && IConstants.SEGMENT_PHOTOS.equals(segments.get(0))) {
					String photoId = segments.get(2);
					FlickrGetPhotoGeneralInfoTask flickrTast = new FlickrGetPhotoGeneralInfoTask();
					flickrTast.addTaskDoneListener(new IGeneralTaskDoneListener<com.googlecode.flickrjandroid.photos.Photo>() {

						@Override
						public void onTaskDone(com.googlecode.flickrjandroid.photos.Photo result) {
							if (result != null) {
								final MediaObjectCollection col = new MediaObjectCollection();
								col.addPhoto(com.gmail.charleszq.picorner.utils.ModelUtils.convertFlickrPhoto(result, null));
								startImageActivity(new SinglePagePhotosProvider(col));
							}
						}
					});
					flickrTast.execute(photoId);
				}
				
			}
		}
	}
	
	private void startImageActivity(SinglePagePhotosProvider mPhotosProvider) {
		final Intent i = new Intent(this, ImageDetailActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtra(ImageDetailActivity.DP_KEY, mPhotosProvider);
		startActivity(i);
	}

	
}
