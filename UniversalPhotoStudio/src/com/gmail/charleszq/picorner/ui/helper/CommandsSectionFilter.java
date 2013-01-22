/**
 * 
 */
package com.gmail.charleszq.picorner.ui.helper;

import java.util.ArrayList;
import java.util.List;

import android.widget.Filter;

import com.gmail.charleszq.picorner.ui.command.ICommand;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class CommandsSectionFilter extends Filter {

	private AbstractCommandSectionListAdapter mAdapter;
	private String mFilterStrig;

	public CommandsSectionFilter(String filterString) {
		this.mFilterStrig = filterString;
	}

	/**
	 * 
	 * @param adapter
	 * @param cancel
	 *            <code>true</code> to apply this filter; <code>false</code> to
	 *            cancel this filter.
	 */
	public final void doFilter(AbstractCommandSectionListAdapter adapter, boolean yes, ICommand<?> cmd) {
		if (mFilterStrig == null) {
			return;
		}

		this.mAdapter = adapter;
		if (yes)
			filter(mFilterStrig);
		else {
			cancelFilter(cmd);
		}
	}

	private void cancelFilter(ICommand<?> cmd) {
		//find the position of the given 'cmd'
		int index = mAdapter.mCommands.indexOf(cmd);
		if( index == -1 ) {
			return; //should not happend
		}
		
		index ++;
		for( ICommand<?> c : mAdapter.mAllCommands ) {
			if( mFilterStrig.equals( c.getCommandCategory() )) {
				if( mAdapter.mCommands.contains(c)) {
					//don't add that if that command is there already, this is because
					//the it matches the text filter constraint.
					continue;
				}
				mAdapter.mCommands.add(index++, c);
			}
		}
		mAdapter.notifyDataSetChanged();
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Filter#performFiltering(java.lang.CharSequence)
	 */
	@Override
	protected FilterResults performFiltering(CharSequence constraint) {
		List<ICommand<?>> currentCommands = mAdapter.mCommands;
		List<ICommand<?>> toremove = new ArrayList<ICommand<?>>();
		for (ICommand<?> cmd : currentCommands) {
			if (constraint.equals(cmd.getCommandCategory())) {
				toremove.add(cmd);
			}
		}
		currentCommands.removeAll(toremove);
		FilterResults result = new FilterResults();
		result.count = currentCommands.size();
		result.values = currentCommands;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Filter#publishResults(java.lang.CharSequence,
	 * android.widget.Filter.FilterResults)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void publishResults(CharSequence constraint, FilterResults results) {
		mAdapter.publishFilterResult(constraint, results.count,
				(List<ICommand<?>>) results.values);

	}

}
