/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command.px500;

import android.content.Context;

import com.github.yuyang226.j500px.photos.PhotoCategory;
import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.service.IPhotoService;
import com.gmail.charleszq.picorner.service.px500.Px500SearchService;
import com.gmail.charleszq.picorner.ui.helper.IHiddenView;
import com.gmail.charleszq.picorner.ui.px500.Px500SearchView;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class TermTagSearchCommand extends AbstractPx500PhotoListCommand {
	
	/**
	 * the term to be search
	 */
	private String mTerm;
	
	/**
	 * The tag to be search
	 */
	private String mTag;
	
	/**
	 * The hidden view associated with this command.
	 */
	private IHiddenView mHiddenView;

	/**
	 * @param context
	 */
	public TermTagSearchCommand(Context context) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.picorner.ui.command.ICommand#getLabel()
	 */
	@Override
	public String getLabel() {
		return mContext.getString(R.string.cmd_name_500px_search);
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == PhotoCategory.class)
			//this command does not support photo category.
			return null;
		if (adapterClass == IHiddenView.class) {
			if (mHiddenView == null) {
				mHiddenView = new Px500SearchView();
			}
			return mHiddenView;
		}
		if( adapterClass == IPhotoService.class ) {
			mCurrentPhotoService = new Px500SearchService();
			((Px500SearchService)mCurrentPhotoService).setSearchCondition(mTerm, mTag);
			return mCurrentPhotoService;
		}
		return super.getAdapter(adapterClass);
	}

	@Override
	public boolean execute(Object... params) {
		if( params.length == 2 ) {
			mTerm = (String) params[0];
			mTag = (String) params[1];
		}
		return super.execute();
	}
}
