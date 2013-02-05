/**
 * 
 */
package com.gmail.charleszq.picorner.ui.flickr;

import java.util.Collection;

import android.content.Context;
import android.view.View;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.picorner.task.flickr.FetchMyGroupsTask;
import com.gmail.charleszq.picorner.ui.command.ICommand;
import com.gmail.charleszq.picorner.ui.helper.AbstractHiddenListView;
import com.gmail.charleszq.picorner.ui.helper.PhotoCollectionItemAdapter;
import com.googlecode.flickrjandroid.groups.Group;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class MyPhotoGroupsHiddenView extends AbstractHiddenListView {

	private FetchMyGroupsTask mTask;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gmail.charleszq.picorner.ui.helper.AbstractHiddenListView#getData
	 * (android.content.Context)
	 */
	@Override
	protected void getData(Context ctx) {
		mTask = new FetchMyGroupsTask(ctx);
		mTask.addTaskDoneListener(new IGeneralTaskDoneListener<Collection<Group>>() {

			@Override
			public void onTaskDone(Collection<Group> result) {
				if (result != null) {
					mAdapter.populateData(result);
					mSpace.setVisibility(View.GONE);
				}
				// TODO what if user does not have group?
			}
		});
		mTask.execute();

	}
	
	@Override
	protected void initializeListViewAdapter(Context ctx, ICommand<?> command) {
		mAdapter = new PhotoCollectionItemAdapter(ctx, command);
		mLoadingMessage = ctx.getString(R.string.msg_loading_my_groups);
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
