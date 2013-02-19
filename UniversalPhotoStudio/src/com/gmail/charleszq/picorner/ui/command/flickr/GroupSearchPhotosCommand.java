/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command.flickr;

import android.content.Context;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.ui.command.PhotoListCommand;
import com.gmail.charleszq.picorner.ui.flickr.GroupSearchHiddenListView;
import com.gmail.charleszq.picorner.ui.helper.IHiddenView;
import com.googlecode.flickrjandroid.groups.Group;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class GroupSearchPhotosCommand extends PhotoListCommand {

	private Group mFlickrGroup;
	private IHiddenView mHiddenView;

	/**
	 * @param context
	 */
	public GroupSearchPhotosCommand(Context context) {
		super(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.picorner.ui.command.ICommand#getIconResourceId()
	 */
	@Override
	public int getIconResourceId() {
		return R.drawable.ic_action_friends; // TODO replace the icon
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.picorner.ui.command.ICommand#getLabel()
	 */
	@Override
	public String getLabel() {
		return mContext.getString(R.string.menu_item_flickr_group_search);
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == IHiddenView.class) {
			if (mHiddenView == null) {
				mHiddenView = new GroupSearchHiddenListView();
			}
			return mHiddenView;
		}
		return super.getAdapter(adapterClass);
	}
	
	

}
