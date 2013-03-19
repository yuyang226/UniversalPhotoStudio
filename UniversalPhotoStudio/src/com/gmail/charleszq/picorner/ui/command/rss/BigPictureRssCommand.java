/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command.rss;

import android.content.Context;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.service.IPhotoService;
import com.gmail.charleszq.picorner.service.rss.BigPictureService;
import com.gmail.charleszq.picorner.ui.command.PhotoListCommand;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class BigPictureRssCommand extends PhotoListCommand {

	public BigPictureRssCommand(Context context) {
		super(context);
	}

	@Override
	public int getIconResourceId() {
		return R.drawable.ic_action_rss;
	}

	@Override
	public String getLabel() {
		return mContext.getString(R.string.cmd_big_picture);
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if( adapterClass == IPhotoService.class ) {
			return new BigPictureService();
		}
		return super.getAdapter(adapterClass);
	}
	
	

}
