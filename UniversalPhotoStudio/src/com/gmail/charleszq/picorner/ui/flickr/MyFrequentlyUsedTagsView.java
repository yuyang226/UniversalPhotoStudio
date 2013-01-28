/**
 * 
 */
package com.gmail.charleszq.picorner.ui.flickr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.model.FlickrTagSearchParameter;
import com.gmail.charleszq.picorner.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.picorner.task.flickr.MyFrequentlyUsedTagsTask;
import com.gmail.charleszq.picorner.ui.command.ICommand;
import com.gmail.charleszq.picorner.ui.helper.AbstractHiddenView;
import com.googlecode.flickrjandroid.tags.Tag;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class MyFrequentlyUsedTagsView extends AbstractHiddenView implements
		OnItemClickListener {

	private ListView		mListView;
	private Button			mCancelButton, mSearchButton;
	private View			mButtonContainer;
	private TagListAdapter	mAdapter;
	private Set<String>		mSelectedTags		= new HashSet<String>();

	private OnClickListener	mOnClickListener	= new OnClickListener() {

													@Override
													public void onClick(View v) {
														if (v == mCancelButton) {
															onAction(ACTION_CANCEL);
														} else if (v == mSearchButton) {
															doSearch(v
																	.getContext());
														}
													}
												};

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gmail.charleszq.picorner.ui.helper.IHiddenView#getView(android.content
	 * .Context)
	 */
	@Override
	public View getView(Context ctx) {
		if (mView == null) {
			mView = LayoutInflater.from(ctx).inflate(R.layout.tags_list, null);
		}
		return mView;
	}

	protected void doSearch(Context context) {
		if (mSelectedTags.isEmpty()) {
			Toast.makeText(context,
					context.getString(R.string.msg_flickr_my_tag_search),
					Toast.LENGTH_SHORT).show();
			return;
		}
		FlickrTagSearchParameter param = new FlickrTagSearchParameter();
		StringBuilder sb = new StringBuilder();
		for( String tag : mSelectedTags ) {
			sb.append( tag );
			sb.append( " "); //$NON-NLS-1$
		}
		param.setTags(sb.toString().trim());
		onAction(ACTION_DO, param);
		
	}

	@Override
	public void init(ICommand<?> command, IHiddenViewActionListener listener) {
		super.init(command, listener);
		Context ctx = (Context) command.getAdapter(Context.class);
		mView = getView(ctx);

		// list view.
		mListView = (ListView) mView.findViewById(R.id.list_f_tags);
		View emptyView = mView.findViewById(R.id.empty_friend_view);
		mListView.setEmptyView(emptyView);
		mAdapter = new TagListAdapter(ctx, mSelectedTags);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);

		// buttons
		mButtonContainer = mView.findViewById(R.id.btn_container);
		mButtonContainer.setVisibility(View.INVISIBLE);
		mCancelButton = (Button) mView.findViewById(R.id.btn_cancel_search);
		mSearchButton = (Button) mView.findViewById(R.id.btn_search);
		mCancelButton.setOnClickListener(mOnClickListener);
		mSearchButton.setOnClickListener(mOnClickListener);

		// load tags
		loadTags(ctx);
	}

	private void loadTags(Context ctx) {
		MyFrequentlyUsedTagsTask task = new MyFrequentlyUsedTagsTask(ctx);
		task.addTaskDoneListener(new IGeneralTaskDoneListener<Collection<Tag>>() {

			@Override
			public void onTaskDone(Collection<Tag> result) {
				if (result == null) {
					onAction(ACTION_CANCEL);
					return;
				}
				mAdapter.populateTags(result);
				mButtonContainer.setVisibility(View.VISIBLE);
			}
		});
		task.execute();
	}

	private static class TagListAdapter extends BaseAdapter {

		private List<Tag>	mTags	= new ArrayList<Tag>();
		private Context		mContext;
		private Set<String>	mSelectedTags;

		TagListAdapter(Context context, Set<String> tags) {
			this.mContext = context;
			mSelectedTags = tags;
		}

		@Override
		public int getCount() {
			return mTags.size();
		}

		@Override
		public Object getItem(int position) {
			return mTags.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = LayoutInflater.from(mContext).inflate(
					R.layout.org_my_flickr_photo_list_item, null);
			ImageView image = (ImageView) v.findViewById(R.id.photo_pool_icon);
			image.setImageResource(R.drawable.ic_action_tags);
			CheckedTextView check = (CheckedTextView) v
					.findViewById(android.R.id.text1);
			check.setTextColor(mContext.getResources().getColor(
					R.color.comment_bg_color));
			Tag t = (Tag) getItem(position);
			check.setText(t.getValue());
			check.setChecked(mSelectedTags.contains(t.getValue()));
			return v;
		}

		void populateTags(Collection<Tag> tags) {
			mTags.clear();
			mTags.addAll(tags);
			Collections.sort(mTags, new Comparator<Tag>() {
				@Override
				public int compare(Tag lhs, Tag rhs) {
					return rhs.getCount() - lhs.getCount();
				}
			});
			notifyDataSetChanged();
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Tag t = (Tag) mAdapter.getItem(position);
		if (mSelectedTags.contains(t.getValue())) {
			mSelectedTags.remove(t.getValue());
		} else {
			mSelectedTags.add(t.getValue());
		}
		mAdapter.notifyDataSetChanged();
	}

}
