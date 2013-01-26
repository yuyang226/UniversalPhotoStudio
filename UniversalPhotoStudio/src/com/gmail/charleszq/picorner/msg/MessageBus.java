/**
 * 
 */
package com.gmail.charleszq.picorner.msg;

import java.util.HashSet;
import java.util.Set;

import android.util.Log;

import com.gmail.charleszq.picorner.BuildConfig;
import com.gmail.charleszq.picorner.ui.SecondaryMenuFragment;

/**
 * @author charleszq
 * 
 */
public final class MessageBus {
	
	private static final String TAG = MessageBus.class.getSimpleName();

	private static Set<IMessageConsumer> mConsumers = new HashSet<IMessageConsumer>();

	public static void addConsumer(IMessageConsumer consumer) {
		mConsumers.add(consumer);
		if( BuildConfig.DEBUG )
			Log.d(TAG, "message consumer registered: " +  consumer.toString()); //$NON-NLS-1$
	}

	public static void removeConsumer(IMessageConsumer consumer) {
		mConsumers.remove(consumer);
		if( BuildConfig.DEBUG )
			Log.d(TAG, "message consumer removed: " +  consumer.toString()); //$NON-NLS-1$
	}
	
	/**
	 * Resets the consumers, only left the 2nd menu fragment.
	 */
	public static void reset() {
		IMessageConsumer consumer = null;
		for( IMessageConsumer c : mConsumers ) {
			if( SecondaryMenuFragment.class.isInstance(c)) {
				consumer = c;
				break;
			}
		}
		mConsumers.clear();
		if( consumer != null ) {
			mConsumers.add(consumer);
		}
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
