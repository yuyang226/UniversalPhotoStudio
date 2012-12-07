/**
 * 
 */
package com.gmail.charleszq.ups.model;

import java.io.Serializable;

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

}
