/**
 * 
 */
package com.gmail.charleszq.ups.ui.ig;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gmail.charleszq.ups.R;
import com.gmail.charleszq.ups.model.Author;
import com.gmail.charleszq.ups.model.MediaObject;
import com.gmail.charleszq.ups.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.ups.task.ig.InstagramLoadLikesTask;
import com.gmail.charleszq.ups.ui.AbstractFragmentWithImageFetcher;
import com.gmail.charleszq.ups.utils.IConstants;
import com.gmail.charleszq.ups.utils.ImageFetcher;

/**
 * Represents the fragment to show the users who like a photo.
 * 
 * @author charles(charleszq@gmail.com)
 * 
 */
public class PhotoDetailLikesFragment extends AbstractFragmentWithImageFetcher {

	private MediaObject mCurrentPhoto;
	private List<Author> mAuthors = new ArrayList<Author>();
	
	//UI controls
	private ProgressBar mProgressBar;
	private TextView mNoLikesText;

	/**
	 * 
	 */
	public PhotoDetailLikesFragment() {
	}

	public static PhotoDetailLikesFragment newInstance(MediaObject photo) {
		PhotoDetailLikesFragment f = new PhotoDetailLikesFragment();
		final Bundle bundle = new Bundle();
		bundle.putSerializable(IConstants.DETAIL_PAGE_PHOTO_ARG_KEY, photo);
		f.setArguments(bundle);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = this.getArguments();
		mCurrentPhoto = (MediaObject) bundle
				.getSerializable(IConstants.DETAIL_PAGE_PHOTO_ARG_KEY);

		int thumbSize = getResources().getDimensionPixelSize(
				R.dimen.cmd_icon_size);
		initializeImageFetcher(IConstants.BUDDY_ICON_DIR, thumbSize);
		this.setRetainInstance(true);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.ig_like_users, null);
		
		mProgressBar = (ProgressBar) v.findViewById(R.id.ig_like_progress_bar);
		mNoLikesText = (TextView) v.findViewById(R.id.ig_no_like_text);
		
		GridView grid = (GridView) v.findViewById(R.id.ig_like_grid);
		LikesListAdapter adapter = new LikesListAdapter(getActivity(), mAuthors,
				mImageFetcher);
		grid.setAdapter(adapter);
		loadComments(adapter);
		
		return v;
	}

	private void loadComments(final LikesListAdapter adapter) {

		IGeneralTaskDoneListener<List<Author>> lis = new IGeneralTaskDoneListener<List<Author>>() {

			@Override
			public void onTaskDone(List<Author> result) {
				mProgressBar.setVisibility(View.INVISIBLE);
				if (result == null || result.isEmpty()) {
					mNoLikesText.setVisibility(View.VISIBLE);
					return;
				}
				mAuthors.clear();
				mAuthors.addAll(result);
				adapter.notifyDataSetChanged();
			}

		};

		String photoId = mCurrentPhoto.getId();

		InstagramLoadLikesTask task = new InstagramLoadLikesTask(getActivity());
		task.addTaskDoneListener(lis);
		task.execute(photoId);

	}

	@SuppressLint("SimpleDateFormat")
	private static class LikesListAdapter extends BaseAdapter {

		private Context mContext;
		private ImageFetcher mFetcher;
		private List<Author> mLikeUsers;

		LikesListAdapter(Context context, List<Author> mAuthors, ImageFetcher fetcher) {
			mContext = context;
			mFetcher = fetcher;
			mLikeUsers = mAuthors;
		}

		@Override
		public int getCount() {
			return mLikeUsers.size();
		}

		@Override
		public Object getItem(int position) {
			return mLikeUsers.get(position);
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
						R.layout.photo_detail_ig_like_item, null);
			}

			ImageView avatorImage = (ImageView) v
					.findViewById(R.id.detail_ig_like_avator);
			TextView txtUserName = (TextView) v
					.findViewById(R.id.detail_ig_like_user_name);
			Author user = (Author) getItem(position);
			txtUserName.setText(user.getUserName() == null ? user
					.getBuddyIconUrl() : user.getUserName());

			mFetcher.loadImage(user.getBuddyIconUrl(), avatorImage);
			return v;
		}
	}
}
