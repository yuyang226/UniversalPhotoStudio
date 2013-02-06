/**
 * 
 */
package com.gmail.charleszq.picorner.ui.flickr;

import java.util.Collection;

import android.content.Context;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.picorner.task.flickr.FetchMyGroupsTask;
import com.gmail.charleszq.picorner.ui.command.ICommand;
import com.gmail.charleszq.picorner.ui.helper.AbstractHiddenListView;
import com.gmail.charleszq.picorner.ui.helper.IHiddenView;
import com.gmail.charleszq.picorner.ui.helper.PhotoCollectionItemAdapter;
import com.googlecode.flickrjandroid.groups.Group;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class MyPhotoGroupsHiddenView extends AbstractHiddenListView {

	private FetchMyGroupsTask mTask;
	private int mCurrentPage = 1;
	private int mActivePage = 1;

	private IGeneralTaskDoneListener<Collection<Group>> mTaskListener = new IGeneralTaskDoneListener<Collection<Group>>() {

		@Override
		public void onTaskDone(Collection<Group> result) {
			mPullToRefreshListView.onRefreshComplete();
			if (result != null && !result.isEmpty()) {
				mCurrentPage = mActivePage;
				mAdapter.populateData(result);
				mSpace.setVisibility(View.GONE);
			} else {
				if (mActivePage == 1) {
					Context ctx = (Context) mCommand.getAdapter(Context.class);
					Toast.makeText(ctx,
							ctx.getString(R.string.msg_no_photo_groups),
							Toast.LENGTH_SHORT).show();
					onAction(IHiddenView.ACTION_CANCEL);
				}
			}
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
	protected void getData(final Context ctx) {
		runTask(ctx,mCurrentPage);
	}
	
	private void runTask(Context c, int page) {
		mTask = new FetchMyGroupsTask(c);
		mTask.addTaskDoneListener(mTaskListener);
		mTask.execute(page);
	}

	@Override
	protected void initializeListViewAdapter(final Context ctx, ICommand<?> command) {
		mAdapter = new PhotoCollectionItemAdapter(ctx, command);
		mLoadingMessage = ctx.getString(R.string.msg_loading_my_groups);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gmail.charleszq.picorner.ui.helper.AbstractHiddenListView#onCancel()
	 */
	@Override
	protected void onCancel() {
		if (mTask != null)
			mTask.cancel(true);
	}

}
