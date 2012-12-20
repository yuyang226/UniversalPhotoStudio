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
import android.widget.GridView;
import android.widget.TextView;

import com.gmail.charleszq.ups.R;
import com.gmail.charleszq.ups.dp.IPhotosProvider;
import com.gmail.charleszq.ups.dp.SinglePagePhotosProvider;
import com.gmail.charleszq.ups.model.Author;
import com.gmail.charleszq.ups.model.MediaObjectCollection;
import com.gmail.charleszq.ups.model.MediaSourceType;
import com.gmail.charleszq.ups.ui.adapter.OneTimeScrollListener;
import com.gmail.charleszq.ups.ui.adapter.PhotoGridAdapter;
import com.gmail.charleszq.ups.ui.command.ICommand;
import com.gmail.charleszq.ups.ui.command.ICommandDoneListener;
import com.gmail.charleszq.ups.ui.command.PhotoListCommand;
import com.gmail.charleszq.ups.ui.command.ig.InstagramUserPhotosCommand;
import com.gmail.charleszq.ups.utils.IConstants;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class UserPhotoListFragment extends AbstractFragmentWithImageFetcher {

	/**
	 * The current user.
	 */
	private Author mCurrentUser;

	/**
	 * the ordinal of <code>MediaSourceType</code>
	 */
	private int mMedisSourceType = 0;

	/**
	 * UI controls
	 */
	private GridView mGridView;
	private TextView mUserInfoText;

	/**
	 * Photo grid size information.
	 */
	private int mImageThumbSize;
	private int mImageThumbSpacing;

	/**
	 * The data provider to store photos.
	 */
	private IPhotosProvider mPhotosProvider = new SinglePagePhotosProvider(
			new MediaObjectCollection());

	/**
	 * The photo grid adapter
	 */
	private PhotoGridAdapter mAdapter;

	/**
	 * Save the current command, so we can load more data later.
	 */
	private PhotoListCommand mCurrentCommand;
	
	/**
	 * m1: 'Photo of user'
	 * m2: 'Loading photos of user..."
	 */
	private String mMessage1, mMessage2;

	/**
	 * Constructor
	 */
	public UserPhotoListFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.user_photo_list_fragment, null);
		// layout ui controls
		mGridView = (GridView) v.findViewById(R.id.grid_user_photos);
		mUserInfoText = (TextView) v.findViewById(R.id.txt_user_info);

		mImageThumbSize = getResources().getDimensionPixelSize(
				R.dimen.image_thumbnail_size);
		mImageThumbSpacing = getResources().getDimensionPixelSize(
				R.dimen.image_thumbnail_spacing);
		this.setRetainInstance(true);

		mGridView.setAdapter(mAdapter);
		// mGridView.setOnItemClickListener(this);
		mGridView.setOnScrollListener(new OneTimeScrollListener() {

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
			public void loadMoreData() {
				UserPhotoListFragment.this.loadMoreData();
			}
		});

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

		hookUserInfoIntoUI();
		return v;
	}

	protected void loadMoreData() {
		String s = String.format(mMessage2, mCurrentUser.getUserName());
		this.mUserInfoText.setText(s);
		mUserInfoText.setVisibility(View.VISIBLE);
		mCurrentCommand.execute();
	}

	private void hookUserInfoIntoUI() {
		if (mCurrentUser != null && mUserInfoText != null) {
			String s = String.format(mMessage1, mCurrentUser.getUserName());
			mUserInfoText.setText(s);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Intent intent = getActivity().getIntent();
		mMedisSourceType = intent.getIntExtra(
				UserPhotoListActivity.MD_TYPE_KEY, 0);
		mCurrentUser = (Author) intent
				.getSerializableExtra(UserPhotoListActivity.USER_KEY);
		initializeImageFetcher(IConstants.IMAGE_THUMBS_CACHE_DIR,
				mImageThumbSize);
		mAdapter = new PhotoGridAdapter(getActivity(), mPhotosProvider,
				mImageFetcher);
		
		mMessage1 = getString(R.string.msg_photos_of_user);
		mMessage2 = getString(R.string.msg_loading_more_photo_of_user);
		loadUserPhotos();

	}

	private void loadUserPhotos() {
		ICommandDoneListener<MediaObjectCollection> lis = new ICommandDoneListener<MediaObjectCollection>() {
			@Override
			public void onCommandDone(ICommand<MediaObjectCollection> command,
					MediaObjectCollection t) {
				mPhotosProvider.loadData(t, command);
				mAdapter.notifyDataSetChanged();
				String s = String.format(mMessage1, mCurrentUser.getUserName());
				if (mUserInfoText != null) {
					mUserInfoText.setText(s);
					mUserInfoText.setVisibility(View.GONE);
				}
			}
		};
		if (mMedisSourceType == MediaSourceType.FLICKR.ordinal()) {

		} else if (mMedisSourceType == MediaSourceType.INSTAGRAM.ordinal()) {
			mCurrentCommand = new InstagramUserPhotosCommand(getActivity(),
					mCurrentUser);
			mCurrentCommand.addCommndDoneListener(lis);
			mCurrentCommand.execute();
		} else {

		}
	}

}
