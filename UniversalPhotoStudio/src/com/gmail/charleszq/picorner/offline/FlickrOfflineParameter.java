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
			String collectionId) {
		super(MediaSourceType.FLICKR, collectionType, collectionId);
	}

	public FlickrOfflineParameter(int collectionType, String collectionId) {
		super(MediaSourceType.FLICKR.ordinal(), collectionType, collectionId);
	}

}
