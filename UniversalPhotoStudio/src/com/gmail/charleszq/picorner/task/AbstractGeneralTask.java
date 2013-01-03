/**
 * 
 */
package com.gmail.charleszq.picorner.task;

import java.util.HashSet;
import java.util.Set;

import android.os.AsyncTask;

/**
 * @author Charles(charleszq@gmail.com)
 * 
 */
public abstract class AbstractGeneralTask<Params, Progress, Result> extends
		AsyncTask<Params, Progress, Result> {
	
	/**
	 * for log
	 */
	protected  String TAG = getClass().getSimpleName();

	protected Set<IGeneralTaskDoneListener<Result>> mListeners;

	public void addTaskDoneListener(IGeneralTaskDoneListener<Result> lis) {
		if (mListeners == null) {
			mListeners = new HashSet<IGeneralTaskDoneListener<Result>>();
		}
		mListeners.add(lis);
	}

	@Override
	protected void onPostExecute(Result result) {
		super.onPostExecute(result);
		if (mListeners != null) {
			for (IGeneralTaskDoneListener<Result> lis : mListeners) {
				lis.onTaskDone(result);
			}
		}
	}

}
