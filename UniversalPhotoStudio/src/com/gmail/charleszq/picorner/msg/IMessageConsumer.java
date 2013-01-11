/**
 * 
 */
package com.gmail.charleszq.picorner.msg;

/**
 * @author charleszq
 * 
 */
public interface IMessageConsumer {

	/**
	 * Consumes the message,
	 * 
	 * @param msg
	 * @return <code>true</code> if consumed, <code>false</code> otherwise. 
	 */
	boolean consumeMessage(Message msg);
}
