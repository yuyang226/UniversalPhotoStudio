/**
 * 
 */
package com.gmail.charleszq.ups.utils;

import org.jinstagram.entity.common.Images;
import org.jinstagram.entity.users.feed.MediaFeedData;

import com.gmail.charleszq.ups.model.MediaObject;
import com.gmail.charleszq.ups.model.MediaObjectCollection;
import com.gmail.charleszq.ups.model.MediaObjectType;
import com.gmail.charleszq.ups.model.MediaSourceType;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;

/**
 * @author charleszq
 * 
 */
public final class ModelUtils {

	/**
	 * Converts the flickrj api <code>Photo</code>.
	 * 
	 * @param photo
	 * @param photoSize
	 * @return
	 */
	public static MediaObject convertFlickrPhoto(Photo photo) {
		MediaObject uPhoto = new MediaObject();
		uPhoto.setDescription(photo.getDescription());
		uPhoto.setTitle(photo.getTitle());
		uPhoto.setId(photo.getId());
		uPhoto.setThumbUrl(photo.getSmallUrl());
		uPhoto.setLargeUrl(photo.getLargeUrl());
		return uPhoto;
	}
	
	public static MediaObjectCollection convertFlickrPhotoList(PhotoList list ) {
		MediaObjectCollection pc = new MediaObjectCollection();
		pc.setCurrentPage(list.getPage()-1);
		pc.setPageSize(list.getPerPage());
		pc.setTotalCount(list.getTotal());
		for( Photo p : list ) {
			MediaObject pic = convertFlickrPhoto(p);
			pc.addPhoto(pic);
		}
		return pc;
	}

	public static MediaObject convertInstagramPhoto(MediaFeedData feed) {
		MediaObject photo = new MediaObject();
		
		String title = ""; //$NON-NLS-1$
		if( feed.getCaption() != null ) {
			title = feed.getCaption().getText();
		}
		photo.setDescription(title);
		photo.setMediaType("image".equals(feed.getType()) ? MediaObjectType.PHOTO //$NON-NLS-1$
				: MediaObjectType.VIDEO);
		photo.setTitle(title);
		photo.setId(feed.getId());
	
		Images imgs = feed.getImages();
		photo.setThumbUrl(imgs.getThumbnail().getImageUrl());
		photo.setLargeUrl(imgs.getStandardResolution().getImageUrl());
		photo.setMediaSource(MediaSourceType.INSTAGRAM);
		return photo;
	}
}
