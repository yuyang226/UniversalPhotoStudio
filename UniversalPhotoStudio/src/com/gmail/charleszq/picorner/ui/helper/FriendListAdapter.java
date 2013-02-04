/**
 * 
 */
package com.gmail.charleszq.picorner.ui.helper;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.model.Author;
import com.gmail.charleszq.picorner.task.AbstractFetchIconUrlTask;
import com.gmail.charleszq.picorner.ui.command.ICommand;

/**
 * Represents the adapter for the friend list.
 * 
 * @author charleszq
 * 
 */
public class FriendListAdapter extends BaseAdapter {

	List<Author> mFriends = new ArrayList<Author>();
	List<Author> mFilteredOutFriends = new ArrayList<Author>();
	private Context mContext;

	/**
	 * From it, we can know how to get the avator of this friend.
	 */
	private ICommand<?> mCommand;

	public FriendListAdapter(Context ctx, ICommand<?> command) {
		this.mContext = ctx;
		this.mCommand = command;
	}

	@Override
	public int getCount() {
		return mFilteredOutFriends.size();
	}

	@Override
	public Object getItem(int position) {
		return mFilteredOutFriends.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

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
		Author a = (Author) getItem(position);
		text.setText(a.getUserName());
		AbstractFetchIconUrlTask task = (AbstractFetchIconUrlTask) mCommand
				.getAdapter(AbstractFetchIconUrlTask.class);
		// this task is special
		task.execute(a, avatar);
		return v;
	}

	public void populateFriends(List<Author> friends) {
		mFriends.clear();
		mFriends.addAll(friends);
		mFilteredOutFriends.clear();
		mFilteredOutFriends.addAll(friends);
		notifyDataSetChanged();
	}

	void publishFilterResult(Object results) {
		@SuppressWarnings("unchecked")
		List<Author> filtered = (List<Author>) results;
		mFilteredOutFriends.clear();
		mFilteredOutFriends.addAll(filtered);
		notifyDataSetChanged();
	}

	private class ViewHolder {
		TextView text;
		ImageView image;
	}
}
