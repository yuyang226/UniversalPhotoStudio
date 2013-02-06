/**
 * 
 */
package com.gmail.charleszq.picorner.offline;

import com.gmail.charleszq.picorner.model.MediaSourceType;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class FlickrOfflineParameter extends AbstractOfflineParameter {

	/**
	 * sid
	 */
	private static final long serialVersionUID = -4314832109544573962L;

	/**
	 * @param photoSourceType
	 * @param collectionType
	 * @param collectionId
	 */
	public FlickrOfflineParameter(OfflinePhotoCollectionType collectionType,
			String collectionId, String title) {
		super(MediaSourceType.FLICKR, collectionType, collectionId, title);
	}

	@Override
	public IOfflinePhotoCollectionProcessor getPhotoCollectionProcessor() {
		IOfflinePhotoCollectionProcessor processor = null;
		switch (getPhotoCollectionType()) {
		case PHOTO_SET:
			processor = new FlickrPhotoSetOfflineProcessor();
			break;
		}
		return processor;
	}

}
