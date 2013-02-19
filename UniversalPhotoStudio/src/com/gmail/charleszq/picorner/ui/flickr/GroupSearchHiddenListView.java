/**
 * 
 */
package com.gmail.charleszq.picorner.ui.flickr;

import android.content.Context;
import android.view.View;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.ui.command.ICommand;
import com.gmail.charleszq.picorner.ui.helper.AbstractHiddenListView;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class GroupSearchHiddenListView extends AbstractHiddenListView {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gmail.charleszq.picorner.ui.helper.AbstractHiddenListView#getData
	 * (android.content.Context)
	 */
	@Override
	protected void getData(Context ctx) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gmail.charleszq.picorner.ui.helper.AbstractHiddenListView#onCancel()
	 */
	@Override
	protected void onCancel() {

	}

	@Override
	public void init(ICommand<?> command, IHiddenViewActionListener listener) {
		super.init(command, listener);
	}

	@Override
	protected void initSearchView(Context ctx) {
		mSearchView = (SearchView) mView.findViewById(R.id.contact_filter);
		mSearchView.setQueryHint(ctx.getString(R.string.menu_item_flickr_group_search));
		mSearchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
		
		mPullToRefreshListView.setVisibility(View.INVISIBLE);
	}

	@Override
	protected void initializeListViewAdapter(Context ctx, ICommand<?> command) {
		super.initializeListViewAdapter(ctx, command);
		mLoadingMessage = ctx.getString(R.string.msg_searching_flickr_group);
	}

}
