/**
 * 
 */
package com.gmail.charleszq.picorner.ui.helper;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Space;
import android.widget.TextView;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.ui.command.ICommand;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public abstract class AbstractHiddenListView extends AbstractHiddenView
		implements OnItemClickListener {

	protected ListView mListView;
	protected PullToRefreshListView mPullToRefreshListView;
	protected Button mCancelButton;
	protected FilterAdapter mAdapter;
	protected View mView;
	protected SearchView mSearchView;
	protected Space mSpace;
	protected TextView mLoadingText;

	protected String mLoadingMessage;

	private SearchView.OnQueryTextListener mQueryTextListener = new SearchView.OnQueryTextListener() {

		@Override
		public boolean onQueryTextSubmit(String query) {
			if (query == null || query.trim().length() == 0)
				return false;
			CommonListTitleFilter filter = new CommonListTitleFilter(mAdapter);
			filter.filter(query);
			return true;
		}

		@Override
		public boolean onQueryTextChange(String newText) {
			if (newText == null || newText.trim().length() == 0) {
				mAdapter.mFilteredData.clear();
				mAdapter.mFilteredData.addAll(mAdapter.mData);
				mAdapter.notifyDataSetChanged();
				return true;
			} else
				return false;
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gmail.charleszq.picorner.ui.helper.IHiddenView#init(com.gmail.charleszq
	 * .picorner.ui.command.ICommand,
	 * com.gmail.charleszq.picorner.ui.helper.IHiddenView
	 * .IHiddenViewActionListener)
	 */
	@Override
	public void init(ICommand<?> command, IHiddenViewActionListener listener) {
		super.init(command, listener);
		Context ctx = (Context) command.getAdapter(Context.class);

		mView = getView(ctx);
		View emptyView = mView.findViewById(R.id.empty_friend_view);
		mLoadingText = (TextView) emptyView.findViewById(R.id.txt_loading_msg);

		mSpace = (Space) mView.findViewById(R.id.contact_list_space);
		mSpace.setVisibility(View.VISIBLE);

		mPullToRefreshListView = (PullToRefreshListView) mView
				.findViewById(R.id.list_f_friends);
		mPullToRefreshListView.setMode(Mode.DISABLED);
		mListView = mPullToRefreshListView.getRefreshableView();
		initializeListViewAdapter(ctx, command);
		if (mLoadingMessage != null)
			mLoadingText.setText(mLoadingMessage);
		
		mListView.setEmptyView(emptyView);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		PauseOnScrollListener pauseListener = new PauseOnScrollListener(false,
				true);
		mListView.setOnScrollListener(pauseListener);

		mCancelButton = (Button) mView.findViewById(R.id.btn_cancel_friends);
		mCancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onCancel();
				onAction(ACTION_CANCEL);
			}
		});

		// filter
		mSearchView = (SearchView) mView.findViewById(R.id.contact_filter);
		mSearchView.setOnQueryTextListener(mQueryTextListener);
		// hide the soft keyboard
		InputMethodManager imm = (InputMethodManager) ctx
				.getSystemService(Service.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);

		getData(ctx);
	}

	/**
	 * Sets the adapter for this list view.
	 * 
	 * @param ctx
	 * @param command
	 */
	protected void initializeListViewAdapter(Context ctx, ICommand<?> command) {
		mAdapter = new FriendListAdapter(ctx, command);
	}

	@Override
	public void onItemClick(AdapterView<?> parentView, View view, int position,
			long id) {
		Object item = mAdapter.getItem((int)id);
		onAction(ACTION_JUST_CMD, item);
	}

	@Override
	public View getView(Context ctx) {
		if (mView == null) {
			mView = LayoutInflater.from(ctx).inflate(R.layout.contacts_list,
					null);
		}
		return mView;
	}

	/**
	 * Gets the data from server side, then populate them into the list.
	 * 
	 * @param ctx
	 */
	protected abstract void getData(Context ctx);

	protected abstract void onCancel();
}
