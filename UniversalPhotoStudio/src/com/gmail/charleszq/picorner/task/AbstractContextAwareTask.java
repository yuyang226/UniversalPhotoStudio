/**
 * 
 */
package com.gmail.charleszq.picorner.task;

import android.content.Context;

/**
 * @author Charles(charleszq@gmail.com)
 * 
 */
public abstract class AbstractContextAwareTask<Params, Progress, Result>
		extends AbstractGeneralTask<Params, Progress, Result> {

	protected Context mContext;

	public AbstractContextAwareTask(Context ctx) {
		super();
		this.mContext = ctx;
	}
	
	
}
