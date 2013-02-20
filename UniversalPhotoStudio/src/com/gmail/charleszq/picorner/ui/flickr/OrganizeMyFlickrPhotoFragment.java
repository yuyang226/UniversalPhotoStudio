/**
 * 
 */
package com.gmail.charleszq.picorner.ui.flickr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.model.FlickrUserPhotoPool;
import com.gmail.charleszq.picorner.model.MediaObject;
import com.gmail.charleszq.picorner.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.picorner.task.flickr.FetchFlickrPhotoContextTask;
import com.gmail.charleszq.picorner.task.flickr.FetchPhotoSetsTask;
import com.gmail.charleszq.picorner.task.flickr.FlickrOrganizePhotoTask;
import com.gmail.charleszq.picorner.ui.AbstractFragmentWithImageFetcher;
import com.gmail.charleszq.picorner.ui.command.ICommand;
import com.gmail.charleszq.picorner.ui.command.MenuSectionHeaderCommand;
import com.gmail.charleszq.picorner.ui.command.flickr.FlickrUserPhotoSetCommand;
import com.gmail.charleszq.picorner.ui.helper.FlickrOrganizeAdapter;
import com.gmail.charleszq.picorner.utils.IConstants;
import com.googlecode.flickrjandroid.photos.PhotoPlace;
import com.googlecode.flickrjandroid.photosets.Photoset;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

/**
 * Represents the fragment to organize my photos in differenct photo sets.
 * 
 * @author charles(charleszq@gmail.com)
 * 
 */
public class OrganizeMyFlickrPhotoFragment extends
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
	private FetchPhotoSetsTask mFetchMyPhotoSetsTask;

	private OnRefreshListener2<ListView> mOnRefreshListener = new OnRefreshListener2<ListView>() {

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			if (mCurrentPhotoSetPageNo > 1) {
				mExecutionPageNo = mCurrentPhotoSetPageNo - 1;
				fetchMyPhotoSets(mExecutionPageNo);
			} else {
				mPullToRefreshListView.onRefreshComplete();
			}
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			mExecutionPageNo = mCurrentPhotoSetPageNo + 1;
			fetchMyPhotoSets(mExecutionPageNo);
		}
	};

	/**
	 * default constructor.
	 */
	public OrganizeMyFlickrPhotoFragment() {
	}

	public static OrganizeMyFlickrPhotoFragment newInstance(MediaObject photo) {
		OrganizeMyFlickrPhotoFragment f = new OrganizeMyFlickrPhotoFragment();
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
		// inflater.inflate(R.menu.crt_photo_set, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_item_save:
			performOk();
			return true;
		case R.id.menu_item_crt_photo_set:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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
		fetchMyPhotoSets(mCurrentPhotoSetPageNo);
	}

	private void fetchMyPhotoSets(int page) {
		Context ctx = getActivity();
		if( ctx ==  null )
			return;
		
		// start another task to fetch all my photo sets and groups
		mFetchMyPhotoSetsTask = new FetchPhotoSetsTask(ctx);
		mFetchMyPhotoSetsTask
				.addTaskDoneListener(new IGeneralTaskDoneListener<List<Photoset>>() {

					@Override
					public void onTaskDone(List<Photoset> result) {
						onPoolsFetched(result);
					}
				});
		mFetchMyPhotoSetsTask.execute(page);
	}

	private void onPoolsFetched(List<Photoset> result) {

		mPullToRefreshListView.onRefreshComplete();
		if (getActivity() == null) {
			return;
		}

		if (result.isEmpty() && mExecutionPageNo > 1)
			return;

		ICommand<?> cmd = null;
		if (mCommands == null)
			mCommands = new ArrayList<ICommand<?>>();
		mCommands.clear();
		if (result.isEmpty()) {
			cmd = new MenuSectionHeaderCommand(getActivity(),
					getString(R.string.msg_no_photo_sets));
			mCommands.add(cmd);
		} else {
			mCurrentPhotoSetPageNo = mExecutionPageNo;
			for (Photoset obj : result) {
				cmd = new FlickrUserPhotoSetCommand(getActivity(), obj);
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
