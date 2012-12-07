/**
 * 
 */
package com.gmail.charleszq.ups.ui.command;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;

import com.gmail.charleszq.ups.task.IGeneralTaskDoneListener;

/**
 * T should be the return type from the task.
 * 
 * @author Charles(charleszq@gmail.com)
 * 
 */
public abstract class AbstractCommand<T> implements ICommand<T> {

	protected Set<ICommandDoneListener<T>> mListeners;

	protected IGeneralTaskDoneListener<T> mTaskDoneListner = new IGeneralTaskDoneListener<T>() {

		@Override
		public void onTaskDone(T result) {
			AbstractCommand.this.onTaskDone(result);
		}
	};

	/**
	 * The context.
	 */
	protected Context mContext;

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		return null;
	}

	protected void onTaskDone(T result) {
		onCommandDone(result);
	}

	public AbstractCommand(Context context) {
		this.mContext = context;
	}

	@Override
	public void addCommndDoneListener(ICommandDoneListener<T> listener) {
		if (mListeners == null) {
			mListeners = new HashSet<ICommandDoneListener<T>>();
		}
		mListeners.add(listener);
	}

	@Override
	public void removeCommandDoneListener(ICommandDoneListener<T> listener) {
		if (mListeners != null) {
			mListeners.remove(listener);
		}
	}

	protected void onCommandDone(T result) {
		if (mListeners != null) {
			for (ICommandDoneListener<T> lis : mListeners) {
				lis.onCommandDone(this, result);
			}
		}
	}

	@Override
	public CommandType getCommandType() {
		return CommandType.GENERAL_CMD;
	}

	@Override
	public void cancel() {

	}
}
