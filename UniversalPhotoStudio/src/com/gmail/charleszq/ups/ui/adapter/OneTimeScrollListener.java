/**
 * 
 */
package com.gmail.charleszq.ups.ui.adapter;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public abstract class OneTimeScrollListener implements OnScrollListener {

	private int mVisibleThreshold = 5;
//	private int mCurrentPage = 0;
	private int mPreviousTotal = 0;
	private boolean mLoading = true;

	/**
     * 
     */
	public OneTimeScrollListener() {

	}

	/**
	 * 
	 */
	public OneTimeScrollListener(int threshold) {
		mVisibleThreshold = threshold;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AbsListView.OnScrollListener#onScrollStateChanged(android
	 * .widget.AbsListView, int)
	 */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AbsListView.OnScrollListener#onScroll(android.widget.
	 * AbsListView, int, int, int)
	 */
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (mLoading) {
			if (totalItemCount > mPreviousTotal) {
				mLoading = false;
				mPreviousTotal = totalItemCount;
//				mCurrentPage++;
			}
		}
		if (!mLoading
				&& (totalItemCount - visibleItemCount) <= (firstVisibleItem + mVisibleThreshold)) {
			loadMoreData();
			mLoading = true;
		}

	}

	abstract public void loadMoreData();

}
