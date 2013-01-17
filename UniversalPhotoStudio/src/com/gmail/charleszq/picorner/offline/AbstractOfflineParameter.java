/**
 * 
 */
package com.gmail.charleszq.picorner.offline;

import java.io.File;

import com.gmail.charleszq.picorner.model.MediaSourceType;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public abstract class AbstractOfflineParameter implements IOfflineViewParameter {

	/**
	 * auto gened sid.
	 */
	private static final long serialVersionUID = -591123326087036309L;

	protected MediaSourceType mPhotoSourceType;
	protected OfflinePhotoCollectionType mPhotoCollectionType;
	protected long mLastUpdateTime;
	protected String mPhotoCollectionId;
	protected String mControlFileName;

	public AbstractOfflineParameter(MediaSourceType photoSourceType,
			OfflinePhotoCollectionType collectionType, String collectionId) {
		this.mPhotoSourceType = photoSourceType;
		this.mPhotoCollectionId = collectionId;
		this.mPhotoCollectionType = collectionType;
	}

	public AbstractOfflineParameter(int sourceType, int collectionType,
			String collectionId) {
		this.mPhotoCollectionId = collectionId;
		if (collectionType == OfflinePhotoCollectionType.PHOTO_SET.ordinal()) {
			this.mPhotoCollectionType = OfflinePhotoCollectionType.PHOTO_SET;
		}
		// TODO other collection types
		if (sourceType == MediaSourceType.FLICKR.ordinal()) {
			this.mPhotoSourceType = MediaSourceType.FLICKR;
		} else if (sourceType == MediaSourceType.INSTAGRAM.ordinal()) {
			this.mPhotoSourceType = MediaSourceType.INSTAGRAM;
		} else {
			this.mPhotoSourceType = MediaSourceType.PX500;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gmail.charleszq.picorner.offline.IOfflineViewParameter#getPhotoSourceType
	 * ()
	 */
	@Override
	public MediaSourceType getPhotoSourceType() {
		return mPhotoSourceType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gmail.charleszq.picorner.offline.IOfflineViewParameter#getControlFileName
	 * ()
	 */
	@Override
	public String getControlFileName() {
		StringBuilder sb = new StringBuilder();
		sb.append(OFFLINE_CONTROL_FOLDER_NAME).append(File.separator);
		switch (getPhotoSourceType()) {
		case FLICKR:
			sb.append(OFFLINE_FLICKR_FOLDER_NAME);
			break;
		case INSTAGRAM:
			sb.append(OFFLINE_INSTAGRAM_FOLDER_NAME);
			break;
		case PX500:
			sb.append(OFFLINE_500PX_FOLDER_NAME);
			break;
		}
		sb.append(File.separator);
		sb.append(getPhotoCollectionType().toString());
		sb.append("_"); //$NON-NLS-1$
		sb.append(getPhotoCollectionId()).append(".dat"); //$NON-NLS-1$
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.picorner.offline.IOfflineViewParameter#
	 * getPhotoCollectionId()
	 */
	@Override
	public String getPhotoCollectionId() {
		return mPhotoCollectionId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.picorner.offline.IOfflineViewParameter#
	 * getPhotoCollectionType()
	 */
	@Override
	public OfflinePhotoCollectionType getPhotoCollectionType() {
		return mPhotoCollectionType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gmail.charleszq.picorner.offline.IOfflineViewParameter#getLastUpdateTime
	 * ()
	 */
	@Override
	public long getLastUpdateTime() {
		return mLastUpdateTime;
	}

	/**
	 * Sets the last update time.
	 * 
	 * @param time
	 */
	public void setLastUpdateTime(long time) {
		this.mLastUpdateTime = time;
	}

	@Override
	public String getOfflineImageLocation() {
		StringBuilder sb = new StringBuilder();
		sb.append(OFFLINE_CONTROL_FOLDER_NAME).append(File.separator);
		switch (getPhotoSourceType()) {
		case FLICKR:
			sb.append(OFFLINE_FLICKR_FOLDER_NAME);
			break;
		case INSTAGRAM:
			sb.append(OFFLINE_INSTAGRAM_FOLDER_NAME);
			break;
		case PX500:
			sb.append(OFFLINE_500PX_FOLDER_NAME);
			break;
		}
		sb.append(File.separator);
		sb.append(OFFLINE_INSTAGRAM_FOLDER_NAME);
		sb.append(File.separator);
		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getPhotoSourceType().toString()).append(" "); //$NON-NLS-1$
		sb.append(getPhotoCollectionType().toString()).append(" "); //$NON-NLS-1$
		sb.append(getPhotoCollectionId());
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (!IOfflineViewParameter.class.isInstance(o)) {
			return false;
		}
		return toString().equals(o.toString());
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

}
