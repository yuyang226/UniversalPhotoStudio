/**
 * 
 */
package com.gmail.charleszq.ups.ui.command.ig;

import android.content.Context;

import com.gmail.charleszq.ups.ui.command.PhotoListCommand;
import com.gmail.charleszq.ups.utils.IConstants;

/**
 * @author charles(charleszq@gmail.com)
 *
 */
public abstract class AbstractInstagramPhotoListCommand extends
		PhotoListCommand {

	/**
	 * @param context
	 */
	public AbstractInstagramPhotoListCommand(Context context) {
		super(context);
	}
	
	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == Integer.class) {
			return IConstants.DEF_IG_PAGE_SIZE;
		}
		return super.getAdapter(adapterClass);
	}


}
