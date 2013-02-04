/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command;

import android.content.Context;

import com.gmail.charleszq.picorner.model.MediaObjectCollection;
import com.gmail.charleszq.picorner.service.IPhotoService;
import com.gmail.charleszq.picorner.task.LoadPhotosTask;
import com.gmail.charleszq.picorner.utils.IConstants;

/**
 * @author Charles(charleszq@gmail.com)
 * 
 */
public abstract class PhotoListCommand extends
		AbstractCommand<MediaObjectCollection> {

	protected LoadPhotosTask mTask;
	protected IPhotoService mCurrentPhotoService;
	protected int mCurrentPageNo = 0;

	public PhotoListCommand(Context context) {
		super(context);
	}

	@Override
	public CommandType getCommandType() {
		return CommandType.PHOTO_LIST_CMD;
	}

	@Override
	public boolean execute(Object... params) {
		if (mTask != null) {
			mTask.cancel(true);
		}

		// must set this to null to clear the cache data executed before if user
		// hit the menu item from the main menu.
		mCurrentPhotoService = null;
		mCurrentPageNo = 0;
		mTask = new LoadPhotosTask(this, this.mTaskDoneListner);

		if (params.length > 0) {
			mCurrentPageNo = (Integer) params[0];
		}
		mTask.execute(mCurrentPageNo);
		return true;
	}

	@Override
	public void cancel() {
		if (mTask != null)
			mTask.cancel(true);
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == Integer.class) {
			return IConstants.DEF_SERVICE_PAGE_SIZE;
		}
		return super.getAdapter(adapterClass);
	}

	/**
	 * Loads next page of photos.
	 * 
	 * @return
	 */
	public boolean loadNextPage() {
		if (mTask != null) {
			mTask.cancel(true);
		}
		mCurrentPageNo++;
		mTask = new LoadPhotosTask(this, this.mTaskDoneListner);
		mTask.execute(mCurrentPageNo);
		return true;
	}
}
