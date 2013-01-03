/**
 * 
 */
package com.gmail.charleszq.picorner.task.flickr;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.model.FlickrUserPhotoPool;
import com.gmail.charleszq.picorner.task.AbstractGeneralTask;
import com.gmail.charleszq.picorner.utils.FlickrHelper;
import com.gmail.charleszq.picorner.utils.IConstants;
import com.gmail.charleszq.picorner.utils.ModelUtils;
import com.googlecode.flickrjandroid.galleries.GalleriesInterface;
import com.googlecode.flickrjandroid.galleries.Gallery;
import com.googlecode.flickrjandroid.groups.Group;
import com.googlecode.flickrjandroid.groups.pools.PoolsInterface;
import com.googlecode.flickrjandroid.photosets.Photoset;
import com.googlecode.flickrjandroid.photosets.Photosets;
import com.googlecode.flickrjandroid.photosets.PhotosetsInterface;

/**
 * Represents the task to fetch user's flickr photo sets, groups and galleries.
 * 
 * @author Charles(charleszq@gmail.com)
 * 
 */
public class FetchFlickrUserPhotoCollectionTask extends
		AbstractGeneralTask<Void, Integer, Collection<?>> {

	/**
	 * The activity
	 */
	private Activity mActivity;
	private String mUserId, mToken, mSecret;

	public FetchFlickrUserPhotoCollectionTask(Activity act) {
		this.mActivity = act;
		PicornerApplication app = (PicornerApplication) mActivity
				.getApplication();
		mToken = app.getFlickrToken();
		mSecret = app.getFlickrTokenSecret();
		mUserId = app.getFlickrUserId();
	}

	@Override
	protected Collection<?> doInBackground(Void... params) {
		List<Object> photosets = new ArrayList<Object>();

		PhotosetsInterface psi = FlickrHelper.getInstance()
				.getFlickrAuthed(mToken, mSecret).getPhotosetsInterface();
		try {
			Photosets ps = psi.getList(mUserId);
			photosets.addAll(ps.getPhotosets());
		} catch (Exception e) {
			Log.w(TAG, "Error to get user photo sets information: " //$NON-NLS-1$
					+ e.getMessage());
		}

		PoolsInterface poolInterface = FlickrHelper.getInstance()
				.getFlickrAuthed(mToken, mSecret).getPoolsInterface();
		try {
			Collection<Group> groups = poolInterface.getGroups();
			photosets.addAll(groups);
		} catch (Exception e) {
		}

		GalleriesInterface gi = FlickrHelper.getInstance()
				.getFlickrAuthed(mToken, mSecret).getGalleriesInterface();
		try {
			Collection<Gallery> galleries = gi.getList(mUserId, -1, -1);
			photosets.addAll(galleries);
		} catch (Exception e) {
		}

		cachePool(photosets);

		return photosets;
	}

	private void cachePool(List<Object> pools) {
		File bsRoot = new File(Environment.getExternalStorageDirectory(),
				IConstants.SD_CARD_FOLDER_NAME);
		if (!bsRoot.exists() && !bsRoot.mkdir()) {
			return;
		}

		List<FlickrUserPhotoPool> userPools = new ArrayList<FlickrUserPhotoPool>();
		for (Object obj : pools) {
			FlickrUserPhotoPool pool = new FlickrUserPhotoPool();
			if (obj instanceof Photoset) {
				Photoset ps = (Photoset) obj;
				pool.setType(FlickrUserPhotoPool.TYPE_PHOTO_SET);
				pool.setId(ps.getId());
				pool.setIconUrl(ps.getId());
				pool.setPhotoCount(ps.getPhotoCount());
				pool.setTitle(ps.getTitle());
			} else if (obj instanceof Group) {
				Group g = (Group) obj;
				pool.setType(FlickrUserPhotoPool.TYPE_GROUP);
				pool.setId(g.getId());
				pool.setIconUrl(g.getBuddyIconUrl());
				pool.setPhotoCount(g.getPhotoCount());
				pool.setTitle(g.getName());
			} else {
				Gallery gallery = (Gallery) obj;
				pool.setType(FlickrUserPhotoPool.TYPE_GALLERY);
				pool.setId(gallery.getGalleryId());
				// we will use the primary photo id to get the icon url.
				pool.setIconUrl(gallery.getPrimaryPhotoId());
				pool.setPhotoCount(gallery.getPhotoCount());
				pool.setTitle(gallery.getTitle());
			}
			Log.d(TAG, pool.toString());
			userPools.add(pool);
		}
		File poolFile = new File(bsRoot, IConstants.FLICKR_USER_POOL_FILE_NAME);
		try {
			ModelUtils.writeFlickrUserPhotoPools(userPools, poolFile);
		} catch (IOException e) {
			Log.d(TAG,
					"Error to cache flickr user pool information: " + e.getMessage()); //$NON-NLS-1$
		}
	}

}
