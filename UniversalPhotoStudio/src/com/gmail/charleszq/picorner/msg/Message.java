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
	public static final int CANCEL_COMMAND = 104;

	/**
	 * The message says one of user's photo account has been authed.
	 */
	public static final int USER_LOGIN_IN = 103;

	private int mMessageType;
	private MediaSourceType mPhotoType;
	private String mPhotoId;
	private Object mCoreData;

	public static final Message PUBLIC_USER_LOGIN_MSG = new Message(
			USER_LOGIN_IN);
	public static final Message CANCEL_CURRENT_COMMAND_MSG = new Message(
			CANCEL_COMMAND);

	/**
	 * 
	 */
	public Message(int type, MediaSourceType sourceType, String photoId,
			Object data) {
		this.mMessageType = type;
		this.mPhotoType = sourceType;
		this.mPhotoId = photoId;
		this.mCoreData = data;
	}

	private Message(int type) {
		this.mMessageType = type;
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
