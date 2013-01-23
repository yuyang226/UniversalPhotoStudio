/**
 * 
 */
package com.gmail.charleszq.picorner.offline;

import java.util.List;

import android.content.Context;

import com.gmail.charleszq.picorner.model.MediaObject;

/**
 * Represents the service to get the photo collection information back from
 * server and save on sdcard, then start to download images.
 * <p/>
 * Each service needs to handle the case that 2nd time fetch, it only needs to
 * fetch the delta, not all the photos
 * 
 * @author charles(charleszq@gmail.com)
 * 
 */
public interface IOfflinePhotoCollectionProcessor {

	/**
	 * Starts to fetch photo collection information and download images.
	 * 
	 * @param ctx
	 *            the context from which we can get the auto information.
	 * @param param
	 */
	void process(Context ctx, IOfflineViewParameter param);

	/**
	 * Returns the cached photos. if the photo collection information is not
	 * ready upon the client call, return <code>null</code>
	 * 
	 * @return
	 */
	List<MediaObject> getCachedPhotos(Context ctx, IOfflineViewParameter param);

}
