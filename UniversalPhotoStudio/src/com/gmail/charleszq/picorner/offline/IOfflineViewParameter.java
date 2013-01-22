/**
 * 
 */
package com.gmail.charleszq.picorner.offline;

import java.io.Serializable;

import com.gmail.charleszq.picorner.model.MediaSourceType;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public interface IOfflineViewParameter extends Serializable {

	/**
	 * The folder name of offline related control file, and image caches.
	 */
	static final String OFFLINE_CONTROL_FOLDER_NAME = "offline"; //$NON-NLS-1$
	static final String OFFLINE_FLICKR_FOLDER_NAME = "f"; //$NON-NLS-1$
	static final String OFFLINE_INSTAGRAM_FOLDER_NAME = "i"; //$NON-NLS-1$
	static final String OFFLINE_500PX_FOLDER_NAME = "p"; //$NON-NLS-1$
	static final String OFFLINE_IMAGE_FOLDER_NAME = "pic"; //$NON-NLS-1$

	/**
	 * the control file for the offline repository control file.
	 */
	static final String OFFLINE_REPO_FILE_NAME = "repo.dat"; //$NON-NLS-1$

	/**
	 * The key of where we save the offline parameter into intent extra.
	 */
	static final String OFFLINE_PARAM_INTENT_KEY = "offline.param"; //$NON-NLS-1$
	static final String OFFLINE_PARAM_INTENT_ADD_REMOVE_REFRESH_KEY = "offline.add.param"; //$NON-NLS-1$

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
	 * 
	 * @return
	 */
	MediaSourceType getPhotoSourceType();

	/**
	 * The control file is where we save the photo list information of this
	 * photo list, usually, it should be: <br/>
	 * <root>/offline/<photo source
	 * type>/<photo_collection_type>_<photo_collection_id>.dat
	 * 
	 * @return
	 */
	String getControlFileName();

	/**
	 * The saved image (big size) location, usually it should be: <br/>
	 * <root>/offline/<photo source type>
	 * 
	 * @return
	 */
	String getOfflineImageLocation();

	/**
	 * Returns the photo collection id.
	 * 
	 * @return
	 */
	String getPhotoCollectionId();

	/**
	 * Photo set for now only.
	 * 
	 * @return
	 */
	OfflinePhotoCollectionType getPhotoCollectionType();

	/**
	 * The last update time.
	 * 
	 * @return
	 */
	long getLastUpdateTime();

	/**
	 * Returns the corresponding photo collection service for this param.
	 * 
	 * @return
	 */
	IOfflinePhotoCollectionProcessor getPhotoCollectionProcessor();

}
