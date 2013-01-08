/**
 * 
 */
package com.gmail.charleszq.picorner.task.flickr;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.os.Environment;
import android.util.Log;

import com.gmail.charleszq.picorner.task.AbstractGeneralTask;
import com.gmail.charleszq.picorner.utils.IConstants;
import com.gmail.charleszq.picorner.utils.ModelUtils;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class FetchFlickrUserPhotoCollectionFromCacheTask extends
		AbstractGeneralTask<Void, Integer, List<Object>> {

	@Override
	protected List<Object> doInBackground(Void... params) {
		File bsRoot = new File(Environment.getExternalStorageDirectory(),
				IConstants.SD_CARD_FOLDER_NAME);
		if (!bsRoot.exists() && !bsRoot.mkdir()) {
			return null;
		}
		File cacheFile = new File(bsRoot, IConstants.FLICKR_USER_POOL_FILE_NAME);
		if (!cacheFile.exists()) {
			return null;
		}

		try {
			List<Object> pools = ModelUtils.readFlickrUserPhotoPools(cacheFile);
			Log.d( TAG, "Pools got from cache: " + pools.size()); //$NON-NLS-1$
			return pools;
		} catch (IOException e) {
			Log.w(TAG, "Unable to read from cache file: " + e.getMessage()); //$NON-NLS-1$
			return null;
		}
	}

}
