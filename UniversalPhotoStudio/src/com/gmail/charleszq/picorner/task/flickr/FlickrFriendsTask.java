/**
 * 
 */
package com.gmail.charleszq.picorner.task.flickr;

import java.util.List;

import android.content.Context;

import com.gmail.charleszq.picorner.model.Author;
import com.gmail.charleszq.picorner.task.AbstractContextAwareTask;

/**
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
		return null;
	}

}
