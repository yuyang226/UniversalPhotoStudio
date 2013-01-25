/**
 * 
 */
package com.gmail.charleszq.picorner.ui.flickr;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.model.Author;
import com.gmail.charleszq.picorner.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.picorner.task.flickr.FlickrFriendsTask;
import com.gmail.charleszq.picorner.ui.command.ICommand;
import com.gmail.charleszq.picorner.ui.helper.AbstractLinearLayoutHiddenView;
import com.gmail.charleszq.picorner.ui.helper.FriendListAdapter;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class FlickrContactsView extends AbstractLinearLayoutHiddenView implements OnItemClickListener {

	private ListView			mListView;
	private Button				mCancelButton;
	private FriendListAdapter	mAdapter;

	/**
	 * @param context
	 */
	public FlickrContactsView(Context context) {
		super(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public FlickrContactsView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public FlickrContactsView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

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
		View emptyView = findViewById(R.id.empty_friend_view);
		
		mListView = (ListView) findViewById(R.id.list_f_friends);
		mAdapter = new FriendListAdapter(getContext(),command);
		mListView.setEmptyView(emptyView);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		
		mCancelButton = (Button) findViewById(R.id.btn_cancel_friends);
		mCancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onAction(ACTION_CANCEL);
			}
		});

		FlickrFriendsTask task = new FlickrFriendsTask(getContext());
		task.addTaskDoneListener(new IGeneralTaskDoneListener<List<Author>>() {

			@Override
			public void onTaskDone(List<Author> result) {
				if( result != null ) {
					mAdapter.populateFriends(result);
					mCancelButton.setVisibility(View.VISIBLE);
				}
			}
		});
		task.execute();
	}

	@Override
	public void onItemClick(AdapterView<?> parentView, View view, int position, long id) {
		Author friend = (Author) mAdapter.getItem(position);
		onAction(ACTION_JUST_CMD, friend);
	}
}
