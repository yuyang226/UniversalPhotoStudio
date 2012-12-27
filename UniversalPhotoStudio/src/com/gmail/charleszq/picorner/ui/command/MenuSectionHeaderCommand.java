/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command;

import android.content.Context;

import com.gmail.charleszq.picorner.ui.helper.CommandSectionListAdapter;
import com.gmail.charleszq.picorner.ui.helper.CommandsSectionFilter;

/**
 * @author Charles(charleszq@gmail.com)
 * 
 */
public class MenuSectionHeaderCommand extends AbstractCommand<String> {

	private String mLabel;
	private boolean mFiltering = false;

	/**
	 * @param context
	 */
	public MenuSectionHeaderCommand(Context context, String label) {
		super(context);
		this.mLabel = label;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ups.ui.command.ICommand#execute()
	 */
	@Override
	public boolean execute(Object... objects) {
		if (objects.length != 1) {
			return false;
		}

		Object obj = objects[0];
		if (obj instanceof CommandSectionListAdapter) {
			CommandsSectionFilter filter = new CommandsSectionFilter(mLabel);
			filter.doFilter((CommandSectionListAdapter) obj, !mFiltering, this);
			mFiltering = !mFiltering;
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ups.ui.command.ICommand#getIconResourceId()
	 */
	@Override
	public int getIconResourceId() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ups.ui.command.ICommand#getLabel()
	 */
	@Override
	public String getLabel() {
		return mLabel;
	}

	@Override
	public CommandType getCommandType() {
		return CommandType.MENU_HEADER_CMD;
	}
	
	public boolean isFiltering() {
		return mFiltering;
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if( adapterClass == Boolean.class ) {
			return mFiltering;
		}
		return super.getAdapter(adapterClass);
	}
	
	

}
