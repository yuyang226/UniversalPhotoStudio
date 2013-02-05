/**
 * 
 */
package com.gmail.charleszq.picorner.ui.helper;

import android.content.Context;

import com.gmail.charleszq.picorner.model.Author;
import com.gmail.charleszq.picorner.ui.command.ICommand;

/**
 * Represents the adapter for the friend list.
 * 
 * @author charleszq
 * 
 */
public class FriendListAdapter extends FilterAdapter {

	public FriendListAdapter(Context ctx, ICommand<?> command) {
		super(ctx, command);
	}

	@Override
	public String getTitle(Object item) {
		Author a = (Author) item;
		return a.getUserName();
	}

}
