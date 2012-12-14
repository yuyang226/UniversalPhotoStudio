/**
 * 
 */
package com.gmail.charleszq.ups.ui.command.flickr;

import android.content.Context;

import com.gmail.charleszq.ups.R;
import com.gmail.charleszq.ups.service.IPhotoService;
import com.gmail.charleszq.ups.service.flickr.FlickrInterestingPhotosService;
import com.gmail.charleszq.ups.ui.command.PhotoListCommand;

/**
 * 
 * @author Charles(charleszq@gmail.com)
 * 
 */
public class FlickrIntestringCommand extends
		PhotoListCommand {

	/**
	 * @param context
	 */
	public FlickrIntestringCommand(Context context) {
		super(context);
	}

	@Override
	public String getLabel() {
		return mContext.getResources().getString(R.string.f_interesting_photos);
	}

	@Override
	public int getIconResourceId() {
		return R.drawable.f_interest;
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == IPhotoService.class) {
			return new FlickrInterestingPhotosService();
		}
		if( adapterClass == Integer.class ) {
			return 200;
		}
		return super.getAdapter(adapterClass);
	}
	
	
}
