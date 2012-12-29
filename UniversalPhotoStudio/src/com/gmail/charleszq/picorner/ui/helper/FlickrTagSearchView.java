/**
 * 
 */
package com.gmail.charleszq.picorner.ui.helper;

import android.app.Service;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.ui.command.ICommand;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class FlickrTagSearchView extends LinearLayout implements IHiddenView {

	private Button mCancelButton;
	private Button mSearchButton;
	private EditText mTagText;
	private ICommand<?> mCommand;
	private IHiddenViewActionListener mHideViewCancelListener;

	/**
	 * @param context
	 */
	public FlickrTagSearchView(Context context) {
		super(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public FlickrTagSearchView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public FlickrTagSearchView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void init(ICommand<?> command, IHiddenViewActionListener listener) {
		this.mCommand = command;
		this.mHideViewCancelListener = listener;

		mTagText = (EditText) findViewById(R.id.txt_flickr_tag_search);

		mCancelButton = (Button) findViewById(R.id.btn_cancel_search);
		mCancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onAction(ACTION_CANCEL);
			}
		});

		mSearchButton = (Button) findViewById(R.id.btn_search);
		mSearchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String s = mTagText.getText().toString();
				if (s == null || s.trim().length() == 0) {
					Toast.makeText(
							getContext(),
							getContext().getString(
									R.string.msg_flickr_tag_search_empty_tag),
							Toast.LENGTH_LONG).show();
					return;
				}
				
				//hide the soft keyboard
				InputMethodManager imm = (InputMethodManager) getContext()
						.getSystemService(Service.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mTagText.getWindowToken(), 0);
				
				onAction(ACTION_DO, s);

			}
		});

	}

	@Override
	public void onAction(int action, Object... data) {
		mHideViewCancelListener.onAction(action, mCommand, this, data);
	}

}
