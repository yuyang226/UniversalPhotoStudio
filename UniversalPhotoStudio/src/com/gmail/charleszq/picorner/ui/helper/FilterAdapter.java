/**
 * 
 */
package com.gmail.charleszq.picorner.ui.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.task.AbstractFetchIconUrlTask;
import com.gmail.charleszq.picorner.ui.command.ICommand;

/**
 * Represents the adapter which has the ability to filter.
 * 
 * @author charles(charleszq@gmail.com)
 * 
 */
public abstract class FilterAdapter extends BaseAdapter {

	List<Object> mData = new ArrayList<Object>();
	List<Object> mFilteredData = new ArrayList<Object>();
	private Context mContext;

	protected ICommand<?> mCommand;

	/**
	 * 
	 */
	public FilterAdapter(Context ctx, ICommand<?> command) {
		this.mContext = ctx;
		this.mCommand = command;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return mFilteredData.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return mFilteredData.get(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			v = LayoutInflater.from(mContext).inflate(
					R.layout.friend_list_item, null);
		}
		ViewHolder holder = (ViewHolder) v.getTag();
		ImageView avatar;
		TextView text;
		if (holder != null) {
			text = holder.text;
			avatar = holder.image;
		} else {
			avatar = (ImageView) v.findViewById(R.id.img_friend_avatar);
			text = (TextView) v.findViewById(R.id.txt_friend_name);
			holder = new ViewHolder();
			holder.image = avatar;
			holder.text = text;
			v.setTag(holder);
		}
		Object data = getItem(position);
		text.setText(getTitle(data));
		AbstractFetchIconUrlTask task = (AbstractFetchIconUrlTask) mCommand
				.getAdapter(AbstractFetchIconUrlTask.class);
		if (task != null)
			// this task is special
			task.execute(data, avatar);
		return v;
	}

	public void populateData(Collection<?> data) {
		mData.clear();
		mData.addAll(data);
		mFilteredData.clear();
		mFilteredData.addAll(data);
		notifyDataSetChanged();
	}

	void publishFilterResult(Object results) {
		List<?> filtered = (List<?>) results;
		mFilteredData.clear();
		for (Object obj : filtered) {
			mFilteredData.add(obj);
		}
		notifyDataSetChanged();
	}

	private class ViewHolder {
		TextView text;
		ImageView image;
	}

	abstract public String getTitle(Object item);

}
