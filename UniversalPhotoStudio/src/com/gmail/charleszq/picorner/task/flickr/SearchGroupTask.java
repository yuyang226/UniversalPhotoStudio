/**
 * 
 */
package com.gmail.charleszq.picorner.task.flickr;

import java.util.Collection;

import android.util.Log;

import com.gmail.charleszq.picorner.task.AbstractGeneralTask;
import com.gmail.charleszq.picorner.utils.FlickrHelper;
import com.gmail.charleszq.picorner.utils.IConstants;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.groups.Group;
import com.googlecode.flickrjandroid.groups.GroupsInterface;

/**
 * Represents the task to search flickr groups
 * 
 * @author charles(charleszq@gmail.com)
 * 
 */
public class SearchGroupTask extends
		AbstractGeneralTask<Integer, Integer, Collection<Group>> {

	/**
	 * the current query string
	 */
	private String mQueryString;

	public SearchGroupTask(String query) {
		this.mQueryString = query;
	}

	@Override
	protected Collection<Group> doInBackground(Integer... params) {
		int page = params[0];

		Flickr f = FlickrHelper.getInstance().getFlickr();
		GroupsInterface gi = f.getGroupsInterface();
		try {
			return gi.search(mQueryString,
					IConstants.DEF_PHOTO_SET_GROUP_PAGE_SIZE, page);
		} catch (Exception e) {
			Log.w(TAG, "unable to search group: " + e.getMessage()); //$NON-NLS-1$
		}
		return null;
	}

}
