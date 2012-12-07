/**
 * 
 */
package com.gmail.charleszq.ups.ui.command;

import android.content.Context;

/**
 * @author Charles(charleszq@gmail.com)
 * 
 */
public class DummyCommand extends AbstractCommand<String> {

	private String mLabel;

	/**
	 * @param context
	 */
	public DummyCommand(Context context, String label) {
		super(context);
		this.mLabel = label;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ups.ui.command.ICommand#execute()
	 */
	@Override
	public boolean execute(Object... objects) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ups.ui.command.ICommand#getIconResourceId()
	 */
	@Override
	public int getIconResourceId() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ups.ui.command.ICommand#getLabel()
	 */
	@Override
	public String getLabel() {
		return mLabel;
	}

}
