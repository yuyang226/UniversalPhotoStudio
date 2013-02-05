/**
 * 
 */
package com.gmail.charleszq.picorner.task.flickr;

import java.util.Collection;

import android.content.Context;
import android.util.Log;

import com.gmail.charleszq.picorner.BuildConfig;
import com.gmail.charleszq.picorner.task.AbstractContextAwareTask;
import com.gmail.charleszq.picorner.utils.FlickrHelper;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.tags.Tag;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class MyFrequentlyUsedTagsTask extends
		AbstractContextAwareTask<Void, Integer, Collection<Tag>> {

	public MyFrequentlyUsedTagsTask(Context ctx) {
		super(ctx);
	}

	@Override
	protected Collection<Tag> doInBackground(Void... params) {
		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(mContext);

		try {
			return f.getTagsInterface().getMostFrequentlyUsed();
		} catch (Exception e) {
			if(BuildConfig.DEBUG)
				Log.w(TAG, "unable to get my tags: "+ e.getMessage()); //$NON-NLS-1$
			return null;
		}
	}

}
