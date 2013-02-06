/**
 * 
 */
package com.gmail.charleszq.picorner.ui.flickr;

import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.gmail.charleszq.picorner.BuildConfig;
import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.task.AbstractContextAwareTask;
import com.gmail.charleszq.picorner.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.picorner.task.flickr.FetchMyOfflinePhotoSetsTask;
import com.gmail.charleszq.picorner.task.flickr.FetchPhotoSetsTask;
import com.gmail.charleszq.picorner.ui.command.ICommand;
import com.gmail.charleszq.picorner.ui.helper.AbstractHiddenListView;
import com.gmail.charleszq.picorner.ui.helper.IHiddenView;
import com.gmail.charleszq.picorner.ui.helper.PhotoSetItemAdapter;
import com.googlecode.flickrjandroid.photosets.Photoset;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class MyPhotoSetsHiddenView extends AbstractHiddenListView {

	private AbstractContextAwareTask<Integer, Integer, List<Photoset>> mTask;
	private int mCurrentPage = 1;
	private int mActivePage = 1;

	private IGeneralTaskDoneListener<List<Photoset>> mTaskDoneListener = new IGeneralTaskDoneListener<List<Photoset>>() {

		@Override
		public void onTaskDone(List<Photoset> result) {
			mPullToRefreshListView.onRefreshComplete();
			if (!result.isEmpty()) {
				mCurrentPage = mActivePage;
				mAdapter.populateData(result);
				mSpace.setVisibility(View.GONE);
			} else {
				if (mActivePage == 1) {
					Context ctx = (Context) mCommand.getAdapter(Context.class);
					Toast.makeText(ctx,
							ctx.getString(R.string.msg_no_photo_sets),
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
	 * com.gmail.charleszq.picorner.ui.helper.AbstractHiddenListView#getContactList
	 * (android.content.Context)
	 */
	@Override
	protected void getData(final Context ctx) {
		runTask(ctx, mCurrentPage);
	}

	private void runTask(Context c, int page) {

		ConnectivityManager cm = (ConnectivityManager) c
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (activeNetwork == null) {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "not network available."); //$NON-NLS-1$
			}
			mTask = new FetchMyOfflinePhotoSetsTask(c);
		} else {
			mTask = new FetchPhotoSetsTask(c);
		}
		mTask.addTaskDoneListener(mTaskDoneListener);
		mTask.execute(page);
	}

	@Override
	protected void initializeListViewAdapter(final Context ctx,
			ICommand<?> command) {
		mCurrentPage = 1;
		mActivePage = 1;
		mAdapter = new PhotoSetItemAdapter(ctx, command);
		mLoadingMessage = ctx.getString(R.string.msg_loading_photo_sets);
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
