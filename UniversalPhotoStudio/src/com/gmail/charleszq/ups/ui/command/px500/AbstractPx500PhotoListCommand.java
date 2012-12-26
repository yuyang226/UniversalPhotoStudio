/**
 * 
 */
package com.gmail.charleszq.ups.ui.command.px500;

import android.content.Context;

import com.gmail.charleszq.ups.ui.command.PhotoListCommand;
import com.gmail.charleszq.ups.utils.IConstants;

/**
 * @author charles(charleszq@gmail.com)
 *
 */
public abstract class AbstractPx500PhotoListCommand extends PhotoListCommand {

	/**
	 * @param context
	 */
	public AbstractPx500PhotoListCommand(Context context) {
		super(context);
	}
	
	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == Integer.class) {
			return IConstants.DEF_500PX_PAGE_SIZE;
		}
		return super.getAdapter(adapterClass);
	}


}
