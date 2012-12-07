/**
 * 
 */
package com.gmail.charleszq.ups.dp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import com.gmail.charleszq.ups.model.MediaObject;
import com.gmail.charleszq.ups.model.MediaObjectCollection;
import com.gmail.charleszq.ups.utils.IConstants;

/**
 * @author Charles(charleszq@gmail.com)
 * 
 * Note: Not used for now.
 */
@SuppressLint("UseSparseArrays")
public class DefaultPhotoListProvider implements IPhotosProvider {

	private Set<IDataChangedListener> mListeners = null;
	private int mTotalCount = 0;

	/**
	 * The map to store photos and separate them into pages.
	 */
	private Map<Integer, List<MediaObject>> mPageMap = null;

	/**
	 * The source object which is responsible for loading data. usually, it's
	 * the command from the UI.
	 */
	private Object mSource = null;

	public DefaultPhotoListProvider(MediaObjectCollection list) {
		mPageMap = new HashMap<Integer, List<MediaObject>>();
		List<MediaObject> page = new ArrayList<MediaObject>();
		page.addAll(list.getPhotos());
		mPageMap.put(0, page);
		mTotalCount = list.getTotalCount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ups.dp.IDataProvider#getTotalCount()
	 */
	@Override
	public int getTotalCount() {
		return mTotalCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ups.dp.IDataProvider#getMediaObject(int)
	 */
	@Override
	public MediaObject getMediaObject(int index) {
		int page = index / IConstants.SERVICE_PAGE_SIZE;
		if (this.mPageMap.containsKey(page)) {
			List<MediaObject> photos = mPageMap.get(page);
			return photos.get(index % IConstants.SERVICE_PAGE_SIZE);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ups.dp.IDataProvider#getCurrentSize()
	 */
	@Override
	public int getCurrentSize() {
		int count = 0;
		for (List<MediaObject> list : mPageMap.values()) {
			count += list.size();
		}
		return count;
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
	 * @see com.gmail.charleszq.ups.dp.IDataProvider#notifyDataChanged()
	 */
	@Override
	public void notifyDataChanged() {
		if (mListeners != null) {
			for (IDataChangedListener lis : mListeners) {
				lis.onDataChanged();
			}
		}
	}

	@Override
	public void loadData(MediaObjectCollection list, Object source) {

		boolean reset = false;
		int serviceCurrentPage = list.getCurrentPage() - 1;
		if (source != mSource || serviceCurrentPage == 0) {
			reset = true;
		}
		this.mSource = source;
		List<MediaObject> photos = (List<MediaObject>) list.getPhotos();
		if (reset) {
			this.mTotalCount = list.getTotalCount();
			mPageMap.clear();
			mPageMap.put(0, photos);
		} else {
			int page = list.getCurrentPage() - 1;
			if (!mPageMap.containsKey(page)) {
				mPageMap.put(page, photos);
			}
		}
	}
	
	public boolean containsPage(int page) {
		return mPageMap.containsKey(page);
	}

	@Override
	public boolean hasMorePage() {
		return false;
	}

	@Override
	public int getCurrentPage() {
		// TODO Auto-generated method stub
		return 0;
	}

}
