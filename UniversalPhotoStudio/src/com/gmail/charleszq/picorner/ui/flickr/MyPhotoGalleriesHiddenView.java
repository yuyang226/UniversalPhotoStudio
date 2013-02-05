/**
 * 
 */
package com.gmail.charleszq.picorner.ui.flickr;

import java.util.Collection;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.picorner.task.flickr.FetchMyGalleriesTask;
import com.gmail.charleszq.picorner.ui.command.ICommand;
import com.gmail.charleszq.picorner.ui.helper.AbstractHiddenListView;
import com.gmail.charleszq.picorner.ui.helper.IHiddenView;
import com.gmail.charleszq.picorner.ui.helper.PhotoCollectionItemAdapter;
import com.googlecode.flickrjandroid.galleries.Gallery;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class MyPhotoGalleriesHiddenView extends AbstractHiddenListView {

	private FetchMyGalleriesTask mTask;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gmail.charleszq.picorner.ui.helper.AbstractHiddenListView#getData
	 * (android.content.Context)
	 */
	@Override
	protected void getData(final Context ctx) {
		mTask = new FetchMyGalleriesTask(ctx);
		mTask.addTaskDoneListener(new IGeneralTaskDoneListener<Collection<Gallery>>() {

			@Override
			public void onTaskDone(Collection<Gallery> result) {
				if (result != null) {
					mAdapter.populateData(result);
					mSpace.setVisibility(View.GONE);
				} else {
					Toast.makeText(ctx,
							ctx.getString(R.string.msg_no_photo_galleries),
							Toast.LENGTH_SHORT).show();
					onAction(IHiddenView.ACTION_CANCEL);
				}
			}
		});
		mTask.execute();
	}
	
	@Override
	protected void initializeListViewAdapter(Context ctx, ICommand<?> command) {
		mAdapter = new PhotoCollectionItemAdapter(ctx, command);
		mLoadingMessage = ctx.getString(R.string.msg_loading_my_galleries);
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
