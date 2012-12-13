/**
 * 
 */
package com.gmail.charleszq.ups.utils;

import java.util.Collection;

import org.jinstagram.entity.common.Images;
import org.jinstagram.entity.common.Location;
import org.jinstagram.entity.users.feed.MediaFeedData;

import com.gmail.charleszq.ups.model.Author;
import com.gmail.charleszq.ups.model.GeoLocation;
import com.gmail.charleszq.ups.model.MediaObject;
import com.gmail.charleszq.ups.model.MediaObjectCollection;
import com.gmail.charleszq.ups.model.MediaObjectType;
import com.gmail.charleszq.ups.model.MediaSourceType;
import com.googlecode.flickrjandroid.people.User;
import com.googlecode.flickrjandroid.photos.GeoData;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.tags.Tag;

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
		uPhoto.setViews(photo.getViews());
		uPhoto.setComments(photo.getComments());
		uPhoto.setFavorites(photo.getFavorites());
		
		GeoData geo = photo.getGeoData();
		if( geo != null ) {
			GeoLocation loc = new GeoLocation();
			loc.setLongitude( geo.getLongitude());
			loc.setLatitude( geo.getLatitude());
			loc.setAccuracy(geo.getAccuracy());
			uPhoto.setLocation( loc );
		}
		
		User user = photo.getOwner();
		if( user != null ) {
			Author author = new Author();
			author.setUserId( user.getId() );
			author.setUserName( user.getUsername());
			author.setBuddyIconUrl( user.getBuddyIconUrl() );
			uPhoto.setAuthor( author );
		}
		
		Collection<Tag> tags = photo.getTags();
		if( tags != null ) {
			for( Tag tag : tags ) {
				uPhoto.addTag( tag.getValue());
			}
		}
		
		
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
		
		Location location = feed.getLocation();
		if( location != null ) {
			GeoLocation loc = new GeoLocation();
			loc.setLongitude( location.getLongitude());
			loc.setLatitude( location.getLatitude());
			photo.setLocation( loc );
		}
		
		if( feed.getTags() != null ) {
			for( String t : feed.getTags() ) {
				photo.addTag(t);
			}
		}
		
		org.jinstagram.entity.common.User u = feed.getUser();
		if( u != null ) {
			Author author = new Author();
			author.setUserId( String.valueOf(u.getId() ));
			author.setUserName( u.getFullName());
			author.setBuddyIconUrl( u.getProfilePictureUrl() );
			photo.setAuthor( author );
		}
		
		photo.setComments( feed.getComments().getCount());
		photo.setFavorites( feed.getLikes().getCount() );
		return photo;
	}
}
