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

	/**
	 * Cancel the hidden view.
	 */
	static final int	ACTION_CANCEL	= 0;
	
	/**
	 * do the command and hide the UI
	 */
	static final int	ACTION_DO		= 1;
	
	/**
	 * The variable to say just do the command, but don't hide the UI.
	 */
	static final int 	ACTION_JUST_CMD = 2;

	/**
	 * Initializes the hidden view.
	 * @param command
	 * @param listener
	 */
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
