/**
 * 
 */
package com.gmail.charleszq.picorner.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
import android.util.JsonReader;
import android.util.JsonWriter;
import android.widget.TextView;

import com.github.yuyang226.j500px.photos.PhotoExif;
import com.gmail.charleszq.picorner.model.Author;
import com.gmail.charleszq.picorner.model.ExifData;
import com.gmail.charleszq.picorner.model.FlickrUserPhotoPool;
import com.gmail.charleszq.picorner.model.GeoLocation;
import com.gmail.charleszq.picorner.model.MediaObject;
import com.gmail.charleszq.picorner.model.MediaObjectCollection;
import com.gmail.charleszq.picorner.model.MediaObjectComment;
import com.gmail.charleszq.picorner.model.MediaObjectType;
import com.gmail.charleszq.picorner.model.MediaSourceType;
import com.google.gson.JsonObject;
import com.googlecode.flickrjandroid.galleries.Gallery;
import com.googlecode.flickrjandroid.groups.Group;
import com.googlecode.flickrjandroid.people.User;
import com.googlecode.flickrjandroid.photos.Exif;
import com.googlecode.flickrjandroid.photos.GeoData;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photos.comments.Comment;
import com.googlecode.flickrjandroid.photosets.Photoset;
import com.googlecode.flickrjandroid.tags.Tag;

/**
 * @author charleszq
 * 
 */
public final class ModelUtils {

	private static MediaObject convertFlickrPhoto(Photo p) {
		return convertFlickrPhoto(p, null);
	}

