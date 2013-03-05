/**
 * 
 */
package com.gmail.charleszq.picorner.ui;

import java.util.Comparator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.SPUtil;
import com.gmail.charleszq.picorner.dp.IPhotosProvider;
import com.gmail.charleszq.picorner.dp.SinglePagePhotosProvider;
import com.gmail.charleszq.picorner.model.GeoLocation;
import com.gmail.charleszq.picorner.model.MediaObject;
import com.gmail.charleszq.picorner.model.MediaObjectCollection;
import com.gmail.charleszq.picorner.model.MediaSourceType;
import com.gmail.charleszq.picorner.msg.IMessageConsumer;
import com.gmail.charleszq.picorner.msg.Message;
import com.gmail.charleszq.picorner.msg.MessageBus;
import com.gmail.charleszq.picorner.ui.command.ICommand;
import com.gmail.charleszq.picorner.ui.command.ICommandDoneListener;
import com.gmail.charleszq.picorner.ui.command.PhotoListCommand;
import com.gmail.charleszq.picorner.ui.helper.OneTimeScrollListener;
import com.gmail.charleszq.picorner.ui.helper.PhotoGridAdapter;
import com.gmail.charleszq.picorner.utils.IConstants;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

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

	protected OneTimeScrollListener mScrollListener = null;

	ICommandDoneListener<MediaObjectCollection> mCommandDoneListener = new ICommandDoneListener<MediaObjectCollection>() {
		@Override
		public void onCommandDone(ICommand<MediaObjectCollection> command,
				MediaObjectCollection t) {
			if (t == null || t.getPhotos().isEmpty()) {
				mNoMoreData = true;
			} else {
				Object comparator = command.getAdapter(Comparator.class);
				mPhotosProvider.loadData(t, command, comparator);
				mAdapter.notifyDataSetChanged();
			}
			if (mLoadingMessageText != null) {
				mLoadingMessageText.setVisibility(View.GONE);
			}
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
			case Message.CANCEL_COMMAND:
				if (mCurrentCommand != null
						&& mCurrentCommand != msg.getCoreData()) {
					mCurrentCommand.cancel();
				}
				if( mLoadingMessageText != null ) {
					mLoadingMessageText.setVisibility(View.GONE);
				}
				return true;
			default:
				return false;
			}
		}
	};

	private int mCurrentSelectedIndex = -1;
	private ActionMode mCurrentActionMode;
	private View mCurrentSelectedView = null;
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			MenuItem mapItem = menu.findItem(R.id.menu_item_view_on_map);
			MediaObject photo = mPhotosProvider
					.getMediaObject(mCurrentSelectedIndex);
			mapItem.setVisible(photo.getLocation() != null);

			if (photo.getMediaSource().equals(MediaSourceType.INSTAGRAM)) {
				MenuItem exifItem = menu.findItem(R.id.menu_item_view_exif);
				exifItem.setVisible(false);
			}
			PicornerApplication app = (PicornerApplication) getActivity()
					.getApplication();
			boolean isMyPhoto = app.isMyOwnPhoto(photo);
			if (isMyPhoto && photo.getMediaSource() == MediaSourceType.FLICKR) {
				menu.setGroupVisible(R.id.group_my_flickr_photo, true);
			} else {
				menu.setGroupVisible(R.id.group_my_flickr_photo, false);
			}
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mCurrentActionMode = null;
			mCurrentSelectedIndex = -1;
			if (mCurrentSelectedView != null) {
				mCurrentSelectedView.setAlpha(1f);
				mCurrentSelectedView = null;
			}
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater mf = mode.getMenuInflater();
			mf.inflate(R.menu.photo_detail_common, menu);
			mf.inflate(R.menu.my_flickr_photo_menus, menu);
			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.menu_item_comment:
				showPhotoDetailWithPage(PhotoDetailActivity.COMMENT_PAGE);
				break;
			case R.id.menu_item_photo_set:
				showPhotoDetailWithPage(PhotoDetailActivity.MY_F_ORG_PHOTO_SET_PAGE);
				break;
			case R.id.menu_item_add_to_group:
				showPhotoDetailWithPage(PhotoDetailActivity.MY_F_ORG_GROUP_PAGE);
				break;
			case R.id.menu_item_view_exif:
				showPhotoDetailWithPage(PhotoDetailActivity.EXIF_PAGE);
				break;
			case R.id.menu_item_view_on_map:
				showPhotoDetailWithPage(PhotoDetailActivity.MAP_PAGE);
				break;
			}
			if (mCurrentSelectedView != null) {
				mCurrentSelectedView.setAlpha(1f);
				mCurrentSelectedView = null;
			}
			mode.finish();
			return true;
		}

		private void showPhotoDetailWithPage(String pageIndex) {
			Intent detailIntent = new Intent(getActivity(),
					PhotoDetailActivity.class);
			detailIntent.putExtra(ImageDetailActivity.DP_KEY, mPhotosProvider);
			detailIntent.putExtra(ImageDetailActivity.LARGE_IMAGE_POSITION,
					mCurrentSelectedIndex);
			if (pageIndex != null)
				detailIntent.putExtra(PhotoDetailActivity.DETAIL_PAGE_KEY,
						pageIndex);
			startActivity(detailIntent);
		}
	};

	/**
	 * 
	 */
	public AbstractPhotoGridFragment() {

	}
	
	void exitActionMode() {
		if( mCurrentActionMode != null ) {
			mCurrentActionMode.finish();
		}
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
		mScrollListener = new GridOnScrollListener(this);
		PauseOnScrollListener pauseListener = new PauseOnScrollListener(false,
				true, mScrollListener);
		mGridView.setOnScrollListener(pauseListener);

		mGridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mCurrentActionMode != null)
					return false;

				mCurrentSelectedIndex = (int) id;
				mCurrentActionMode = getActivity().startActionMode(
						mActionModeCallback);
				mCurrentSelectedView = view;
				mCurrentSelectedView.setAlpha(0.5f);
				return true;
			}
		});

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
		int maxSize = SPUtil.getMaxPhotoSize(getActivity());
		noMoreData = noMoreData | currentPhotoSize > maxSize;
		if (currentPhotoSize > 0) {
			noMoreData = noMoreData
					| currentPhotoSize < IConstants.DEF_MIN_PAGE_SIZE;
		}
		if (noMoreData) {
			Log.d(TAG, "There is no more data."); //$NON-NLS-1$
			mLoadingMessageText.setVisibility(View.GONE);
			return;
		}

		Log.d(TAG, "Loading more..."); //$NON-NLS-1$
		mLoadingMessageText.setVisibility(View.VISIBLE);
		if (mCurrentCommand != null)
			mCurrentCommand.loadNextPage();
	}

	/**
	 * Loads the first page
	 */
	abstract protected void loadFirstPage();

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

	protected static class GridOnScrollListener extends OneTimeScrollListener {

		private AbstractPhotoGridFragment mFragment;

		protected GridOnScrollListener(AbstractPhotoGridFragment fragment) {
			this.mFragment = fragment;
		}

		@Override
		protected void loadMoreData() {
			mFragment.loadMoreData();
		}

		@Override
		protected void showGridTitle(boolean show) {
			mFragment.exitActionMode();
		}
	}
}
