/**
 * 
 */
package com.gmail.charleszq.picorner.ui;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.model.Author;
import com.gmail.charleszq.picorner.model.MediaObject;
import com.gmail.charleszq.picorner.model.MediaObjectComment;
import com.gmail.charleszq.picorner.model.MediaSourceType;
import com.gmail.charleszq.picorner.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.picorner.task.flickr.FetchFlickrUserIconUrlTask;
import com.gmail.charleszq.picorner.task.flickr.FlickrAddPhotoCommentTask;
import com.gmail.charleszq.picorner.task.flickr.FlickrLoadCommentsTask;
import com.gmail.charleszq.picorner.task.ig.InstagramAddPhotoCommentTask;
import com.gmail.charleszq.picorner.task.ig.InstagramLoadCommentsTask;
import com.gmail.charleszq.picorner.task.px500.PxFetchPhotoCommentsTask;
import com.gmail.charleszq.picorner.utils.IConstants;
import com.gmail.charleszq.picorner.utils.ModelUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class PhotoDetailCommentsFragment extends
		AbstractFragmentWithImageFetcher implements OnItemClickListener {

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
		mCommentListView.setOnItemClickListener(this);
		loadComments();

		mSendComment = (EditText) view.findViewById(R.id.edit_comment);
		mSendComment.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEND) {
					CharSequence commentText = v.getText();
					Log.e(getClass().getName(), "Comment: " + commentText); //$NON-NLS-1$
					sendComment(commentText);
				}
				return false;
			}
		});
		mSendComment
				.setVisibility(isUserLoggedIn()
						&& MediaSourceType.FLICKR.equals(mCurrentPhoto
								.getMediaSource()) ? View.VISIBLE : View.GONE);

		return view;
	}

	/**
	 * After comment sent, make a dummy comment and append to the end of the
	 * current comment list.
	 * 
	 * @param comment
	 * @return
	 */
	private MediaObjectComment makeDummyComment(String comment) {
		PicornerApplication app = (PicornerApplication) getActivity()
				.getApplication();

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
		PicornerApplication app = (PicornerApplication) getActivity()
				.getApplication();
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
			FlickrLoadCommentsTask flickrTask = new FlickrLoadCommentsTask(
					getActivity());
			flickrTask.addTaskDoneListener(lis);
			flickrTask.execute(photoId);
			break;
		case INSTAGRAM:
			InstagramLoadCommentsTask igTask = new InstagramLoadCommentsTask(
					getActivity());
			igTask.addTaskDoneListener(lis);
			igTask.execute(photoId);
			break;
		case PX500:
			PxFetchPhotoCommentsTask pxTask = new PxFetchPhotoCommentsTask();
			pxTask.addTaskDoneListener(lis);
			pxTask.execute(photoId);
			break;
		}

	}

	@SuppressLint("SimpleDateFormat")
	private static class CommentListAdapter extends BaseAdapter {

		private List<MediaObjectComment> mComments;
		private Context mContext;
		private ImageLoader mFetcher;
		private MediaObject mPhoto;
		private DisplayImageOptions mImageDisplayOptions;

		CommentListAdapter(Context context, MediaObject photo,
				List<MediaObjectComment> comments, ImageLoader fetcher) {
			mContext = context;
			mComments = comments;
			mFetcher = fetcher;
			mPhoto = photo;

			mImageDisplayOptions = new DisplayImageOptions.Builder()
					.showStubImage(R.drawable.empty_photo)
					.showImageForEmptyUri(R.drawable.empty_photo)
					.cacheInMemory().cacheOnDisc()
					.imageScaleType(ImageScaleType.EXACTLY)
					.bitmapConfig(Bitmap.Config.RGB_565).build();
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
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = LayoutInflater.from(mContext).inflate(
					R.layout.photo_detali_comment_item, null);

			ImageView avatorImage = (ImageView) v
					.findViewById(R.id.detail_author_avator);
			TextView txtCreateTime = (TextView) v
					.findViewById(R.id.detail_comment_time);
			TextView txtCommentText = (TextView) v
					.findViewById(R.id.detail_comment_text);
			TextView txtAuthorName = (TextView) v
					.findViewById(R.id.detail_author_name);
			MediaObjectComment comment = (MediaObjectComment) getItem(position);
			txtCreateTime.setText(comment.getCreateTimeString());
			ModelUtils.formatHtmlString(comment.getText(), txtCommentText);

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
				mFetcher.displayImage(buddyIcon, image, mImageDisplayOptions);
				break;
			case FLICKR:
				String url = comment.getAuthor().getBuddyIconUrl();
				if (url != null) {
					mFetcher.displayImage(url, image, mImageDisplayOptions);
				} else {
					FetchFlickrUserIconUrlTask task = new FetchFlickrUserIconUrlTask(
							mContext, comment.getAuthor().getUserId());
					task.execute(mFetcher, image);
				}
				break;
			case PX500:
				mFetcher.displayImage(comment.getAuthor().getBuddyIconUrl(),
						image, mImageDisplayOptions);
				break;
			}
		}

		void populateComments(List<MediaObjectComment> comments) {
			mComments.clear();
			mComments.addAll(comments);
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		MediaObjectComment comment = (MediaObjectComment) parent.getAdapter()
				.getItem(position);
		PhotoDetailActivity act = (PhotoDetailActivity) getActivity();
		act.showUserPhotos(comment.getAuthor());
	}
}
