/**
 * 
 */
package com.gmail.charleszq.picorner.ui.flickr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.picorner.task.flickr.SearchGroupTask;
import com.gmail.charleszq.picorner.ui.command.ICommand;
import com.gmail.charleszq.picorner.ui.helper.AbstractHiddenListView;
import com.gmail.charleszq.picorner.ui.helper.PhotoCollectionItemAdapter;
import com.googlecode.flickrjandroid.groups.Group;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class GroupSearchHiddenListView extends AbstractHiddenListView {

	/*
	 * The search task.
	 */
	private SearchGroupTask mSearchTask;
	
	/**
	 * The current query string to search groups.
	 */
	private String mQueryString;
	
	private int mCurrentPage = 1;
	private int mActivePage = 1;

	private IGeneralTaskDoneListener<Collection<Group>> mSearchTaskDoneListener = new IGeneralTaskDoneListener<Collection<Group>>() {

		@Override
		public void onTaskDone(Collection<Group> result) {
			mPullToRefreshListView.onRefreshComplete();
			ImageLoader.getInstance().resume();
			if (result == null || result.isEmpty()) {
				if (mActivePage == 1) {
					mPullToRefreshListView.setVisibility(View.INVISIBLE);
					Context ctx = (Context) mCommand.getAdapter(Context.class);
					Toast.makeText(ctx,
							ctx.getString(R.string.msg_no_groups_found),
							Toast.LENGTH_SHORT).show();
				}
				return;
			}
			mCurrentPage = mActivePage;
			mAdapter.populateData(result);
			mSpace.setVisibility(View.GONE);
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gmail.charleszq.picorner.ui.helper.AbstractHiddenListView#getData
	 * (android.content.Context)
	 */
	@Override
	protected void getData(Context ctx) {
		// do nothing here, we need user input the search text first.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gmail.charleszq.picorner.ui.helper.AbstractHiddenListView#onCancel()
	 */
	@Override
	protected void onCancel() {
		ImageLoader.getInstance().resume();
		if (mSearchTask != null) {
			mSearchTask.cancel(true);
		}
	}

	@Override
	protected void initSearchView(final Context ctx) {
		mSearchView = (SearchView) mView.findViewById(R.id.contact_filter);
		mSearchView.setQueryHint(ctx
				.getString(R.string.menu_item_flickr_group_search));
		mSearchView.requestFocus();
		mSearchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				doSearch(ctx, query);
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});

		mPullToRefreshListView.setVisibility(View.INVISIBLE);
	}

	private void doSearch(Context ctx, String query) {
		mCurrentPage = 1;
		mActivePage = 1;
		mQueryString = query;
		List<Group> groups = new ArrayList<Group>();
		mAdapter.populateData(groups);
		
		// hide the soft keyboard
		InputMethodManager imm = (InputMethodManager) ctx
				.getSystemService(Service.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);

		mPullToRefreshListView.setVisibility(View.VISIBLE);
		mPullToRefreshListView.setMode(Mode.BOTH);
		mSearchTask = new SearchGroupTask(query);
		mSearchTask.addTaskDoneListener(mSearchTaskDoneListener);
		mSearchTask.execute(mActivePage);
	}

	@Override
	protected void initializeListViewAdapter(final Context ctx, ICommand<?> command) {
		this.mAdapter = new PhotoCollectionItemAdapter(ctx, command);
		mLoadingMessage = ctx.getString(R.string.msg_searching_flickr_group);
		
		mActivePage = 1;
		mCurrentPage = 1;
		
		mPullToRefreshListView.setMode(Mode.BOTH);
		mPullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						if (mCurrentPage > 1) {
							mActivePage = mCurrentPage - 1;
							runTask(ctx, mActivePage);
						} else
							mPullToRefreshListView.onRefreshComplete();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						mActivePage = mCurrentPage + 1;
						runTask(ctx, mActivePage);
					}
				});
	}

	private void runTask(Context ctx, int page) {
		ImageLoader.getInstance().pause();
		mSearchTask = new SearchGroupTask(mQueryString);
		mSearchTask.addTaskDoneListener(mSearchTaskDoneListener);
		mSearchTask.execute(page);
	}

}
