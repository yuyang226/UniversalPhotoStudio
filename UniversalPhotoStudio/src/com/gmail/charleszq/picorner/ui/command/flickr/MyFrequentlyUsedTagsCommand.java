/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command.flickr;

import java.util.Comparator;

import android.app.ActionBar;
import android.content.Context;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.SPUtil;
import com.gmail.charleszq.picorner.model.FlickrTagSearchParameter;
import com.gmail.charleszq.picorner.service.IPhotoService;
import com.gmail.charleszq.picorner.service.flickr.FlickrTagSearchService;
import com.gmail.charleszq.picorner.ui.command.PhotoListCommand;
import com.gmail.charleszq.picorner.ui.flickr.MyFrequentlyUsedTagsView;
import com.gmail.charleszq.picorner.ui.helper.IHiddenView;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class MyFrequentlyUsedTagsCommand extends PhotoListCommand {

	private FlickrTagSearchParameter	mSearchParameter;
	private IHiddenView					mHiddenView;

	/**
	 * @param context
	 */
	public MyFrequentlyUsedTagsCommand(Context context) {
		super(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.picorner.ui.command.ICommand#getIconResourceId()
	 */
	@Override
	public int getIconResourceId() {
		return R.drawable.ic_action_tags;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.picorner.ui.command.ICommand#getLabel()
	 */
	@Override
	public String getLabel() {
		return mContext.getString(R.string.menu_item_my_frequently_used_tags);
	}

	@Override
	public boolean execute(Object... params) {
		mSearchParameter = (FlickrTagSearchParameter) params[0];
		mSearchParameter.setUserId(SPUtil.getFlickrUserId(mContext));
		return super.execute();
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == IHiddenView.class) {
			if (mHiddenView == null) {
				mHiddenView = new MyFrequentlyUsedTagsView();
			}
			return mHiddenView;
		}
		if (adapterClass == IPhotoService.class) {
			if (mCurrentPhotoService == null) {
				String token = SPUtil.getFlickrAuthToken(mContext);
				String secret = SPUtil.getFlickrAuthTokenSecret(mContext);
				mCurrentPhotoService = new FlickrTagSearchService(
						token, secret);
			}
			((FlickrTagSearchService) mCurrentPhotoService)
					.setSearchParameter(mSearchParameter);
			return mCurrentPhotoService;
		}
		if (adapterClass == Comparator.class) {
			return this.mSearchParameter;
		}
		if( adapterClass == ActionBar.class )
		{
			return Boolean.FALSE.toString();
		}
		return super.getAdapter(adapterClass);
	}

	@Override
	public String getDescription() {
		String msg = mContext.getString(R.string.cd_flickr_my_tags);
		return String.format(msg, mSearchParameter.getTags());
	}
}
