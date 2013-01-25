/**
 * 
 */
package com.gmail.charleszq.picorner.model;

import android.annotation.SuppressLint;
import java.io.Serializable;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public final class FlickrTagSearchParameter implements Serializable {

	/**
	 * auto sid
	 */
	private static final long	serialVersionUID	= 5631648625115371072L;

	private String				mTags;
	private FlickrTagSearchMode	mSearchMode			= FlickrTagSearchMode.ALL;
	private boolean				mSearchInCommon		= false;
	private boolean				mHasGeoInformation	= false;

	public String getTags() {
		return mTags;
	}

	public boolean isSearchInCommon() {
		return mSearchInCommon;
	}

	public void setSearchInCommon(boolean mSearchInCommon) {
		this.mSearchInCommon = mSearchInCommon;
	}

	public boolean isHasGeoInformation() {
		return mHasGeoInformation;
	}

	public void setHasGeoInformation(boolean mHasGeoInformation) {
		this.mHasGeoInformation = mHasGeoInformation;
	}

	public void setTags(String mTags) {
		this.mTags = mTags;
	}

	public FlickrTagSearchMode getSearchMode() {
		return mSearchMode;
	}

	public void setSearchMode(FlickrTagSearchMode mSearchMode) {
		this.mSearchMode = mSearchMode;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("("); //$NON-NLS-1$
		sb.append(mSearchMode.toString());
		sb.append(")").append(mTags.trim()); //$NON-NLS-1$
		sb.append(Boolean.toString(mSearchInCommon));
		sb.append(Boolean.toString(mHasGeoInformation));
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (!FlickrTagSearchParameter.class.isInstance(o)) {
			return false;
		}
		return toString().equals(o.toString());
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	public enum FlickrTagSearchMode {
		ANY, ALL;

		@SuppressLint("DefaultLocale")
		@Override
		public String toString() {
			String s = super.toString();
			return s.toLowerCase();
		}
	}

}
