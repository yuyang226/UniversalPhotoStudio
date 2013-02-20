/**
 * 
 */
package com.gmail.charleszq.picorner.ui.flickr;

import java.util.Collection;

import android.app.Service;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class GroupSearchHiddenListView extends AbstractHiddenListView {

	/*
	 * The search task.
	 */
	private SearchGroupTask mSearchTask;
	private int mCurrentPage = 1;
	private int mActivePage = 1;

	private IGeneralTaskDoneListener<Collection<Group>> mSearchTaskDoneListener = new IGeneralTaskDoneListener<Collection<Group>>() {

		@Override
		public void onTaskDone(Collection<Group> result) {
			if (result == null || result.isEmpty()) {
				if (mCurrentPage == 1) {
					mPullToRefreshListView.setVisibility(View.INVISIBLE);
					Context ctx = (Context) mCommand.getAdapter(Context.class);
					Toast.makeText(ctx,
							ctx.getString(R.string.msg_no_groups_found),
							Toast.LENGTH_SHORT).show();
					return;
				}
			}
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
		// hide the soft keyboard
		InputMethodManager imm = (InputMethodManager) ctx
				.getSystemService(Service.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);

		mPullToRefreshListView.setVisibility(View.VISIBLE);
		mSearchTask = new SearchGroupTask(query);
		mSearchTask.addTaskDoneListener(mSearchTaskDoneListener);
		mSearchTask.execute(1);
	}

	@Override
	protected void initializeListViewAdapter(Context ctx, ICommand<?> command) {
		this.mAdapter = new PhotoCollectionItemAdapter(ctx, command);
		mLoadingMessage = ctx.getString(R.string.msg_searching_flickr_group);
	}

}
