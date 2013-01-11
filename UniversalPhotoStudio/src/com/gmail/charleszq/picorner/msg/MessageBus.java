/**
 * 
 */
package com.gmail.charleszq.picorner.msg;

import java.util.HashSet;
import java.util.Set;

/**
 * @author charleszq
 * 
 */
public final class MessageBus {

	private static Set<IMessageConsumer> mConsumers = new HashSet<IMessageConsumer>();

	public static void addConsumer(IMessageConsumer consumer) {
		mConsumers.add(consumer);
	}

	public static void removeConsumer(IMessageConsumer consumer) {
		mConsumers.remove(consumer);
	}
	
	public static void reset() {
		mConsumers.clear();
	}

	public static void broadcastMessage(Message msg) {
		for (IMessageConsumer c : mConsumers) {
			boolean ret = c.consumeMessage(msg);
			if( ret ) {
				break;
			}
		}
	}

	/**
	 * 
	 */
	private MessageBus() {
	}


}
