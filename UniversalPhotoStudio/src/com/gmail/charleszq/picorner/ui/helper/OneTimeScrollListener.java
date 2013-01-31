/**
 * 
 */
package com.gmail.charleszq.picorner.ui.helper;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public abstract class OneTimeScrollListener implements OnScrollListener {

	protected String TAG = getClass().getName();

	private int mVisibleThreshold = 5;
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
			}
		}
		if (!mLoading
				&& (totalItemCount - visibleItemCount) <= (firstVisibleItem + mVisibleThreshold)) {
			loadMoreData();
			mLoading = true;
		}

	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		//do nothing.
	}

	/**
	 * The UI needs to implement this and load more data if any, otherwise, UI
	 * needs to do another stuffs like hiding the messsages.
	 */
	abstract protected void loadMoreData();
}
