/**
 * 
 */
package com.gmail.charleszq.picorner.ui.flickr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.model.FlickrUserPhotoPool;
import com.gmail.charleszq.picorner.model.MediaObject;
import com.gmail.charleszq.picorner.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.picorner.task.flickr.FetchFlickrPhotoContextTask;
import com.gmail.charleszq.picorner.task.flickr.FetchMyGroupsTask;
import com.gmail.charleszq.picorner.task.flickr.FlickrOrganizePhotoTask;
import com.gmail.charleszq.picorner.ui.AbstractFragmentWithImageFetcher;
import com.gmail.charleszq.picorner.ui.command.ICommand;
import com.gmail.charleszq.picorner.ui.command.MenuSectionHeaderCommand;
import com.gmail.charleszq.picorner.ui.command.flickr.FlickrUserGroupCommand;
import com.gmail.charleszq.picorner.ui.helper.FlickrOrganizeAdapter;
import com.gmail.charleszq.picorner.utils.IConstants;
import com.googlecode.flickrjandroid.groups.Group;
import com.googlecode.flickrjandroid.photos.PhotoPlace;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

/**
 * Represents the fragment to manage photo groups of one of my photos.
 * 
 * @author charles(charleszq@gmail.com)
 * 
 */
public class ManagePhotoGroupFragment extends
		AbstractFragmentWithImageFetcher implements OnItemClickListener {

	private ListView mListView;
	private PullToRefreshListView mPullToRefreshListView;
	private View mEmptyView;
	private FlickrOrganizeAdapter mAdapter;
	private MediaObject mCurrentPhoto;
	private Set<String> mCurrentPhotoContext;
	private Set<String> mUpdatePhotoContext;

	private Collection<ICommand<?>> mCommands;
	private int mCurrentPhotoSetPageNo = 1;
	private int mExecutionPageNo = 1;
	private FetchMyGroupsTask mFetchMyPhotoSetsTask;

	private OnRefreshListener2<ListView> mOnRefreshListener = new OnRefreshListener2<ListView>() {

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			if (mCurrentPhotoSetPageNo > 1) {
				mExecutionPageNo = mCurrentPhotoSetPageNo - 1;
				fetchMyPhotoGroups(mExecutionPageNo);
			} else {
				mPullToRefreshListView.onRefreshComplete();
			}
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			mExecutionPageNo = mCurrentPhotoSetPageNo + 1;
			fetchMyPhotoGroups(mExecutionPageNo);
		}
	};

	/**
	 * default constructor.
	 */
	public ManagePhotoGroupFragment() {
	}

	public static ManagePhotoGroupFragment newInstance(MediaObject photo) {
		ManagePhotoGroupFragment f = new ManagePhotoGroupFragment();
		final Bundle bundle = new Bundle();
		bundle.putSerializable(IConstants.DETAIL_PAGE_PHOTO_ARG_KEY, photo);
		f.setArguments(bundle);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater
				.inflate(R.layout.frg_org_my_f_photo, container, false);

		// the list view.
		mPullToRefreshListView = (PullToRefreshListView) v
				.findViewById(R.id.list_org_flickr_photo);
		mPullToRefreshListView.setOnRefreshListener(mOnRefreshListener);
		mListView = mPullToRefreshListView.getRefreshableView();
		mEmptyView = v.findViewById(R.id.empty_photo_set_view);
		TextView loadingMessageView = (TextView) v.findViewById(R.id.txt_loading_msg);
		loadingMessageView.setText(R.string.msg_loading_my_groups);
		mListView.setEmptyView(mEmptyView);

		mAdapter = new FlickrOrganizeAdapter(getActivity());
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		PauseOnScrollListener pauseListener = new PauseOnScrollListener(false,
				true);
		mListView.setOnScrollListener(pauseListener);

		// the progress bar
		return v;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = this.getArguments();
		mCurrentPhoto = (MediaObject) bundle
				.getSerializable(IConstants.DETAIL_PAGE_PHOTO_ARG_KEY);
		this.setHasOptionsMenu(true);
		this.setRetainInstance(true);
	}

	@Override
	public void onStart() {
		super.onStart();
		if (this.mCurrentPhotoContext == null) {
			FetchFlickrPhotoContextTask t = new FetchFlickrPhotoContextTask();
			t.addTaskDoneListener(new IGeneralTaskDoneListener<List<PhotoPlace>>() {

				@Override
				public void onTaskDone(List<PhotoPlace> result) {
					onPhotoContextFetched(result);

				}
			});
			t.execute(mCurrentPhoto.getId());
		} else if (mCommands != null) {
			mAdapter.clearSections();
			mAdapter.setCurrentPhotoContext(mUpdatePhotoContext);
			mAdapter.addCommands(mCommands);
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_save, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_item_save) {
			performOk();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void performOk() {
		Set<String> add = new HashSet<String>();
		Set<String> remove = new HashSet<String>();

		for (String s : mUpdatePhotoContext) {
			if (!mCurrentPhotoContext.contains(s)) {
				add.add(s);
			}
		}

		for (String ss : mCurrentPhotoContext) {
			if (!mUpdatePhotoContext.contains(ss)) {
				remove.add(ss);
			}
		}
		FlickrOrganizePhotoTask task = new FlickrOrganizePhotoTask(
				getActivity(), add, remove);
		task.execute(mCurrentPhoto.getId());
	}

	protected void onPhotoContextFetched(List<PhotoPlace> result) {
		Log.d(TAG,
				"photo context fetched, size: " + (result == null ? 0 : result.size())); //$NON-NLS-1$

		mCurrentPhotoContext = new HashSet<String>();
		mUpdatePhotoContext = new HashSet<String>();
		if (result != null)
			for (PhotoPlace place : result) {
				mCurrentPhotoContext.add(place.getKind() + place.getId());
				mUpdatePhotoContext.add(place.getKind() + place.getId());
			}
		mAdapter.setCurrentPhotoContext(mUpdatePhotoContext);
		mAdapter.notifyDataSetChanged();

		// fetch my photo sets.
		fetchMyPhotoGroups(mCurrentPhotoSetPageNo);
	}

	private void fetchMyPhotoGroups(int page) {
		// start another task to fetch all my photo sets and groups
		mFetchMyPhotoSetsTask = new FetchMyGroupsTask(getActivity());
		mFetchMyPhotoSetsTask
				.addTaskDoneListener(new IGeneralTaskDoneListener<Collection<Group>>() {

					@Override
					public void onTaskDone(Collection<Group> result) {
						onPoolsFetched(result);
					}
				});
		mFetchMyPhotoSetsTask.execute(page);
	}

	private void onPoolsFetched(Collection<Group> result) {

		mPullToRefreshListView.onRefreshComplete();
		if (getActivity() == null  ) {
			return;
		}
		if( (result == null || result.isEmpty()) && mExecutionPageNo > 1) 
			return;

		ICommand<?> cmd = null;
		if (mCommands == null)
			mCommands = new ArrayList<ICommand<?>>();
		mCommands.clear();
		if ( result == null || result.isEmpty() ) {
			cmd = new MenuSectionHeaderCommand(getActivity(),
					getString(R.string.msg_no_photo_groups));
			mCommands.add(cmd);
		} else {
			mCurrentPhotoSetPageNo = mExecutionPageNo;
			for (Group obj : result) {
				cmd = new FlickrUserGroupCommand(getActivity(), obj);
				mCommands.add(cmd);
			}
		}

		mAdapter.clearSections();
		mAdapter.addCommands(mCommands);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ICommand<?> cmd = (ICommand<?>) mAdapter.getItem((int) id);
		if (cmd == null) {
			return;
		}

		String poolid = cmd.getAdapter(FlickrUserPhotoPool.class).toString();
		if (poolid == null) {
			return;
		}

		if (mUpdatePhotoContext.contains(poolid)) {
			mUpdatePhotoContext.remove(poolid);
		} else {
			mUpdatePhotoContext.add(poolid);
		}
		mAdapter.notifyDataSetChanged();

	}
}
