/**
 * 
 */
package com.gmail.charleszq.picorner.task.flickr;

import java.util.Set;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.task.AbstractContextAwareTask;
import com.gmail.charleszq.picorner.utils.FlickrHelper;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.groups.pools.PoolsInterface;
import com.googlecode.flickrjandroid.photos.PhotoPlace;
import com.googlecode.flickrjandroid.photosets.PhotosetsInterface;

/**
 * Represents the task to organize my own flickr photos, that is, put a photo
 * into sets/groups.
 * 
 * <p/>
 * the parameter is the photo id; the return value is the fail numbers.
 * 
 * @author charleszq
 * 
 */
public class FlickrOrganizePhotoTask extends
		AbstractContextAwareTask<String, Integer, Integer> {

	private Set<String> mPoolsToAddTo;
	private Set<String> mPoolsToRemoveFrom;

	private ProgressDialog mProgressDialog;

	public FlickrOrganizePhotoTask(Context ctx, Set<String> addPool,
			Set<String> removePool) {
		super(ctx);
		this.mPoolsToAddTo = addPool;
		this.mPoolsToRemoveFrom = removePool;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mProgressDialog = new ProgressDialog( mContext) ;
		mProgressDialog.setTitle(R.string.msg_org_my_f_photo_progress);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog
				.setMax(mPoolsToAddTo.size() + mPoolsToRemoveFrom.size());
		mProgressDialog.show();
	}

	@Override
	protected Integer doInBackground(String... params) {
		// photo id
		String photoId = params[0];

		// get the flickr api interfaces.
		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(mContext);
		PhotosetsInterface pi = f.getPhotosetsInterface();
		PoolsInterface pooli = f.getPoolsInterface();

		// fail number
		int failNumber = 0;

		// add to pools
		int progress = 1;
		for (String s : mPoolsToAddTo) {
			// 's' is in the format of '<photo place kind>+<pool id>'
			publishProgress(progress++);
			String kind = s.substring(0, 1);
			String poolId = s.substring(1);
			Log.d(TAG, s + " kind: " + kind + ", pool id: " + poolId); //$NON-NLS-1$//$NON-NLS-2$
			int intKind = Integer.parseInt(kind);
			switch (intKind) {
			case PhotoPlace.SET:
				try {
					pi.addPhoto(poolId, photoId);
				} catch (Exception e) {
					failNumber++;
					Log.e(TAG,
							String.format(
									"Fail to add to set '%s', reason: %s", poolId, e.getMessage())); //$NON-NLS-1$
				}
				break;
			case PhotoPlace.POOL:
				try {
					pooli.add(photoId, poolId);
				} catch (Exception e) {
					failNumber++;
					Log.e(TAG,
							String.format(
									"Fail to add to group '%s', reason: %s", poolId, e.getMessage())); //$NON-NLS-1$
				}
				break;
			}
		}

		for (String s : mPoolsToRemoveFrom) {
			publishProgress(progress++);
			String kind = s.substring(0, 1);
			String poolId = s.substring(1);
			Log.d(TAG, s + " kind: " + kind + ", pool id: " + poolId); //$NON-NLS-1$//$NON-NLS-2$
			int intKind = Integer.parseInt(kind);
			switch (intKind) {
			case PhotoPlace.SET:
				try {
					pi.removePhoto(poolId, photoId);
				} catch (Exception e) {
					failNumber++;
					Log.e(TAG,
							String.format(
									"Fail to remove from set '%s', reason: %s", poolId, e.getMessage())); //$NON-NLS-1$
				}
				break;
			case PhotoPlace.POOL:
				try {
					pooli.remove(photoId, poolId);
				} catch (Exception e) {
					failNumber++;
					Log.e(TAG,
							String.format(
									"Fail to remove from group '%s', reason: %s", poolId, e.getMessage())); //$NON-NLS-1$
				}
				break;
			}

		}

		return failNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gmail.charleszq.picorner.task.AbstractGeneralTask#onPostExecute(java
	 * .lang.Object)
	 */
	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
		if (mProgressDialog != null) {
			mProgressDialog.cancel();
		}
		if (result == 0) {
			Toast.makeText(mContext,
					mContext.getString(R.string.msg_org_my_f_photo_done),
					Toast.LENGTH_SHORT).show();
		} else if (result == mPoolsToAddTo.size() + mPoolsToRemoveFrom.size()) {
			Toast.makeText(mContext,
					mContext.getString(R.string.msg_org_my_f_photo_fail),
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(
					mContext,
					mContext.getString(R.string.msg_org_my_f_photo_partail_success),
					Toast.LENGTH_SHORT).show();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
	 */
	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		if (mProgressDialog != null) {
			mProgressDialog.setProgress(values[0]);
		}
	}
}
