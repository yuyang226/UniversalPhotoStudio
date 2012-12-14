/**
 * 
 */
package com.gmail.charleszq.ups.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the model for either photos or videos.
 * 
 * @author charleszq
 * 
 */
public final class MediaObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3467986522217462235L;

	private String mId;
	private String mTitle;
	private String mDescription;
	private String mThumbUrl;
	private String mLargeUrl;
	private int mViews = -1, mComments = -1, mFavorites = -1;

	private List<MediaObjectComment> commentList;

	private GeoLocation mLocation;
	private List<String> mTags;
	private Author mAuthor;

	public Author getAuthor() {
		return mAuthor;
	}

	public void setAuthor(Author mAuthor) {
		this.mAuthor = mAuthor;
	}

	public GeoLocation getLocation() {
		return mLocation;
	}

	public void setLocation(GeoLocation mLocation) {
		this.mLocation = mLocation;
	}

	private MediaObjectType mMediaType = MediaObjectType.PHOTO;
	private MediaSourceType mMediaSource = MediaSourceType.FLICKR;

	public MediaSourceType getMediaSource() {
		return mMediaSource;
	}

	public void setMediaSource(MediaSourceType mMediaSource) {
		this.mMediaSource = mMediaSource;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	public String getDescription() {
		return mDescription;
	}

	public void setDescription(String mDescription) {
		this.mDescription = mDescription;
	}

	public String getThumbUrl() {
		return mThumbUrl;
	}

	public void setThumbUrl(String mUrl) {
		this.mThumbUrl = mUrl;
	}

	public MediaObjectType getMediaType() {
		return mMediaType;
	}

	public void setMediaType(MediaObjectType mMediaType) {
		this.mMediaType = mMediaType;
	}

	public String getLargeUrl() {
		return mLargeUrl;
	}

	public void setLargeUrl(String mLargeUrl) {
		this.mLargeUrl = mLargeUrl;
	}

	public String getId() {
		return mId;
	}

	public void setId(String id) {
		mId = id;
	}

	public void addTag(String tag) {
		if (mTags == null) {
			mTags = new ArrayList<String>();
		}
		mTags.add(tag);
	}

	@SuppressWarnings("unchecked")
	public List<String> getTags() {
		if (mTags == null) {
			return Collections.EMPTY_LIST;
		}
		return mTags;
	}

	public int getViews() {
		return mViews;
	}

	public void setViews(int mViews) {
		this.mViews = mViews;
	}

	public int getComments() {
		return mComments;
	}

	public void setComments(int mComments) {
		this.mComments = mComments;
	}

	public int getFavorites() {
		return mFavorites;
	}

	public void setFavorites(int mFavorites) {
		this.mFavorites = mFavorites;
	}

	@SuppressWarnings("unchecked")
	public List<MediaObjectComment> getCommentList() {
		if (commentList == null) {
			return Collections.EMPTY_LIST;
		}
		return commentList;
	}
	
	public void addComment( MediaObjectComment comment ) {
		if( commentList == null ) {
			commentList = new ArrayList<MediaObjectComment>();
		}
		commentList.add(comment);
	}

}
