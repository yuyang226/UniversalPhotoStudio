/**
 * 
 */
package com.gmail.charleszq.ups.dp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.gmail.charleszq.ups.model.MediaObject;
import com.gmail.charleszq.ups.model.MediaObjectCollection;

/**
 * @author Charles(charleszq@gmail.com)
 * 
 */
public class SinglePagePhotosProvider implements IPhotosProvider {

	/**
	 * The total number on the server side.
	 */
	private int mTotalOnServer;

	/**
	 * Total number for the current page.
	 */
	private int mTotal;

	/**
	 * The current page
	 */
	private int mCurrentPage = 0;

	/**
	 * the current page size.
	 */
	private int mCurrentPageSize = 0;

	private List<MediaObject> mPhotos;
	private Set<IDataChangedListener> mListeners;
	
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
	 * @see com.gmail.charleszq.ups.dp.IPhotosProvider#getTotalCount()
	 */
	@Override
	public int getTotalCount() {
		return mTotal;
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
	 * com.gmail.charleszq.ups.dp.IDataProvider#addDataChangeListener(com.gmail
	 * .charleszq.ups.dp.IDataChangedListener)
	 */
	@Override
	public void addDataChangeListener(IDataChangedListener listener) {
		if (mListeners == null) {
			mListeners = new HashSet<IDataChangedListener>();
		}
		mListeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ups.dp.IPhotosProvider#notifyDataChanged()
	 */
	@Override
	public void notifyDataChanged() {
		if (mListeners != null) {
			for (IDataChangedListener lis : mListeners) {
				lis.onDataChanged();
			}
		}
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
		mTotalOnServer = list.getTotalCount();
		mCurrentPageSize = list.getPageSize();
		mTotal = mCurrentPageSize;
		mCurrentPage = list.getCurrentPage();
		if (mPhotos == null)
			mPhotos = new ArrayList<MediaObject>();
		if( source != mCurrentSource ) {
			mPhotos.clear();
			mCurrentSource = source;
		}
		mPhotos.addAll(list.getPhotos());
		if (mPhotos.size() < mCurrentPageSize) {
			mTotal = mPhotos.size();
		}
	}

	@Override
	public boolean hasMorePage() {
		if (mTotal < mCurrentPageSize) {
			return false;
		} else {
			if (mCurrentPageSize * (mCurrentPage + 1) <= mTotalOnServer) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int getCurrentPage() {
		return mCurrentPage;
	}

}
