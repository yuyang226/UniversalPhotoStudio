/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command.flickr;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.model.Author;
import com.gmail.charleszq.picorner.ui.command.AbstractCommand;
import com.gmail.charleszq.picorner.ui.helper.IHiddenView;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class FlickrFriendListCommand extends AbstractCommand<List<Author>> {

	private IHiddenView	mHiddenView;

	public FlickrFriendListCommand(Context context) {
		super(context);
	}

	@Override
	public boolean execute(Object... params) {
		return false;
	}

	@Override
	public int getIconResourceId() {
		return R.drawable.ic_action_friends;
	}

	@Override
	public String getLabel() {
		return mContext.getString(R.string.menu_header_flickr);
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == IHiddenView.class) {
			if (mHiddenView == null) {
				mHiddenView = (IHiddenView) LayoutInflater.from(mContext)
						.inflate(R.layout.flickr_contacts_list, null);
			}
			return mHiddenView;
		}
		return super.getAdapter(adapterClass);
	}

}
