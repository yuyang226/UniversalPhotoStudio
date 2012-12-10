/**
 * 
 */
package com.gmail.charleszq.ups.task;

/**
 * General task means the task that is not going to fetch some photo list.
 * 
 * @author Charles(charleszq@gmail.com)
 *
 */
public interface IGeneralTaskDoneListener<T> {

	/**
	 * 
	 * @param result
	 */
	void onTaskDone( T result );
}
