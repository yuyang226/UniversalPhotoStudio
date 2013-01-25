/**
 * 
 */
package com.gmail.charleszq.picorner.ui.helper;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.gmail.charleszq.picorner.ui.command.ICommand;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public abstract class AbstractLinearLayoutHiddenView extends LinearLayout
		implements IHiddenView {

	protected ICommand<?>				mCommand;
	protected IHiddenViewActionListener	mHideViewCancelListener;

	/**
	 * @param context
	 */
	public AbstractLinearLayoutHiddenView(Context context) {
		super(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public AbstractLinearLayoutHiddenView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public AbstractLinearLayoutHiddenView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

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
		// notify the listener.
		mHideViewCancelListener.onAction(action, mCommand, this, objects);
	}

}
