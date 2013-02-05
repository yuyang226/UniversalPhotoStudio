/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command;

import android.content.Context;

import com.gmail.charleszq.picorner.task.IGeneralTaskDoneListener;

/**
 * T should be the return type from the task.
 * 
 * @author Charles(charleszq@gmail.com)
 * 
 */
public abstract class AbstractCommand<T> implements ICommand<T> {

	protected ICommandDoneListener<T> mListener;
	protected String TAG = getClass().getSimpleName();

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
	public void setCommndDoneListener(ICommandDoneListener<T> listener) {
		this.mListener = listener;
	}

	protected void onCommandDone(T result) {
		if (mListener != null) {
			mListener.onCommandDone(this, result);
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
	public String getDescription() {
		return getLabel();
	}
}
