/**
 * 
 */
package com.gmail.charleszq.ups.task.flickr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;

import com.gmail.charleszq.ups.UPSApplication;
import com.gmail.charleszq.ups.task.AbstractGeneralTask;
import com.gmail.charleszq.ups.utils.FlickrHelper;
import com.googlecode.flickrjandroid.galleries.GalleriesInterface;
import com.googlecode.flickrjandroid.galleries.Gallery;
import com.googlecode.flickrjandroid.groups.Group;
import com.googlecode.flickrjandroid.groups.pools.PoolsInterface;
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

	private static final Logger logger = LoggerFactory
			.getLogger(FetchFlickrUserPhotoCollectionTask.class);

	/**
	 * The activity
	 */
	private Activity mActivity;
	private String mUserId, mToken, mSecret;

	public FetchFlickrUserPhotoCollectionTask(Activity act) {
		this.mActivity = act;
		UPSApplication app = (UPSApplication) mActivity.getApplication();
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
			logger.warn("Error to get user photo sets information: " //$NON-NLS-1$
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
		return photosets;
	}

}
