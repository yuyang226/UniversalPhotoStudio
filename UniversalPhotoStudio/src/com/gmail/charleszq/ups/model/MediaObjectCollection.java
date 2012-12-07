/**
 * 
 */
package com.gmail.charleszq.ups.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.gmail.charleszq.ups.utils.IConstants;

/**
 * @author charleszq
 * 
 */
public final class MediaObjectCollection {

	private int mCurrentPage = 0;
	private int mTotalCount = 0;
	private int mPageSize = IConstants.SERVICE_PAGE_SIZE;
	
	private Collection<MediaObject> mPhotos;

	public int getCurrentPage() {
		return mCurrentPage;
	}

	public void setCurrentPage(int mCurrentPage) {
		this.mCurrentPage = mCurrentPage;
	}

	public int getTotalCount() {
		return mTotalCount;
	}

	public void setTotalCount(int mTotalCount) {
		this.mTotalCount = mTotalCount;
	}

	public int getPageSize() {
		return mPageSize;
	}

	public void setPageSize(int mPageSize) {
		this.mPageSize = mPageSize;
	}

	@SuppressWarnings("unchecked")
	public Collection<MediaObject> getPhotos() {
		return mPhotos == null ? Collections.EMPTY_LIST : mPhotos;
	}
	
	public void addPhoto(MediaObject photo) {
		if( mPhotos == null ) {
			mPhotos = new ArrayList<MediaObject>();
		}
		mPhotos.add(photo);
	}

}
