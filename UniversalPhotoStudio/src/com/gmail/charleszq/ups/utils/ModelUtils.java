/**
 * 
 */
package com.gmail.charleszq.ups.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jinstagram.entity.comments.CommentData;
import org.jinstagram.entity.comments.MediaCommentsFeed;
import org.jinstagram.entity.common.Comments;
import org.jinstagram.entity.common.Images;
import org.jinstagram.entity.common.Location;
import org.jinstagram.entity.likes.LikesFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;

import com.gmail.charleszq.ups.model.Author;
import com.gmail.charleszq.ups.model.GeoLocation;
import com.gmail.charleszq.ups.model.MediaObject;
import com.gmail.charleszq.ups.model.MediaObjectCollection;
import com.gmail.charleszq.ups.model.MediaObjectComment;
import com.gmail.charleszq.ups.model.MediaObjectType;
import com.gmail.charleszq.ups.model.MediaSourceType;
import com.googlecode.flickrjandroid.people.User;
import com.googlecode.flickrjandroid.photos.GeoData;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photos.comments.Comment;
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
		if (geo != null) {
			GeoLocation loc = new GeoLocation();
			loc.setLongitude(geo.getLongitude());
			loc.setLatitude(geo.getLatitude());
			loc.setAccuracy(geo.getAccuracy());
			uPhoto.setLocation(loc);
		}

		User user = photo.getOwner();
		if (user != null) {
			Author author = new Author();
			author.setUserId(user.getId());
			author.setUserName(user.getUsername());
			author.setBuddyIconUrl(user.getBuddyIconUrl());
			uPhoto.setAuthor(author);
		}

		Collection<Tag> tags = photo.getTags();
		if (tags != null) {
			for (Tag tag : tags) {
				uPhoto.addTag(tag.getValue());
			}
		}

		return uPhoto;
	}

	public static MediaObjectCollection convertFlickrPhotoList(PhotoList list) {
		MediaObjectCollection pc = new MediaObjectCollection();
		pc.setCurrentPage(list.getPage() - 1);
		pc.setPageSize(list.getPerPage());
		pc.setTotalCount(list.getTotal());
		for (Photo p : list) {
			MediaObject pic = convertFlickrPhoto(p);
			pc.addPhoto(pic);
		}
		return pc;
	}

	public static MediaObject convertInstagramPhoto(MediaFeedData feed) {
		MediaObject photo = new MediaObject();

		String title = ""; //$NON-NLS-1$
		if (feed.getCaption() != null) {
			title = feed.getCaption().getText();
		}
		photo.setMediaType("image".equals(feed.getType()) ? MediaObjectType.PHOTO //$NON-NLS-1$
				: MediaObjectType.VIDEO);
		photo.setTitle(title);
		photo.setId(feed.getId());

		Images imgs = feed.getImages();
		photo.setThumbUrl(imgs.getThumbnail().getImageUrl());
		photo.setLargeUrl(imgs.getStandardResolution().getImageUrl());
		photo.setMediaSource(MediaSourceType.INSTAGRAM);

		Location location = feed.getLocation();
		if (location != null) {
			GeoLocation loc = new GeoLocation();
			loc.setLongitude(location.getLongitude());
			loc.setLatitude(location.getLatitude());
			photo.setLocation(loc);
		}

		if (feed.getTags() != null) {
			for (String t : feed.getTags()) {
				photo.addTag(t);
			}
		}

		org.jinstagram.entity.common.User u = feed.getUser();
		if (u != null) {
			Author author = new Author();
			author.setUserId(String.valueOf(u.getId()));
			author.setUserName(u.getFullName());
			author.setBuddyIconUrl(u.getProfilePictureUrl());
			photo.setAuthor(author);
		}

		photo.setComments(feed.getComments().getCount());
		photo.setFavorites(feed.getLikes().getCount());

		// comments
		Comments comments = feed.getComments();
		for (CommentData data : comments.getComments()) {
			photo.addComment(convertInstagramComment(data));
		}
		return photo;
	}

	public static MediaObjectComment convertFlickrComment(Comment c) {
		MediaObjectComment comment = new MediaObjectComment();
		comment.setCreationTime(c.getDateCreate().getTime());
		comment.setId(c.getId());
		comment.setText(c.getText());
		Author author = new Author();
		author.setUserId(c.getAuthor());
		author.setUserName(c.getAuthorName());
		comment.setAuthor(author);
		return comment;
	}

	public static MediaObjectComment convertInstagramComment(CommentData c) {
		MediaObjectComment comment = new MediaObjectComment();
		comment.setCreationTime(1000L * Long.parseLong(c.getCreatedTime()));
		comment.setId(String.valueOf(c.getId()));
		comment.setText(c.getText());
		Author author = new Author();
		author.setUserId(String.valueOf(c.getCommentFrom().getId()));
		author.setUserName(c.getCommentFrom().getUsername());
		author.setBuddyIconUrl(c.getCommentFrom().getProfilePicture());
		comment.setAuthor(author);
		return comment;
	}

	public static List<MediaObjectComment> convertInstagramComments(
			MediaCommentsFeed feed) {
		List<MediaObjectComment> comments = new ArrayList<MediaObjectComment>();
		for (CommentData data : feed.getCommentDataList()) {
			comments.add(convertInstagramComment(data));
		}
		return comments;
	}

	public static List<MediaObjectComment> convertFlickrComments(
			List<Comment> flickrComments) {
		List<MediaObjectComment> comments = new ArrayList<MediaObjectComment>();
		for (Comment c : flickrComments) {
			comments.add(convertFlickrComment(c));
		}
		return comments;
	}

	public static List<Author> convertInstagramLikesFeed(LikesFeed feed) {
		List<Author> users = new ArrayList<Author>();
		for (org.jinstagram.entity.common.User u : feed.getUserList()) {
			users.add(convertInstagramUser(u));
		}
		return users;
	}

	public static Author convertInstagramUser(
			org.jinstagram.entity.common.User u) {
		Author a = new Author();
		a.setBuddyIconUrl(u.getProfilePictureUrl());
		a.setUserId(String.valueOf(u.getId()));
		a.setUserName(u.getUserName());
		return a;
	}
}
