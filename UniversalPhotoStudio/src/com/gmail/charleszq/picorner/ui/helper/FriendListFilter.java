/**
 * 
 */
package com.gmail.charleszq.picorner.ui.helper;

import java.util.ArrayList;
import java.util.List;

import android.widget.Filter;

import com.gmail.charleszq.picorner.model.Author;

/**
 * @author charles(charleszq@gmail.com)
 *
 */
public class FriendListFilter extends Filter {
	
	private FriendListAdapter	mAdapter;

	public FriendListFilter(FriendListAdapter adapter) {
		this.mAdapter = adapter;
	}

	/* (non-Javadoc)
	 * @see android.widget.Filter#performFiltering(java.lang.CharSequence)
	 */
	@Override
	protected FilterResults performFiltering(CharSequence constraint) {
		String query = constraint.toString().toLowerCase();
		List<Author> filteredIn = new ArrayList<Author>();
		List<Author> all = mAdapter.mFriends;
		for( Author a : all ) {
			if( a.getUserName().toLowerCase().contains(query)) {
				filteredIn.add(a);
			}
		}
		FilterResults result = new FilterResults();
		result.count = filteredIn.size();
		result.values = filteredIn;
		return result;
	}

	/* (non-Javadoc)
	 * @see android.widget.Filter#publishResults(java.lang.CharSequence, android.widget.Filter.FilterResults)
	 */
	@Override
	protected void publishResults(CharSequence constraint, FilterResults results) {
		mAdapter.publishFilterResult(results.values);
	}

}
