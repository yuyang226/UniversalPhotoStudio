/**
 * 
 */
package com.gmail.charleszq.ups.task.flickr;

import com.gmail.charleszq.ups.task.AbstractGeneralTask;
import com.gmail.charleszq.ups.utils.FlickrHelper;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.people.PeopleInterface;
import com.googlecode.flickrjandroid.people.User;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class FlickrGetUserInfoTask extends
		AbstractGeneralTask<String, Integer, User> {

	@Override
	protected User doInBackground(String... params) {
		String id = params[0];
		Flickr f = FlickrHelper.getInstance().getFlickr();
		PeopleInterface pi = f.getPeopleInterface();
		try {
			User u = pi.getInfo(id);
			return u;
		} catch (Exception e) {
		}
		return null;
	}

}
