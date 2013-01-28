/**
 * 
 */
package com.gmail.charleszq.picorner.ui.flickr;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.gmail.charleszq.picorner.model.Author;
import com.gmail.charleszq.picorner.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.picorner.task.flickr.FlickrFriendsTask;
import com.gmail.charleszq.picorner.ui.AbstractContactsView;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class FlickrContactsView extends AbstractContactsView {

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

	@Override
	protected void getContactList() {
		FlickrFriendsTask task = new FlickrFriendsTask(getContext());
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
