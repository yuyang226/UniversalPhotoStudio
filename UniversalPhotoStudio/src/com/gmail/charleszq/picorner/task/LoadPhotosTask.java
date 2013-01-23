/**
 * 
 */
package com.gmail.charleszq.picorner.task;

import java.util.List;

import android.content.Context;
import android.util.Log;

import com.gmail.charleszq.picorner.BuildConfig;
import com.gmail.charleszq.picorner.model.MediaObject;
import com.gmail.charleszq.picorner.model.MediaObjectCollection;
import com.gmail.charleszq.picorner.offline.IOfflineViewParameter;
import com.gmail.charleszq.picorner.offline.OfflineControlFileUtil;
import com.gmail.charleszq.picorner.service.IPhotoService;
import com.gmail.charleszq.picorner.ui.command.ICommand;
import com.gmail.charleszq.picorner.utils.IConstants;

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

		IOfflineViewParameter offlineParam = (IOfflineViewParameter) mCommand
				.getAdapter(IOfflineViewParameter.class);
		Context ctx = (Context) mCommand.getAdapter(Context.class);
		if (offlineParam != null
				&& OfflineControlFileUtil.isOfflineViewEnabled(ctx,
						offlineParam)
				&& OfflineControlFileUtil.isOfflineControlFileReady(ctx,
						offlineParam)) {
			if (pageNo == 0) {
				List<MediaObject> photos = offlineParam
						.getPhotoCollectionProcessor().getCachedPhotos(ctx,
								offlineParam);
				if (photos != null) {
					MediaObjectCollection mc = new MediaObjectCollection();
					for (MediaObject photo : photos) {
						mc.addPhoto(photo);
					}
					if (BuildConfig.DEBUG)
						Log.e(TAG, "Returns photos from offline cache."); //$NON-NLS-1$
					return mc;
				}
			} else {
				return null;
			}
		}

		if (BuildConfig.DEBUG)
			Log.d(TAG, "Trying to get photos from server."); //$NON-NLS-1$
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
		Log.d(TAG,
				String.format("%s photos returned.", result.getPhotos().size())); //$NON-NLS-1$
		super.onPostExecute(result);
	}

}
