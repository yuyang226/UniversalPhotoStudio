/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;

import com.gmail.charleszq.picorner.task.IGeneralTaskDoneListener;

/**
 * T should be the return type from the task.
 * 
 * @author Charles(charleszq@gmail.com)
 * 
 */
public abstract class AbstractCommand<T> implements ICommand<T> {

	protected Set<ICommandDoneListener<T>> mListeners;

	/**
	 * the command section header title, used to identify which group this
	 * command belongs to.
	 */
	protected String mCommandCategory;

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
		if (adapterClass == Context.class) {
			return mContext;
		}
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

	@Override
	public void clearCommandDoneListener() {
		if (mListeners != null) {
			mListeners.clear();
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

	@Override
	public void attacheContext(Context ctx) {
		this.mContext = ctx;
	}

	@Override
	public void setCommandCategory(String category) {
		this.mCommandCategory = category;

	}

	@Override
	public String getCommandCategory() {
		return mCommandCategory;
	}

	@Override
	public String getDescription() {
		return getLabel();
	}

}
