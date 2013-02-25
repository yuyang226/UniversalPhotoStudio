package com.gmail.charleszq.picorner.ui.flickr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.picorner.task.flickr.FetchGroupInfoTask;
import com.gmail.charleszq.picorner.utils.ModelUtils;
import com.googlecode.flickrjandroid.groups.Group;

public class FlickrGroupInfoDialog extends Activity {

	public static final String F_GROUP_ID_KEY = "f.group.id"; //$NON-NLS-1$
	public static final String F_GROUP_TITLE_KEY = "f.group.title"; //$NON-NLS-1$
	public static final String F_GROUP_MY_GROUP_KEY = "f.group.my.group"; //$NON-NLS-1$

	private String mGroupId;
	private TextView mDescription, mRules;

	private OnClickListener mListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int tag = (Integer) v.getTag();
			switch (tag) {
			case R.id.btn_flickr_group_cancel:
				finish();
				break;
			case R.id.btn_join_flickr_group:
				break;
			}
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setProgressBarIndeterminateVisibility(true);
		setContentView(R.layout.flickr_group_info);

		Intent intent = getIntent();
		mGroupId = intent.getStringExtra(F_GROUP_ID_KEY);
		String title = intent.getStringExtra(F_GROUP_TITLE_KEY);
		boolean isMyGroup = intent.getBooleanExtra(F_GROUP_MY_GROUP_KEY, false);

		TextView titleView = (TextView) findViewById(R.id.flickr_group_title);
		titleView.setText(title);

		Button btnJoin = (Button) findViewById(R.id.btn_join_flickr_group);
		btnJoin.setTag(R.id.btn_join_flickr_group);
		btnJoin.setVisibility(isMyGroup ? View.GONE : View.VISIBLE);
		btnJoin.setOnClickListener(mListener);

		Button cancelBtn = (Button) findViewById(R.id.btn_flickr_group_cancel);
		cancelBtn.setTag(R.id.btn_flickr_group_cancel);
		cancelBtn.setOnClickListener(mListener);

		mDescription = (TextView) findViewById(R.id.flickr_group_desc);
		mRules = (TextView) findViewById(R.id.flickr_group_rules);
	}

	@Override
	protected void onResume() {
		super.onResume();
		FetchGroupInfoTask task = new FetchGroupInfoTask();
		task.addTaskDoneListener(new IGeneralTaskDoneListener<Group>() {

			@Override
			public void onTaskDone(Group result) {
				onGroupInfoFetch(result);
			}
		});
		this.setProgressBarIndeterminate(true);
		task.execute(mGroupId);

	}

	private void onGroupInfoFetch(Group result) {
		if (result != null) {
			if (mDescription != null && result.getDescription() != null) {
				ModelUtils.formatHtmlString(result.getDescription(),
						mDescription);
			}

			if (mRules != null && result.getRules() != null) {
				ModelUtils.formatHtmlString(result.getRules(), mRules);
			}
		}
		this.setProgressBarIndeterminate(false);
	}

}
