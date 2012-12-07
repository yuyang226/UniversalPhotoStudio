/**
 * 
 */
package com.gmail.charleszq.ups.ui.command;

import android.content.Context;

import com.gmail.charleszq.ups.model.MediaObjectCollection;
import com.gmail.charleszq.ups.task.LoadPhotosTask;

/**
 * @author Charles(charleszq@gmail.com)
 * 
 */
public abstract class PhotoListCommand extends
		AbstractCommand<MediaObjectCollection> {
	
	protected LoadPhotosTask mTask;

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
		mTask = new LoadPhotosTask(this, this.mTaskDoneListner);
		
		int page = 0;
		if( params.length > 0 ) {
			page = (Integer)params[0];
		}
		mTask.execute(page);
		return true;
	}

	@Override
	public void cancel() {
		mTask.cancel(true);
	}

}
