/**
 * 
 */
package com.gmail.charleszq.picorner.msg;

import com.gmail.charleszq.picorner.model.MediaSourceType;

/**
 * @author charleszq
 *
 */
public final class Message {
	
	public static final int LIKE_PHOTO = 100;
	public static final int VOTE_PHOTO = 101;
	public static final int GEO_INFO_FETCHED = 102;
	
	private int mMessageType;
	private MediaSourceType mPhotoType;
	private String mPhotoId;
	private Object mCoreData;

	/**
	 * 
	 */
	public Message(int type, MediaSourceType sourceType, String photoId, Object data ) {
		this.mMessageType = type;
		this.mPhotoType = sourceType;
		this.mPhotoId = photoId;
		this.mCoreData = data;
	}

	/**
	 * @return the mMessageType
	 */
	public int getMessageType() {
		return mMessageType;
	}

	/**
	 * @return the mPhotoType
	 */
	public MediaSourceType getPhotoType() {
		return mPhotoType;
	}

	/**
	 * @return the mPhotoId
	 */
	public String getPhotoId() {
		return mPhotoId;
	}

	/**
	 * @return the mCoreData
	 */
	public Object getCoreData() {
		return mCoreData;
	}
	
	

}
