/**
 * 
 */
package com.gmail.charleszq.picorner.ui.ig;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.gmail.charleszq.picorner.model.Author;
import com.gmail.charleszq.picorner.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.picorner.task.ig.InstagramGetFollowingListTask;
import com.gmail.charleszq.picorner.ui.AbstractContactsView;

/**
 * @author charles(charleszq@gmail.com)
 *
 */
public class InstagramContactView extends AbstractContactsView {

	/**
	 * @param context
	 */
	public InstagramContactView(Context context) {
		super(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public InstagramContactView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public InstagramContactView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	/* (non-Javadoc)
	 * @see com.gmail.charleszq.picorner.ui.AbstractContactsView#getContactList()
	 */
	@Override
	protected void getContactList() {
		InstagramGetFollowingListTask task = new InstagramGetFollowingListTask(getContext());
		task.addTaskDoneListener(new IGeneralTaskDoneListener<List<Author>>() {

			@Override
			public void onTaskDone(List<Author> result) {
				if (result != null) {
					mAdapter.populateFriends(result);
					mCancelButton.setVisibility(View.VISIBLE);
				}
			}
		});
		task.execute();
	}
}
