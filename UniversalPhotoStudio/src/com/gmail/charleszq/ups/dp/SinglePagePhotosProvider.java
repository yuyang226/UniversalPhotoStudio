/**
 * 
 */
package com.gmail.charleszq.ups.dp;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.gmail.charleszq.ups.model.MediaObject;
import com.gmail.charleszq.ups.model.MediaObjectCollection;

/**
 * @author Charles(charleszq@gmail.com)
 * 
 */
public class SinglePagePhotosProvider implements IPhotosProvider {

	private static final String TAG = SinglePagePhotosProvider.class.getName();

	private List<MediaObject> mPhotos;

	/**
	 * The current source which populate photos into this, usually, the command.
	 */
	private Object mCurrentSource = null;

	public SinglePagePhotosProvider(MediaObjectCollection photos) {
		loadData(photos, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ups.dp.IPhotosProvider#getMediaObject(int)
	 */
	@Override
	public MediaObject getMediaObject(int index) {
		return mPhotos.get(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ups.dp.IPhotosProvider#getCurrentSize()
	 */
	@Override
	public int getCurrentSize() {
		return mPhotos.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gmail.charleszq.ups.dp.IPhotosProvider#loadData(com.gmail.charleszq
	 * .ups.model.MediaObjectCollection, java.lang.Object)
	 */
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
			mCurrentSource = source;
		}
		for( MediaObject p : list.getPhotos() ) {
			if( mPhotos.contains(p) ) {
				Log.w(TAG, "Duplication photo."); //$NON-NLS-1$
				continue;
			}
			mPhotos.add(p);
		}
		Log.d(TAG, String.format("now there are %s photos.", mPhotos.size())); //$NON-NLS-1$
	}
}
