/**
 * 
 */
package com.gmail.charleszq.picorner.task.flickr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.gmail.charleszq.picorner.BuildConfig;
import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.model.Author;
import com.gmail.charleszq.picorner.task.AbstractContextAwareTask;
import com.gmail.charleszq.picorner.utils.FlickrHelper;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.contacts.Contact;

/**
 * Represents the task to get all my flickr friend list.
 * 
 * @author charles(charleszq@gmail.com)
 * 
 */
public class FlickrFriendsTask extends
		AbstractContextAwareTask<Void, Integer, List<Author>> {

	public FlickrFriendsTask(Context ctx) {
		super(ctx);
	}

	@Override
	protected List<Author> doInBackground(Void... params) {
		PicornerApplication app = (PicornerApplication) ((Activity) mContext)
				.getApplication();
		String token = app.getFlickrToken();
		String secret = app.getFlickrTokenSecret();
		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(token, secret);
		try {
			Collection<Contact> friends = f.getContactsInterface().getList();
			List<Author> authors = new ArrayList<Author>();
			for (Contact c : friends) {
				Author a = new Author();
				a.setUserId(c.getId());
				a.setUserName(c.getUsername());
				a.setBuddyIconUrl(c.getBuddyIconUrl());
				authors.add(a);
			}
			return authors;
		} catch (Exception e) {
			if (BuildConfig.DEBUG)
				Log.w(TAG,
						"unable to get my flickr friend list: " + e.getMessage()); //$NON-NLS-1$
			return null;
		}
	}

}
