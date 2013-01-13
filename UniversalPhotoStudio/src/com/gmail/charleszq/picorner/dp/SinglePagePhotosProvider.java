/**
 * 
 */
package com.gmail.charleszq.picorner.dp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.util.Log;

import com.gmail.charleszq.picorner.model.MediaObject;
import com.gmail.charleszq.picorner.model.MediaObjectCollection;

/**
 * @author Charles(charleszq@gmail.com)
 * 
 */
public class SinglePagePhotosProvider implements IPhotosProvider {

	/**
	 * auto gen sid
	 */
	private static final long serialVersionUID = 1771926524223469593L;

	/**
	 * Log tag
	 */
	private static final String TAG = SinglePagePhotosProvider.class.getName();

	/**
	 * The photo list.
	 */
	private List<MediaObject> mPhotos;
	
	private Set<String> mPhotoIds;

	/**
	 * The current source which populate photos into this, usually, the command.
	 */
	transient private Object mCurrentSource = null;

	public SinglePagePhotosProvider(MediaObjectCollection photos) {
		mPhotoIds = new HashSet<String>();
		loadData(photos, null);
	}

	@Override
	public MediaObject getMediaObject(int index) {
		return mPhotos.get(index);
	}

	@Override
	public int getCurrentSize() {
		return mPhotos.size();
	}

	@Override
	public void loadData(MediaObjectCollection list, Object source) {
		if (list == null) {
			return;
		}
		if (mPhotos == null)
			mPhotos = new ArrayList<MediaObject>();
		if (source != mCurrentSource) {
			Log.d(TAG, String.format(
					"before clear previous photos, there were %s in it", //$NON-NLS-1$
					mPhotos.size()));
			mPhotos.clear();
			mPhotoIds.clear();
			mCurrentSource = source;
		}
		for( MediaObject p : list.getPhotos() ) {
			if(mPhotoIds.contains(p.getId())) {
				Log.d(TAG, "Duplicate photo."); //$NON-NLS-1$
				continue;
			}
			mPhotos.add(p);
			mPhotoIds.add(p.getId());
		}
		Log.d(TAG, String.format("now there are %s photos.", mPhotos.size())); //$NON-NLS-1$
	}
}
