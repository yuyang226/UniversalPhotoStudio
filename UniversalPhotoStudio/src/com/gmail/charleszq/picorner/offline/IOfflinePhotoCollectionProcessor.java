/**
 * 
 */
package com.gmail.charleszq.picorner.offline;

import java.io.IOException;
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
	 * @param download
	 *            <code>true</code> to download photos even if there is no
	 *            update; <code>false</code> otherwise.
	 */
	void process(Context ctx, IOfflineViewParameter param, boolean download);

	/**
	 * Returns the cached photos. if the photo collection information is not
	 * ready upon the client call, return <code>null</code>
	 * 
	 * @return
	 */
	List<MediaObject> getCachedPhotos(Context ctx, IOfflineViewParameter param);

	/**
	 * Removes all cached photos for the given <code>param</code>, returns the
	 * photo count removed, -1 if the control file of this parameter does not
	 * even exist or encountered errors when reading the control file.
	 * 
	 * @param param
	 * @return
	 */
	int removeCachedPhotos(Context ctx, IOfflineViewParameter param);

	/**
	 * Exports the cached photos to a folder.
	 * @param ctx
	 * @param param
	 * @param foldername
	 * @param overwrite
	 * @return the number of how many photos exported.
	 */
	int exportCachedPhotos(Context ctx, IOfflineViewParameter param,
			String foldername, boolean overwrite) throws IOException;

}
