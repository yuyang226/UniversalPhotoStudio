/**
 * 
 */
package com.gmail.charleszq.picorner.task.flickr;

import android.content.Context;

import com.gmail.charleszq.picorner.task.AbstractFetchIconUrlTask;
import com.gmail.charleszq.picorner.utils.FlickrHelper;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.people.PeopleInterface;
import com.googlecode.flickrjandroid.people.User;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class FetchFlickrUserIconUrlTask extends AbstractFetchIconUrlTask {

	private String mUserId;

	/**
	 * @param ctx
	 */
	public FetchFlickrUserIconUrlTask(Context ctx, String userId) {
		super(ctx);
		this.mUserId = userId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected String doInBackground(Object... params) {
		beforeExecute(params);
		Flickr f = FlickrHelper.getInstance().getFlickr();
		PeopleInterface pi = f.getPeopleInterface();
		try {
			User u = pi.getInfo(mUserId);
			if( u != null ) {
				logger.debug( "flickr user buddy icon url: " + u.getBuddyIconUrl()); //$NON-NLS-1$
			}
			return u.getBuddyIconUrl();
		} catch (Exception e) {
			
		}
		return null;
	}

}
