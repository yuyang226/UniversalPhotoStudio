/**
 * 
 */
package com.gmail.charleszq.ups.task.flickr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;

import com.gmail.charleszq.ups.UPSApplication;
import com.gmail.charleszq.ups.model.ExifData;
import com.gmail.charleszq.ups.task.AbstractContextAwareTask;
import com.gmail.charleszq.ups.utils.FlickrHelper;
import com.gmail.charleszq.ups.utils.ModelUtils;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.photos.Exif;
import com.googlecode.flickrjandroid.photos.PhotosInterface;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class FetchFlickrPhotoExifTask extends
		AbstractContextAwareTask<String, Integer, List<ExifData>> {

	public FetchFlickrPhotoExifTask(Context ctx) {
		super(ctx);
	}

	@Override
	protected List<ExifData> doInBackground(String... params) {
		String photoId = params[0];
		String photoSecret = null;
		if (params.length > 1) {
			photoSecret = params[1];
		}

		UPSApplication app = (UPSApplication) ((Activity) mContext)
				.getApplication();
		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(
				app.getFlickrToken(), app.getFlickrTokenSecret());
		PhotosInterface pi = f.getPhotosInterface();
		try {
			Collection<Exif> exifs = pi.getExif(photoId, photoSecret);
			List<ExifData> data = ModelUtils.convertFlickrExifs(exifs);
			return postProcess(data);
		} catch (Exception e) {
			logger.warn("Error to get exif information of a photo: " //$NON-NLS-1$
					+ e.getMessage());
		}

		return null;
	}

	/**
	 * 
	 * @param data
	 * @return
	 */
	private List<ExifData> postProcess(List<ExifData> data) {
		if (data.isEmpty()) {
			return null;
		}

		Map<String, ExifData> map = ExifData.getPredefinedExifList();
		for (ExifData exif : data) {
			if (exif.label == null) {
				continue;
			}

			map.put(exif.label, exif);
		}
		return new ArrayList<ExifData>(map.values());
	}

}
