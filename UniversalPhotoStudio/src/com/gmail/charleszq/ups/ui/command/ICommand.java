/**
 * 
 */
package com.gmail.charleszq.ups.ui.command;

import android.content.Context;

import com.gmail.charleszq.ups.utils.IConstants;

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
	 * <ul>
	 * <li>IPhotoService.class, return a service to return photos;</li>
	 * <li>Integer.class, return the page size, default to that defined in {@link IConstants};</li>
	 * <li>AbstractFetchIconUrlTask.class, return the task to get the url of this command.</li>
	 * <li>Boolean.class, return <code>false</code> to say don't show navigation menu items;</li>
	 * <li>Context.class, return <code>mContext</code>.
	 * </ul>
	 * @param adapterClass
	 * @return
	 */
	Object getAdapter(Class<?> adapterClass);

	/**
	 * 
	 * @param listener
	 */
	void addCommndDoneListener(ICommandDoneListener<T> listener);

	/**
	 * 
	 * @param mCommandDoneListener
	 */
	void removeCommandDoneListener(ICommandDoneListener<T> mCommandDoneListener);

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
	
	void setCommandCategory(String category);
	String getCommandCategory();

}
