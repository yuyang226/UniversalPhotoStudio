/**
 * 
 */
package com.gmail.charleszq.ups.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.gmail.charleszq.ups.R;
import com.gmail.charleszq.ups.UPSApplication;
import com.gmail.charleszq.ups.model.Author;
import com.gmail.charleszq.ups.model.MediaObject;
import com.gmail.charleszq.ups.model.MediaObjectComment;
import com.gmail.charleszq.ups.model.MediaSourceType;
import com.gmail.charleszq.ups.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.ups.task.flickr.FetchFlickrUserIconUrlTask;
import com.gmail.charleszq.ups.task.flickr.FlickrAddPhotoCommentTask;
import com.gmail.charleszq.ups.task.flickr.FlickrLoadCommentsTask;
import com.gmail.charleszq.ups.task.ig.InstagramAddPhotoCommentTask;
import com.gmail.charleszq.ups.task.ig.InstagramLoadCommentsTask;
import com.gmail.charleszq.ups.utils.IConstants;
import com.gmail.charleszq.ups.utils.ImageFetcher;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class PhotoDetailCommentsFragment extends
		AbstractFragmentWithImageFetcher {

	private MediaObject mCurrentPhoto;

	private ProgressBar mProgressBar;
	private TextView mNoCommentText;
	private ListView mCommentListView;
	private EditText mSendComment;
	private CommentListAdapter mAdapter;

	/**
	 * 
	 */
	public PhotoDetailCommentsFragment() {
	}

	public static PhotoDetailCommentsFragment newInstance(MediaObject photo) {
		PhotoDetailCommentsFragment f = new PhotoDetailCommentsFragment();
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

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.photo_detail_comment_frg, null);

		mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
		mNoCommentText = (TextView) view.findViewById(R.id.txt_no_comment);

		mCommentListView = (ListView) view.findViewById(R.id.photo_detail_list);
		mAdapter = new CommentListAdapter(getActivity(), mCurrentPhoto,
				mCurrentPhoto.getCommentList(), mImageFetcher);
		mCommentListView.setAdapter(mAdapter);
		loadComments();

		mSendComment = (EditText) view.findViewById(R.id.edit_comment);
		mSendComment.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEND) {
					CharSequence commentText = v.getText();
					logger.debug("Comment: " + commentText); //$NON-NLS-1$
					sendComment(commentText);
				}
				return false;
			}
		});
		mSendComment.setVisibility(isUserLoggedIn() ? View.VISIBLE : View.GONE);

		return view;
	}

	private MediaObjectComment makeDummyComment(String comment) {
		UPSApplication app = (UPSApplication) getActivity().getApplication();

		MediaObjectComment c = new MediaObjectComment();
		c.setText(comment);
		c.setCreationTime(System.currentTimeMillis());

		Author a = new Author();
		c.setAuthor(a);

		switch (mCurrentPhoto.getMediaSource()) {
		case FLICKR:
			a.setUserId(app.getFlickrUserId());
			a.setUserName(app.getFlickrUserName());
			break;
		case INSTAGRAM:
			a.setUserId(String.valueOf(app.getInstagramUserId()));
			a.setBuddyIconUrl(app.getInstagramUserBuddyIconUrl());
			break;
		}

		return c;
	}

	private void sendComment(final CharSequence commentText) {
		IGeneralTaskDoneListener<Boolean> lis = new IGeneralTaskDoneListener<Boolean>() {

			@Override
			public void onTaskDone(Boolean result) {
				if (result) {
					MediaObjectComment c = makeDummyComment(commentText
							.toString());
					mCurrentPhoto.getCommentList().add(c);
					mAdapter.notifyDataSetChanged();
					mProgressBar.setVisibility(View.INVISIBLE);
					mNoCommentText.setVisibility(View.INVISIBLE);
					Toast.makeText(
							getActivity(),
							getActivity().getString(R.string.msg_comment_added),
							Toast.LENGTH_SHORT).show();
					mSendComment.setText(""); //$NON-NLS-1$
				}
			}
		};

		switch (mCurrentPhoto.getMediaSource()) {
		case FLICKR:
			FlickrAddPhotoCommentTask ft = new FlickrAddPhotoCommentTask(
					getActivity());
			ft.addTaskDoneListener(lis);
			ft.execute(mCurrentPhoto.getId(), commentText.toString());
			break;
		case INSTAGRAM:
			InstagramAddPhotoCommentTask it = new InstagramAddPhotoCommentTask(
					getActivity());
			it.addTaskDoneListener(lis);
			it.execute(mCurrentPhoto.getId(), commentText.toString());
			break;
		}

	}

	private boolean isUserLoggedIn() {
		boolean result = false;
		UPSApplication app = (UPSApplication) getActivity().getApplication();
		switch (mCurrentPhoto.getMediaSource()) {
		case FLICKR:
			result = app.getFlickrUserId() != null;
			break;
		case INSTAGRAM:
			result = app.getInstagramUserId() != null;
			break;
		}
		return result;
	}

	private void loadComments() {

		this.mProgressBar.setVisibility(View.VISIBLE);
		this.mNoCommentText.setVisibility(View.INVISIBLE);

		IGeneralTaskDoneListener<List<MediaObjectComment>> lis = new IGeneralTaskDoneListener<List<MediaObjectComment>>() {

			@Override
			public void onTaskDone(List<MediaObjectComment> result) {
				PhotoDetailCommentsFragment.this.mProgressBar
						.setVisibility(View.INVISIBLE);
				if (result == null) {
					return;
				}

				if (result.isEmpty()) {
					mNoCommentText.setVisibility(View.VISIBLE);
					mCommentListView.setVisibility(View.INVISIBLE);
				}
				mAdapter.populateComments(result);
				mAdapter.notifyDataSetChanged();

			}

		};

		String photoId = mCurrentPhoto.getId();

		// load comments from the server side, and save them into the current
		// photo
		MediaSourceType type = mCurrentPhoto.getMediaSource();
		switch (type) {
		case FLICKR:
			FlickrLoadCommentsTask t = new FlickrLoadCommentsTask(getActivity());
			t.addTaskDoneListener(lis);
			t.execute(photoId);
			break;
		case INSTAGRAM:
			InstagramLoadCommentsTask task = new InstagramLoadCommentsTask(
					getActivity());
			task.addTaskDoneListener(lis);
			task.execute(photoId);
			break;
		}

	}

	@SuppressLint("SimpleDateFormat")
	private static class CommentListAdapter extends BaseAdapter {

		private List<MediaObjectComment> mComments;
		private Context mContext;
		private ImageFetcher mFetcher;
		private MediaObject mPhoto;

		CommentListAdapter(Context context, MediaObject photo,
				List<MediaObjectComment> comments, ImageFetcher fetcher) {
			mContext = context;
			mComments = comments;
			mFetcher = fetcher;
			mPhoto = photo;
		}

		@Override
		public int getCount() {
			return mComments.size();
		}

		@Override
		public Object getItem(int position) {
			return mComments.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public boolean isEnabled(int position) {
			return false;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				v = LayoutInflater.from(mContext).inflate(
						R.layout.photo_detali_comment_item, null);
			}

			ImageView avatorImage = (ImageView) v
					.findViewById(R.id.detail_author_avator);
			TextView txtCreateTime = (TextView) v
					.findViewById(R.id.detail_comment_time);
			TextView txtCommentText = (TextView) v
					.findViewById(R.id.detail_comment_text);
			TextView txtAuthorName = (TextView) v
					.findViewById(R.id.detail_author_name);
			MediaObjectComment comment = (MediaObjectComment) getItem(position);
			SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm"); //$NON-NLS-1$
			txtCreateTime.setText(format.format(new Date(comment
					.getCreationTime())));
			txtCommentText.setText(comment.getText());

			String userName = comment.getAuthor().getUserName();
			if (userName == null) {
				userName = comment.getAuthor().getUserId();
			}
			txtAuthorName.setText(userName);

			loadAvator(mPhoto, comment, avatorImage);
			return v;
		}

		private void loadAvator(MediaObject photo, MediaObjectComment comment,
				ImageView image) {
			MediaSourceType type = photo.getMediaSource();
			switch (type) {
			case INSTAGRAM:
				String buddyIcon = comment.getAuthor().getBuddyIconUrl();
				mFetcher.loadImage(buddyIcon, image);
				break;
			case FLICKR:
				String url = comment.getAuthor().getBuddyIconUrl();
				if (url != null) {
					mFetcher.loadImage(url, image);
				} else {
					FetchFlickrUserIconUrlTask task = new FetchFlickrUserIconUrlTask(
							mContext, comment.getAuthor().getUserId());
					task.execute(mFetcher, image);
				}
				break;
			}
		}

		void populateComments(List<MediaObjectComment> comments) {
			mComments.clear();
			mComments.addAll(comments);
		}

	}
}
