package com.gmail.charleszq.picorner.ui.flickr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.picorner.task.flickr.FetchGroupInfoTask;
import com.gmail.charleszq.picorner.task.flickr.JoinGroupTask;
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
				DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == DialogInterface.BUTTON_NEGATIVE)
							dialog.cancel();
						else {
							finish();
							JoinGroupTask task = new JoinGroupTask(
									FlickrGroupInfoDialog.this);
							task.addTaskDoneListener(new IGeneralTaskDoneListener<String>() {
								@Override
								public void onTaskDone(String result) {
									Toast.makeText(FlickrGroupInfoDialog.this,
											result, Toast.LENGTH_LONG).show();
								}
							});
							task.execute(mGroupId);
						}
					}
				};
				AlertDialog dialog = new AlertDialog.Builder(
						FlickrGroupInfoDialog.this)
						.setTitle(R.string.button_join_flickr_group)
						.setMessage(R.string.msg_join_group_condition)
						.setNegativeButton(android.R.string.no, listener)
						.setPositiveButton(android.R.string.yes, listener)
						.create();
				dialog.show();
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
			if (mDescription != null && result.getDescription() != null && result.getDescription().trim().length() > 0) {
				ModelUtils.formatHtmlString(result.getDescription(),
						mDescription);
			}

			if (mRules != null && result.getRules() != null && result.getRules().trim().length() > 0 ) {
				ModelUtils.formatHtmlString(result.getRules(), mRules);
			}
		}
		this.setProgressBarIndeterminate(false);
	}

}
