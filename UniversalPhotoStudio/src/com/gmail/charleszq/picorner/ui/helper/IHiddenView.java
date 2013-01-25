/**
 * 
 */
package com.gmail.charleszq.picorner.ui.helper;

import com.gmail.charleszq.picorner.ui.command.ICommand;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public interface IHiddenView {

	static final int	ACTION_CANCEL	= 0;
	static final int	ACTION_DO		= 1;

	void init(ICommand<?> command, IHiddenViewActionListener listener);

	/**
	 * 
	 * @param action
	 *            the action id
	 * @param objects
	 *            any data the view wants to returned back to the main caller
	 */
	void onAction(int action, Object... objects);

	public interface IHiddenViewActionListener {

		/**
		 * 
		 * @param action
		 * @param command
		 * @param view
		 * @param data
		 */
		void onAction(int action, ICommand<?> command, IHiddenView view,
				Object... data);
	}

}
