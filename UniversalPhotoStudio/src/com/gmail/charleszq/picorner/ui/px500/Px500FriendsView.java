/**
 * 
 */
package com.gmail.charleszq.picorner.ui.px500;

import java.util.List;

import android.content.Context;
import android.view.View;

import com.gmail.charleszq.picorner.model.Author;
import com.gmail.charleszq.picorner.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.picorner.task.px500.Px500FetchFriendsTask;
import com.gmail.charleszq.picorner.ui.helper.AbstractContactsView;

/**
 * @author charles(charleszq@gmail.com)
 *
 */
public class Px500FriendsView extends AbstractContactsView {

	/* (non-Javadoc)
	 * @see com.gmail.charleszq.picorner.ui.AbstractContactsView#getContactList(android.content.Context)
	 */
	@Override
	protected void getContactList(Context ctx) {
		Px500FetchFriendsTask task = new Px500FetchFriendsTask(ctx);
		task.addTaskDoneListener(new IGeneralTaskDoneListener<List<Author>>() {

			@Override
			public void onTaskDone(List<Author> result) {
				if (result != null) {
					mAdapter.populateFriends(result);
					mSpace.setVisibility(View.GONE);
				}
			}
		});
		task.execute();
	}

}
