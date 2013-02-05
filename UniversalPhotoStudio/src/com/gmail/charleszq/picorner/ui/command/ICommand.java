/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command;

import android.content.Context;

import com.gmail.charleszq.picorner.utils.IConstants;

/**
 * Represents the command to initiate an action from UI. Usually, it should know
 * the <code>Context</code>, should have an icon and a text, it may also contain
 * other information.
 * 
 * @author charleszq
 * 
 */
public interface ICommand<T> {

	/**
	 * Executes the command and return <code>true</code> to say it's succeed,
	 * <code>false</code> otherwise.
	 * 
	 * @return
	 */
	boolean execute(Object... params);

	/**
	 * Returns the icon resource id.
	 * 
	 * @return
	 */
	int getIconResourceId();

	/**
	 * Returns the label.
	 * 
	 * @return
	 */
	String getLabel();

	/**
	 * Gets the full description of the command.
	 * 
	 * @return
	 */
	String getDescription();

	/**
	 * <ul>
	 * <li>IPhotoService.class, return a service to return photos;</li>
	 * <li>Integer.class, return the page size, default to that defined in
	 * {@link IConstants};</li>
	 * <li>AbstractFetchIconUrlTask.class, return the task to get the url of
	 * this command.</li>
	 * <li>Context.class, return <code>mContext</code>.
	 * <li>Comparator.class, return the real comparator, which will be used in
	 * photo grid page to check the command is the same as previous one.
	 * </ul>
	 * 
	 * @param adapterClass
	 * @return
	 */
	Object getAdapter(Class<?> adapterClass);

	/**
	 * 
	 * @param listener
	 */
	void setCommndDoneListener(ICommandDoneListener<T> listener);

	/**
	 * 
	 * @return
	 */
	CommandType getCommandType();

	/**
	 * Cancels the execution of this command.
	 */
	void cancel();

	/**
	 * 
	 * @param ctx
	 */
	void attacheContext(Context ctx);

}
