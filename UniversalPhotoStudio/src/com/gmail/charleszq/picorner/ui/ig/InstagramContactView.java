/**
 * 
 */
package com.gmail.charleszq.picorner.ui.ig;

import java.util.List;

import android.content.Context;
import android.view.View;

import com.gmail.charleszq.picorner.model.Author;
import com.gmail.charleszq.picorner.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.picorner.task.ig.InstagramGetFollowingListTask;
import com.gmail.charleszq.picorner.ui.helper.AbstractHiddenListView;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class InstagramContactView extends AbstractHiddenListView {
	
	private InstagramGetFollowingListTask task;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gmail.charleszq.picorner.ui.AbstractContactsView#getContactList()
	 */
	@Override
	protected void getData(Context ctx) {
		 task = new InstagramGetFollowingListTask(
				ctx);
		task.addTaskDoneListener(new IGeneralTaskDoneListener<List<Author>>() {

			@Override
			public void onTaskDone(List<Author> result) {
				if (result != null) {
					mAdapter.populateData(result);
					mSpace.setVisibility(View.GONE);
				}
			}
		});
		task.execute();
	}

	@Override
	protected void onCancel() {
		if( task != null ) 
			task.cancel(true);
	}
}
