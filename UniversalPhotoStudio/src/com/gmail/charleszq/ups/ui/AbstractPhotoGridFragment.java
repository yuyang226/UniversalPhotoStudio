/**
 * 
 */
package com.gmail.charleszq.ups.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.gmail.charleszq.ups.R;
import com.gmail.charleszq.ups.dp.IPhotosProvider;
import com.gmail.charleszq.ups.dp.SinglePagePhotosProvider;
import com.gmail.charleszq.ups.model.MediaObjectCollection;
import com.gmail.charleszq.ups.ui.adapter.OneTimeScrollListener;
import com.gmail.charleszq.ups.ui.adapter.PhotoGridAdapter;
import com.gmail.charleszq.ups.ui.command.ICommand;
import com.gmail.charleszq.ups.ui.command.ICommandDoneListener;
import com.gmail.charleszq.ups.ui.command.PhotoListCommand;
import com.gmail.charleszq.ups.utils.IConstants;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public abstract class AbstractPhotoGridFragment extends
		AbstractFragmentWithImageFetcher implements OnItemClickListener {

	/**
	 * UI controls
	 */
	protected GridView mGridView;
	protected TextView mLoadingMessageText;

	/**
	 * Photo grid size information.
	 */
	protected int mImageThumbSize;
	protected int mImageThumbSpacing;

	/**
	 * The data provider to store photos.
	 */
	protected IPhotosProvider mPhotosProvider = new SinglePagePhotosProvider(
			new MediaObjectCollection());

	/**
	 * The photo grid adapter
	 */
	protected PhotoGridAdapter mAdapter;

	/**
	 * Save the current command, so we can load more data later.
	 */
	protected PhotoListCommand mCurrentCommand;

	/**
	 * m1: 'Photo of user' m2: 'Loading photos of user..."
	 */
	protected String mLoadingMessage;

	/**
	 * The marker to say no more data, then we don't do loading more.
	 */
	protected boolean mNoMoreData = false;

	protected OneTimeScrollListener mScrollListener = new OneTimeScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView absListView,
				int scrollState) {
			// Pause fetcher to ensure smoother scrolling when flinging
			if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
				mImageFetcher.setPauseWork(true);
			} else {
				mImageFetcher.setPauseWork(false);
			}
		}

		@Override
		protected void loadMoreData() {
			AbstractPhotoGridFragment.this.loadMoreData();
		}
	};
	
	ICommandDoneListener<MediaObjectCollection> mCommandDoneListener = new ICommandDoneListener<MediaObjectCollection>() {
		@Override
		public void onCommandDone(ICommand<MediaObjectCollection> command,
				MediaObjectCollection t) {
			if (t == null || t.getPhotos().isEmpty()) {
				mNoMoreData = true;
			} else {
				mPhotosProvider.loadData(t, command);
				mAdapter.notifyDataSetChanged();
			}
			if (mLoadingMessageText != null) {
				mLoadingMessageText.setVisibility(View.GONE);
			}
		}
	};

	/**
	 * 
	 */
	public AbstractPhotoGridFragment() {

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		initialIntentData(getActivity().getIntent());

		mLoadingMessage = getLoadingMessage();

		if (mGridView != null) {
			mGridView.setOnScrollListener(null);
		}
		if (mCurrentCommand != null) {
			mCurrentCommand.attacheContext(getActivity());
		}
		this.setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.user_photo_list_fragment, null);
		// layout ui controls
		mGridView = (GridView) v.findViewById(R.id.grid_user_photos);
		mLoadingMessageText = (TextView) v.findViewById(R.id.txt_user_info);

		if (mImageFetcher == null) {
			mImageThumbSize = getResources().getDimensionPixelSize(
					R.dimen.image_thumbnail_size);
			mImageThumbSpacing = getResources().getDimensionPixelSize(
					R.dimen.image_thumbnail_spacing);
			initializeImageFetcher(IConstants.IMAGE_THUMBS_CACHE_DIR,
					mImageThumbSize);
		}

		if (mAdapter == null) {
			mAdapter = new PhotoGridAdapter(getActivity(), mPhotosProvider,
					mImageFetcher);
		}
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(this);
		mGridView.setOnScrollListener(mScrollListener);

		// This listener is used to get the final width of the GridView and then
		// calculate the
		// number of columns and the width of each column. The width of each
		// column is variable
		// as the GridView has stretchMode=columnWidth. The column width is used
		// to set the height
		// of each view so we get nice square thumbnails.
		mGridView.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						if (mAdapter.getNumColumns() == 0) {
							final int numColumns = (int) Math.floor(mGridView
									.getWidth()
									/ (mImageThumbSize + mImageThumbSpacing));
							if (numColumns > 0) {
								final int columnWidth = (mGridView.getWidth() / numColumns)
										- mImageThumbSpacing;
								mAdapter.setNumColumns(numColumns);
								mAdapter.setItemHeight(columnWidth);
							}
						}
					}
				});

		bindData();
		if (mCurrentCommand == null) {
			loadFirstPage();
		}
		return v;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
	}

	/**
	 * The sub-classes need to handle that and hide/show message if any.
	 */
	protected void loadMoreData() {
		
		boolean noMoreData = mNoMoreData;
		noMoreData = noMoreData | mPhotosProvider.getCurrentSize() >= 300;
		if( mPhotosProvider.getCurrentSize() > 0 ) {
			int pageSize = (Integer) mCurrentCommand.getAdapter(Integer.class);
			noMoreData = noMoreData | mPhotosProvider.getCurrentSize() < pageSize;
		}
		if (noMoreData) {
			mLoadingMessageText.setVisibility(View.GONE);
			return;
		}
		mLoadingMessageText.setVisibility(View.VISIBLE);
		mCurrentCommand.loadNextPage();
	}
	
	abstract protected void loadFirstPage();
	abstract protected void initialIntentData(Intent intent);
	abstract protected String getLoadingMessage();
	abstract protected void bindData();

}
