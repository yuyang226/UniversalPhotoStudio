/**
 * 
 */
package com.gmail.charleszq.ups.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jinstagram.entity.comments.CommentData;
import org.jinstagram.entity.comments.MediaCommentsFeed;
import org.jinstagram.entity.common.Comments;
import org.jinstagram.entity.common.Images;
import org.jinstagram.entity.common.Location;
import org.jinstagram.entity.likes.LikesFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;

import android.text.Html;
import android.text.util.Linkify;
import android.text.util.Linkify.MatchFilter;
import android.text.util.Linkify.TransformFilter;
import android.widget.TextView;

import com.gmail.charleszq.ups.model.Author;
import com.gmail.charleszq.ups.model.ExifData;
import com.gmail.charleszq.ups.model.GeoLocation;
import com.gmail.charleszq.ups.model.MediaObject;
import com.gmail.charleszq.ups.model.MediaObjectCollection;
import com.gmail.charleszq.ups.model.MediaObjectComment;
import com.gmail.charleszq.ups.model.MediaObjectType;
import com.gmail.charleszq.ups.model.MediaSourceType;
import com.googlecode.flickrjandroid.people.User;
import com.googlecode.flickrjandroid.photos.Exif;
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
		uPhoto.setSecret(photo.getSecret());

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
		photo.setUserLiked(feed.isUserHasLiked());

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
		if (feed != null)
			for (CommentData data : feed.getCommentDataList()) {
				comments.add(convertInstagramComment(data));
			}
		return comments;
	}

	public static List<MediaObjectComment> convertFlickrComments(
			List<Comment> flickrComments) {
		List<MediaObjectComment> comments = new ArrayList<MediaObjectComment>();
		if (flickrComments != null)
			for (Comment c : flickrComments) {
				comments.add(convertFlickrComment(c));
			}
		return comments;
	}

	public static List<Author> convertInstagramLikesFeed(LikesFeed feed) {
		List<Author> users = new ArrayList<Author>();
		if (feed != null)
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

	public static Author convertFlickrUser(User user) {
		Author a = new Author();
		a.setUserId(user.getId());
		a.setUserName(user.getUsername());
		a.setBuddyIconUrl(user.getBuddyIconUrl());
		return a;
	}

	public static List<Author> convertFlickrUsers(Collection<User> users) {
		List<Author> as = new ArrayList<Author>();
		if (users != null)
			for (User u : users) {
				as.add(convertFlickrUser(u));
			}
		return as;
	}

	public static ExifData convertFlickrExif(Exif exif) {
		ExifData data = new ExifData();
		data.label = exif.getLabel();
		data.value = exif.getRaw();
		return data;
	}

	public static List<ExifData> convertFlickrExifs(Collection<Exif> exifs) {
		List<ExifData> es = new ArrayList<ExifData>();
		if (exifs != null)
			for (Exif exif : exifs) {
				es.add(convertFlickrExif(exif));
			}
		return es;
	}

	/**
	 * Example: [http://www.flickr.com/photos/example/2910192942/]
	 */
	private static final String FILICK_URL_EXPRESSION = "(\\[http){1}+(s)?+(://){1}+.*\\]{1}+"; //$NON-NLS-1$

	public static void formatHtmlString(String string, TextView textView) {

		textView.setText(Html.fromHtml(string));
		Linkify.addLinks(textView, Pattern.compile(FILICK_URL_EXPRESSION),
				"http://", new MatchFilter() { //$NON-NLS-1$

					@Override
					public boolean acceptMatch(CharSequence s, int start,
							int end) {
						return true;
					}

				}, new TransformFilter() {

					@Override
					public String transformUrl(Matcher matcher, String data) {
						if (data.length() > 2) {
							return data.substring(1, data.length() - 1);
						}
						return data;
					}

				});
	}

	public static MediaObjectCollection convertPx500Photos(
			List<com.gmail.charleszq.px500.model.Photo> photos) {
		MediaObjectCollection list = new MediaObjectCollection();
		for (com.gmail.charleszq.px500.model.Photo p : photos) {
			list.addPhoto(convertPx500Photo(p));
		}
		list.setTotalCount(photos.size());
		return list;
	}

	public static MediaObject convertPx500Photo(
			com.gmail.charleszq.px500.model.Photo p) {
		MediaObject photo = new MediaObject();
		photo.setId(p.id);
		photo.setThumbUrl(p.imageUrl);
		photo.setLargeUrl(p.largeImageUrl);
		photo.setTitle(p.name);

		Author a = new Author();
		a.setUserId(p.author.id);
		a.setUserName(p.author.userName);
		a.setBuddyIconUrl(p.author.buddyIconUrl);
		photo.setAuthor(a);

		// Exif
		ExifData exif = new ExifData(ExifData.LABEL_MODEL);
		exif.value = p.exif.camera;
		photo.addExifdata(exif);

		exif = new ExifData(ExifData.LABEL_APERTURE);
		exif.value = p.exif.aperture;
		photo.addExifdata(exif);

		exif = new ExifData(ExifData.LABEL_CRT_TIME);
		exif.value = p.exif.takenAt;
		photo.addExifdata(exif);

		exif = new ExifData(ExifData.LABEL_FOCAL_LEN);
		exif.value = p.exif.focalLength;
		photo.addExifdata(exif);

		exif = new ExifData(ExifData.LABEL_ISO);
		exif.value = p.exif.iso;
		photo.addExifdata(exif);

		exif = new ExifData(ExifData.LABEL_EXPOSURE);
		exif.value = p.exif.shutterSpeed;
		photo.addExifdata(exif);

		exif = new ExifData(ExifData.LABEL_LEN);
		exif.value = p.exif.lens;
		photo.addExifdata(exif);

		if (p.latitude != null && p.longitude != null) {
			GeoLocation loc = new GeoLocation();
			loc.setLatitude(Double.parseDouble(p.latitude));
			loc.setLongitude(Double.parseDouble(p.longitude));
			photo.setLocation(loc);
		}

		photo.setFavorites(p.favorites);
		photo.setComments(p.comments);

		photo.setMediaSource(MediaSourceType.PX500);
		return photo;
	}
}
