/**
 * 
 */
package com.gmail.charleszq.picorner.ui.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.model.Author;
import com.gmail.charleszq.picorner.ui.command.ICommand;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public abstract class AbstractContactsView extends
		AbstractHiddenView implements OnItemClickListener {

	protected ListView			mListView;
	protected Button			mCancelButton;
	protected FriendListAdapter	mAdapter;
	protected View				mView;

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

		mListView = (ListView) mView.findViewById(R.id.list_f_friends);
		mAdapter = new FriendListAdapter(ctx, command);
		mListView.setEmptyView(emptyView);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);

		mCancelButton = (Button) mView.findViewById(R.id.btn_cancel_friends);
		mCancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onAction(ACTION_CANCEL);
				mCancelButton.setVisibility(View.INVISIBLE);
			}
		});

		getContactList(ctx);
	}

	@Override
	public void onItemClick(AdapterView<?> parentView, View view, int position,
			long id) {
		Author friend = (Author) mAdapter.getItem(position);
		onAction(ACTION_JUST_CMD, friend);
	}

	@Override
	public View getView(Context ctx) {
		if (mView == null) {
			mView = LayoutInflater.from(ctx).inflate(R.layout.contacts_list,
					null);
		}
		return mView;
	}

	protected abstract void getContactList(Context ctx);
}
