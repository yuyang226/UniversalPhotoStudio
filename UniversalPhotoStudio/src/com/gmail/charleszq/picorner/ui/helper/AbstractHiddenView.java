/**
 * 
 */
package com.gmail.charleszq.picorner.ui.helper;

import android.view.View;

import com.gmail.charleszq.picorner.ui.command.ICommand;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public abstract class AbstractHiddenView implements IHiddenView {
	protected String TAG = getClass().getSimpleName();

	protected ICommand<?> mCommand;
	protected IHiddenViewActionListener mHideViewCancelListener;
	protected View mView;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gmail.charleszq.picorner.ui.helper.IHiddenView#init(com.gmail.charleszq
	 * .picorner.ui.command.ICommand,
	 * com.gmail.charleszq.picorner.ui.helper.IHiddenView
	 * .IHiddenViewActionListener)
	 */
	@Override
	public void init(ICommand<?> command, IHiddenViewActionListener listener) {
		this.mCommand = command;
		this.mHideViewCancelListener = listener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.picorner.ui.helper.IHiddenView#onAction(int,
	 * java.lang.Object[])
	 */
	@Override
	public void onAction(int action, Object... objects) {
		
		//in some cases, the image loader mighe be set pause, so resume it.
		ImageLoader.getInstance().resume();
		// notify the listener.
		mHideViewCancelListener.onAction(action, mCommand, this, objects);
	}

}
