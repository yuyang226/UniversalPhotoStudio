/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command.flickr;

import java.util.Comparator;

import android.content.Context;
import android.view.LayoutInflater;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.service.IPhotoService;
import com.gmail.charleszq.picorner.service.flickr.FlickrTagSearchService;
import com.gmail.charleszq.picorner.ui.command.PhotoListCommand;
import com.gmail.charleszq.picorner.ui.helper.IHiddenView;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class FlickrTagSearchCommand extends PhotoListCommand {

	private IHiddenView mHiddenView;
	private String mCurrentSearchTag;

	/**
	 * @param context
	 */
	public FlickrTagSearchCommand(Context context) {
		super(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.picorner.ui.command.ICommand#getIconResourceId()
	 */
	@Override
	public int getIconResourceId() {
		return android.R.drawable.ic_menu_search;
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == IHiddenView.class) {
			if (mHiddenView == null) {
				mHiddenView = (IHiddenView) LayoutInflater.from(mContext)
						.inflate(R.layout.flickr_tag_search, null);
			}
			return mHiddenView;
		}
		if (adapterClass == IPhotoService.class) {
			if (mCurrentPhotoService == null) {
				mCurrentPhotoService = new FlickrTagSearchService();
				((FlickrTagSearchService) mCurrentPhotoService)
						.setTags(mCurrentSearchTag);
			}
			return mCurrentPhotoService;
		}
		if (adapterClass == Comparator.class) {
			return this.mCurrentSearchTag;
		}
		return super.getAdapter(adapterClass);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.picorner.ui.command.ICommand#getLabel()
	 */
	@Override
	public String getLabel() {
		return mContext.getString(R.string.cmd_name_flickr_tag_search);
	}

	@Override
	public boolean execute(Object... params) {
		mCurrentSearchTag = (String) params[0];

		// parent execute method requires the first parameter is page no
		return super.execute();
	}

}
