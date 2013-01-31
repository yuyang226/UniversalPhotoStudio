/**
 * 
 */
package com.gmail.charleszq.picorner.ui;

import java.util.Comparator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.gmail.charleszq.picorner.BuildConfig;
import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.dp.IPhotosProvider;
import com.gmail.charleszq.picorner.dp.SinglePagePhotosProvider;
import com.gmail.charleszq.picorner.model.GeoLocation;
import com.gmail.charleszq.picorner.model.MediaObject;
import com.gmail.charleszq.picorner.model.MediaObjectCollection;
import com.gmail.charleszq.picorner.msg.IMessageConsumer;
import com.gmail.charleszq.picorner.msg.Message;
import com.gmail.charleszq.picorner.msg.MessageBus;
import com.gmail.charleszq.picorner.ui.command.ICommand;
import com.gmail.charleszq.picorner.ui.command.ICommandDoneListener;
import com.gmail.charleszq.picorner.ui.command.PhotoListCommand;
import com.gmail.charleszq.picorner.ui.helper.PhotoGridAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public abstract class AbstractPhotoGridFragment extends
		AbstractFragmentWithImageFetcher implements OnItemClickListener {

	private static String TAG = AbstractPhotoGridFragment.class.getSimpleName();

	/**
	 * UI controls
	 */
	protected PullToRefreshGridView mPullToRefreshGridView;
	protected GridView mGridView;
	protected ProgressBar mProgressBar;

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
	 * The marker to say no more data, then we don't do loading more.
	 */
	protected boolean mNoMoreData = false;

	ICommandDoneListener<MediaObjectCollection> mCommandDoneListener = new ICommandDoneListener<MediaObjectCollection>() {
		@Override
		public void onCommandDone(ICommand<MediaObjectCollection> command,
				MediaObjectCollection t) {
			if (mPullToRefreshGridView != null)
				mPullToRefreshGridView.onRefreshComplete();
			if (t == null || t.getPhotos().isEmpty()) {
				mNoMoreData = true;
				if (mPullToRefreshGridView != null) {
					mPullToRefreshGridView.setMode(Mode.PULL_FROM_START);
				}
			} else {
				Object comparator = command.getAdapter(Comparator.class);
				mPhotosProvider.loadData(t, comparator == null ? command
						: comparator);
			}
			mAdapter.notifyDataSetChanged();
			mProgressBar.setVisibility(View.INVISIBLE);
		}
	};

	protected IMessageConsumer mConsumer = new IMessageConsumer() {

		@Override
		public boolean consumeMessage(Message msg) {
			switch (msg.getMessageType()) {
			case Message.LIKE_PHOTO:
				for (int i = 0; i < mPhotosProvider.getCurrentSize(); i++) {
					MediaObject photo = mPhotosProvider.getMediaObject(i);
					if (photo.getId().equals(msg.getPhotoId())) {
						photo.setUserLiked(Boolean.parseBoolean(msg
								.getCoreData().toString()));
						break;
					}
				}
				return true;
			case Message.VOTE_PHOTO:
				for (int i = 0; i < mPhotosProvider.getCurrentSize(); i++) {
					MediaObject photo = mPhotosProvider.getMediaObject(i);
					if (photo.getId().equals(msg.getPhotoId())) {
						photo.setUserVoted(true);
						break;
					}
				}
				return true;
			case Message.GEO_INFO_FETCHED:
				for (int i = 0; i < mPhotosProvider.getCurrentSize(); i++) {
					MediaObject photo = mPhotosProvider.getMediaObject(i);
					if (photo.getId().equals(msg.getPhotoId())) {
						photo.setLocation((GeoLocation) msg.getCoreData());
						break;
					}
				}
				return true;
			default:
				return false;
			}
		}
	};

	private OnRefreshListener2<GridView> mOnPullToRefreshListener = new OnRefreshListener2<GridView>() {

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
			mPullToRefreshGridView.onRefreshComplete(); // TODO
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
			loadMoreData();
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
		mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
		mPullToRefreshGridView = (PullToRefreshGridView) v
				.findViewById(R.id.grid_user_photos);
		mPullToRefreshGridView.setOnRefreshListener(mOnPullToRefreshListener);
		mGridView = mPullToRefreshGridView.getRefreshableView();

		mImageThumbSize = getResources().getDimensionPixelSize(
				R.dimen.image_thumbnail_size);
		mImageThumbSpacing = getResources().getDimensionPixelSize(
				R.dimen.image_thumbnail_spacing);

		if (mAdapter == null) {
			mAdapter = new PhotoGridAdapter(getActivity(), mPhotosProvider,
					mImageFetcher);
		}
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(this);

		mGridView.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
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
				});

		bindData();
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mPullToRefreshGridView != null) {
			mPullToRefreshGridView.onRefreshComplete();
		}
		if (mCurrentCommand == null) {
			loadFirstPage();
		}
		MessageBus.addConsumer(mConsumer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onDetach()
	 */
	@Override
	public void onDetach() {
		MessageBus.removeConsumer(mConsumer);
		super.onDetach();
	}

	/**
	 * The sub-classes need to handle that and hide/show message if any.
	 */
	protected void loadMoreData() {

		int currentPhotoSize = mPhotosProvider.getCurrentSize();
		Log.d(TAG, String.format(
				"When loading more, there are %s photos currently", //$NON-NLS-1$
				currentPhotoSize));
		boolean noMoreData = mNoMoreData;
		noMoreData = noMoreData | currentPhotoSize > getMaxPhotoSize();
		if (noMoreData) {
			mPullToRefreshGridView.setMode(Mode.PULL_FROM_START);
			mPullToRefreshGridView.onRefreshComplete();
			return;
		}

		if (BuildConfig.DEBUG)
			Log.d(TAG, "Loading more..."); //$NON-NLS-1$
		if (mCurrentCommand != null)
			mCurrentCommand.loadNextPage();
	}

	private int getMaxPhotoSize() {
		PicornerApplication app = (PicornerApplication) getActivity()
				.getApplication();
		return app.getMaxPhotoSize();
	}

	/**
	 * Loads the first page
	 */
	protected void loadFirstPage() {
		mPullToRefreshGridView.onRefreshComplete();
		mPullToRefreshGridView.setMode(Mode.BOTH);
	}

	/**
	 * Initializes the intent data
	 * 
	 * @param intent
	 */
	abstract protected void initialIntentData(Intent intent);

	/**
	 * Gets the load message
	 * 
	 * @return
	 */
	abstract protected String getLoadingMessage();

	/**
	 * Bind data to UI, the data usually comes from the intent
	 */
	abstract protected void bindData();
}
