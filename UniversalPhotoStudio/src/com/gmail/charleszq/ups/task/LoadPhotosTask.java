/**
 * 
 */
package com.gmail.charleszq.ups.task;

import android.util.Log;

import com.gmail.charleszq.ups.model.MediaObjectCollection;
import com.gmail.charleszq.ups.service.IPhotoService;
import com.gmail.charleszq.ups.ui.command.ICommand;
import com.gmail.charleszq.ups.utils.IConstants;

/**
 * 
 * @author Charles(charleszq@gmail.com)
 * 
 */
public class LoadPhotosTask extends
		AbstractGeneralTask<Integer, Integer, MediaObjectCollection> {

	private ICommand<?> mCommand;

	/**
	 * Constructor.
	 * 
	 * @param command
	 * @param lis
	 */
	public LoadPhotosTask(ICommand<MediaObjectCollection> command,
			IGeneralTaskDoneListener<MediaObjectCollection> lis) {
		mCommand = command;
		addTaskDoneListener(lis);
	}

	@Override
	protected MediaObjectCollection doInBackground(Integer... params) {

		int pageNo = 0;
		if (params.length == 1) {
			pageNo = params[0];
		}

		IPhotoService service = (IPhotoService) mCommand
				.getAdapter(IPhotoService.class);
		Integer total = (Integer) mCommand.getAdapter(Integer.class);
		total = total == null ? IConstants.DEF_SERVICE_PAGE_SIZE : total;
		if (service == null)
			return null;
		try {
			return service.getPhotos(total, pageNo);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	protected void onPostExecute(MediaObjectCollection result) {
		if (result == null) {
			super.onPostExecute(result);
			return;
		}
		Log.d(getClass().getName(),
				String.format("%s photos returned.", result.getPhotos().size())); //$NON-NLS-1$
		super.onPostExecute(result);
	}

}
