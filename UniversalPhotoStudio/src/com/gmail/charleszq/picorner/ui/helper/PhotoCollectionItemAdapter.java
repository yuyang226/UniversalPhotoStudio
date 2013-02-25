/**
 * 
 */
package com.gmail.charleszq.picorner.ui.helper;

import android.content.Context;

import com.gmail.charleszq.picorner.ui.command.ICommand;
import com.googlecode.flickrjandroid.galleries.Gallery;
import com.googlecode.flickrjandroid.groups.Group;
import com.googlecode.flickrjandroid.photosets.Photoset;

/**
 * Represents the adapter to show photo sets, groups and galleries.
 * 
 * @author charles(charleszq@gmail.com)
 * 
 */
public class PhotoCollectionItemAdapter extends FilterAdapter {

	public PhotoCollectionItemAdapter(Context ctx, ICommand<?> command) {
		super(ctx, command);
	}

	@Override
	public String getTitle(Object item) {
		if (Photoset.class.isInstance(item)) {
			return ((Photoset) item).getTitle();
		} else if (Group.class.isInstance(item)) {
			Group g = (Group) item;
			return g.getName() + " (" + g.getPhotoCount() + ")";
		} else if (Gallery.class.isInstance(item)) {
			return ((Gallery) item).getTitle();
		}
		return null;
	}

}
