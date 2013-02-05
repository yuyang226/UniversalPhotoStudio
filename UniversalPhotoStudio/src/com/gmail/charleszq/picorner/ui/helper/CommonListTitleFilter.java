/**
 * 
 */
package com.gmail.charleszq.picorner.ui.helper;

import java.util.ArrayList;
import java.util.List;

import android.widget.Filter;

/**
 * @author charles(charleszq@gmail.com)
 *
 */
public class CommonListTitleFilter extends Filter {
	
	private FilterAdapter	mAdapter;

	public CommonListTitleFilter(FilterAdapter adapter) {
		this.mAdapter = adapter;
	}

	/* (non-Javadoc)
	 * @see android.widget.Filter#performFiltering(java.lang.CharSequence)
	 */
	@Override
	protected FilterResults performFiltering(CharSequence constraint) {
		String query = constraint.toString().toLowerCase();
		List<Object> filteredIn = new ArrayList<Object>();
		List<?> all = mAdapter.mData;
		for( Object a : all ) {
			if( mAdapter.getTitle(a).toLowerCase().contains(query)) {
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
