/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command;

/**
 * @author Charles(charleszq@gmail.com)
 *
 */
public interface ICommandDoneListener<T> {

	void onCommandDone(ICommand<T> command, T t);
}
