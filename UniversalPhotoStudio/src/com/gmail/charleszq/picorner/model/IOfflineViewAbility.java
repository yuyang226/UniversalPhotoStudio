/**
 * 
 */
package com.gmail.charleszq.picorner.model;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public interface IOfflineViewAbility {
	
	/**
	 * The key of photo source type for gson
	 */
	static final String KEY_PHOTO_SOURCE_TYPE = "s"; //$NON-NLS-1$
	
	/**
	 * The key of photo collection type:
	 * <ul>
	 * <li>Photo set</li>
	 * </ul>
	 */
	static final String KEY_PHOTO_COLLECTION_TYPE = "c"; //$NON-NLS-1$
	
	/**
	 * the key of the photo collection id. for example, the photo set id.
	 */
	static final String KEY_PHOTO_COLLECTION_ID = "id"; //$NON-NLS-1$
	
	/**
	 * the key of the time when this collection was update lately.
	 */
	static final String KEY_LAST_UPDATE_TS = "t"; //$NON-NLS-1$
	
	/**
	 * The source type
	 * @return
	 */
	MediaSourceType getPhotoSourceType();
	
	/**
	 * The control file is where we save the photo list information of this photo list, usually, it 
	 * should be: <br/>
	 * <root>/offline/<photo source type>/<photo_collection_type>+<photo_collection_id>.dat
	 * @return
	 */
	String getControlFileName();
	
	/**
	 * The saved image (big size) location, usually it should be: <br/>
	 * <root>/offline/<photo source type>
	 * @return
	 */
	String getOfflineImageLocation();
	
	/**
	 * Returns the photo collection id.
	 * @return
	 */
	String getPhotoCollectionId();
	
	/**
	 * Photo set for now only.
	 * @return
	 */
	OfflinePhotoCollectionType getPhotoCollectionType();
	
	/**
	 * The last update time.
	 * @return
	 */
	long getLastUpdateTime();

}
