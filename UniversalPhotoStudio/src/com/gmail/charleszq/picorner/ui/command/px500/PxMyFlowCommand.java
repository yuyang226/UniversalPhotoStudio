/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command.px500;

import android.content.Context;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.service.IPhotoService;
import com.gmail.charleszq.picorner.service.px500.PxMyFlowService;

/**
 * @author charles(charleszq@gmail.com)
 *
 */
public class PxMyFlowCommand extends AbstractPx500PhotoListCommand {

	/**
	 * @param context
	 */
	public PxMyFlowCommand(Context context) {
		super(context);
	}

	@Override
	public String getLabel() {
		return mContext.getString(R.string.px_my_flow);
	}

	@Override
	public String getDescription() {
		return mContext.getString(R.string.cd_500px_my_flow);
	}

	@Override
	public int getIconResourceId() {
		return R.drawable.ic_action_500px_myflow;
	}
	
	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == IPhotoService.class) {
			return new PxMyFlowService(getAuthToken(),
					getAuthTokenSecret(), getUserId());
		}
		return super.getAdapter(adapterClass);
	}

}
