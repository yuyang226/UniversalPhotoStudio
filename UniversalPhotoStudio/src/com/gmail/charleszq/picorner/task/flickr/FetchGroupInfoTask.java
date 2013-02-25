/**
 * 
 */
package com.gmail.charleszq.picorner.task.flickr;

import com.gmail.charleszq.picorner.task.AbstractGeneralTask;
import com.gmail.charleszq.picorner.utils.FlickrHelper;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.groups.Group;
import com.googlecode.flickrjandroid.groups.GroupsInterface;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class FetchGroupInfoTask extends
		AbstractGeneralTask<String, Integer, Group> {

	@Override
	protected Group doInBackground(String... params) {
		String id = params[0];
		Flickr f = FlickrHelper.getInstance().getFlickr();
		GroupsInterface gi = f.getGroupsInterface();
		try {
			return gi.getInfo(id);
		} catch (Exception e) {
			return null;
		}
	}

}