	public static MediaObject convertFlickrPhoto(Photo photo, User flickrOwner) {
		MediaObject uPhoto = new MediaObject();
		uPhoto.setDescription(photo.getDescription());
		uPhoto.setTitle(photo.getTitle());
		uPhoto.setId(photo.getId());
		uPhoto.setThumbUrl(photo.getLargeSquareUrl());
		String largeUrl = photo.getLargeUrl();
		if( largeUrl == null ) {
			largeUrl = photo.getMediumUrl();
			if( largeUrl == null ) {
				largeUrl = photo.getLargeSquareUrl();
			}
		}
		uPhoto.setLargeUrl(largeUrl);
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
		Author author = new Author();
		if (user != null) {
			author.setUserId(user.getId());
			author.setUserName(user.getUsername());
			author.setBuddyIconUrl(user.getBuddyIconUrl());
			uPhoto.setAuthor(author);
		} else if (flickrOwner != null) {
			author.setUserId(flickrOwner.getId());
			author.setUserName(flickrOwner.getUsername());
			author.setBuddyIconUrl(flickrOwner.getBuddyIconUrl());
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

	public static MediaObjectCollection convertFlickrPhotoList(PhotoList list,
			User flickrOwner) {
		MediaObjectCollection pc = new MediaObjectCollection();
		pc.setCurrentPage(list.getPage() - 1);
		pc.setPageSize(list.getPerPage());
		pc.setTotalCount(list.getTotal());
		for (Photo p : list) {
			MediaObject pic = flickrOwner == null ? convertFlickrPhoto(p)
					: convertFlickrPhoto(p, flickrOwner);
			pc.addPhoto(pic);
		}
		return pc;
	}

	public static MediaObjectCollection convertFlickrPhotoList(PhotoList list) {
		return convertFlickrPhotoList(list, null);
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
			List<com.github.yuyang226.j500px.photos.Photo> photos) {
		MediaObjectCollection list = new MediaObjectCollection();
		for (com.github.yuyang226.j500px.photos.Photo p : photos) {
			list.addPhoto(convertPx500Photo(p));
		}
		list.setTotalCount(photos.size());
		return list;
	}

	public static MediaObject convertPx500Photo(
			com.github.yuyang226.j500px.photos.Photo p) {
		MediaObject photo = new MediaObject();
		photo.setId(String.valueOf(p.getId()));
		photo.setThumbUrl(p.getImageUrl());
		if (!p.getImageUrls().isEmpty()) {
			photo.setLargeUrl(p.getImageUrls().get(0).getImageUrl());
		}
		photo.setTitle(p.getName());

		if (p.getAuthor() != null) {
			Author a = new Author();
			a.setUserId(String.valueOf(p.getAuthor().getId()));
			a.setUserName(p.getAuthor().getUserName());
			a.setBuddyIconUrl(p.getAuthor().getUserPicUrl());
			photo.setAuthor(a);
		}

		// Exif
		handlePx500PhotoExif(photo, p);

		if (p.getLatitude() != null && p.getLongitude() != null) {
			GeoLocation loc = new GeoLocation();
			loc.setLatitude(p.getLatitude());
			loc.setLongitude(p.getLongitude());
			photo.setLocation(loc);
		}

		photo.setFavorites(p.getFavouritesCount());
		photo.setComments(p.getCommentsCount());
		photo.setViews(p.getViewsCount());
		
		photo.setUserLiked(p.isFavorited());
		photo.setUserVoted(p.isVoted());

		photo.setMediaSource(MediaSourceType.PX500);
		return photo;
	}

	public static MediaObject handlePx500PhotoExif(MediaObject photo,
			com.github.yuyang226.j500px.photos.Photo p) {
		if (p.getExif() != null) {
			PhotoExif pExif = p.getExif();
			ExifData exif = null;
			if (pExif.getCamera() != null) {
				exif = new ExifData(ExifData.LABEL_MODEL);
				exif.value = pExif.getCamera().getName();
				photo.addExifdata(exif);
			}

			if (pExif.getAperture() != null) {
				exif = new ExifData(ExifData.LABEL_APERTURE);
				exif.value = pExif.getAperture();
				photo.addExifdata(exif);
			}

			if (pExif.getTakenAt() != null) {
				exif = new ExifData(ExifData.LABEL_CRT_TIME);
				exif.value = pExif.getTakenAt().toString();
				photo.addExifdata(exif);
			}

			if (pExif.getFocalLength() != null) {
				exif = new ExifData(ExifData.LABEL_FOCAL_LEN);
				exif.value = pExif.getFocalLength();
				photo.addExifdata(exif);
			}

			if (pExif.getIso() != null) {
				exif = new ExifData(ExifData.LABEL_ISO);
				exif.value = pExif.getIso();
				photo.addExifdata(exif);
			}

			if (pExif.getShutterSpeed() != null) {
				exif = new ExifData(ExifData.LABEL_EXPOSURE);
				exif.value = pExif.getShutterSpeed();
				photo.addExifdata(exif);
			}

			if (pExif.getLens() != null) {
				exif = new ExifData(ExifData.LABEL_LEN);
				exif.value = pExif.getLens().getName();
				photo.addExifdata(exif);
			}
		}
		return photo;
	}

	public static MediaObjectComment convertPxPhotoComment(
			com.github.yuyang226.j500px.photos.Comment pxComment) {
		MediaObjectComment comment = new MediaObjectComment();
		comment.setId(String.valueOf(pxComment.getId()));
		comment.setText(pxComment.getComment());
		comment.setCreateTimeString(pxComment.getCreatedAt() != null 
				? pxComment.getCreatedAt().toString() : ""); //$NON-NLS-1$

		Author u = new Author();
		u.setUserId(String.valueOf(pxComment.getUserId()));
		u.setUserName(pxComment.getAuthor().getUserName());
		u.setBuddyIconUrl(pxComment.getAuthor().getUserPicUrl());
		comment.setAuthor(u);
		return comment;
	}

	public static JsonObject createFlickrUserPhotoPoolJsonObject(
			FlickrUserPhotoPool pool) {
		JsonObject obj = new JsonObject();
		obj.addProperty(FlickrUserPhotoPool.ID, pool.getId());
		obj.addProperty(FlickrUserPhotoPool.ICON_URL, pool.getIconUrl());
		obj.addProperty(FlickrUserPhotoPool.COUNT, pool.getPhotoCount());
		obj.addProperty(FlickrUserPhotoPool.TITLE, pool.getTitle());
		obj.addProperty(FlickrUserPhotoPool.TYPE, pool.getType());
		return obj;
	}

	public static FlickrUserPhotoPool createFlickrUserPhotoPoolFromJsonObject(
			JsonObject json) {
		FlickrUserPhotoPool pool = new FlickrUserPhotoPool();
		pool.setId(json.getAsJsonPrimitive(FlickrUserPhotoPool.ID)
				.getAsString());
		pool.setTitle(json.getAsJsonPrimitive(FlickrUserPhotoPool.TITLE)
				.getAsString());
		pool.setType(json.getAsJsonPrimitive(FlickrUserPhotoPool.TYPE)
				.getAsInt());
		pool.setPhotoCount(json.getAsJsonPrimitive(FlickrUserPhotoPool.COUNT)
				.getAsInt());
		pool.setIconUrl(json.getAsJsonPrimitive(FlickrUserPhotoPool.ICON_URL)
				.getAsString());
		return pool;
	}

	public static void writeFlickrUserPhotoPools(
			List<FlickrUserPhotoPool> pools, File file) throws IOException {
		JsonWriter writer = new JsonWriter(new FileWriter(file));
		writer.beginArray();
		for (FlickrUserPhotoPool pool : pools) {
			writer.beginObject();
			writer.name(FlickrUserPhotoPool.ID).value(pool.getId());
			writer.name(FlickrUserPhotoPool.TITLE).value(pool.getTitle());
			writer.name(FlickrUserPhotoPool.ICON_URL).value(pool.getIconUrl());
			writer.name(FlickrUserPhotoPool.COUNT).value(pool.getPhotoCount());
			writer.name(FlickrUserPhotoPool.TYPE).value(pool.getType());
			writer.endObject();
		}
		writer.endArray();
		writer.flush();
		writer.close();
	}

	@SuppressWarnings("deprecation")
	public static List<Object> readFlickrUserPhotoPools(File file)
			throws IOException {
		JsonReader reader = new JsonReader(new FileReader(file));
		List<Object> pools = new ArrayList<Object>();
		reader.beginArray();
		while (reader.hasNext()) {
			reader.beginObject();
			final FlickrUserPhotoPool pool = new FlickrUserPhotoPool();
			while (reader.hasNext()) {
				String name = reader.nextName();
				if (FlickrUserPhotoPool.ID.equals(name)) {
					pool.setId(reader.nextString());
				} else if (FlickrUserPhotoPool.COUNT.equals(name)) {
					pool.setPhotoCount(reader.nextInt());
				} else if (FlickrUserPhotoPool.ICON_URL.equals(name)) {
					pool.setIconUrl(reader.nextString());
				} else if (FlickrUserPhotoPool.TITLE.equals(name)) {
					pool.setTitle(reader.nextString());
				} else if (FlickrUserPhotoPool.TYPE.equals(name)) {
					pool.setType(reader.nextInt());
				}
			}
			reader.endObject();
			switch (pool.getType()) {
			case FlickrUserPhotoPool.TYPE_PHOTO_SET:
				Photoset ps = new Photoset();
				ps.setId(pool.getId());
				ps.setPhotoCount(pool.getPhotoCount());
				ps.setTitle(pool.getTitle());
				pools.add(ps);
				break;
			case FlickrUserPhotoPool.TYPE_GROUP:
				Group g = new Group() {
					/**
					 * sid
					 */
					private static final long serialVersionUID = -247173679567276483L;

					@Override
					public String getBuddyIconUrl() {
						return pool.getIconUrl();
					}

				};
				g.setId(pool.getId());
				g.setName(pool.getTitle());
				g.setPhotoCount(pool.getPhotoCount());
				pools.add(g);
				break;
			case FlickrUserPhotoPool.TYPE_GALLERY:
				Gallery gallery = new Gallery();
				gallery.setGalleryId(pool.getId());
				gallery.setPhotoCount(pool.getPhotoCount());
				gallery.setTitle(pool.getTitle());
				gallery.setPrimaryPhotoId(pool.getIconUrl());
				pools.add(gallery);
				break;
			}
		}
		reader.endArray();
		reader.close();
		return pools;
	}
}
