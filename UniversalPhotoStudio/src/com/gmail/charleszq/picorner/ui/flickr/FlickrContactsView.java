/**
 * 
 */
package com.gmail.charleszq.picorner.ui.flickr;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.ui.command.ICommand;
import com.gmail.charleszq.picorner.ui.helper.AbstractLinearLayoutHiddenView;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class FlickrContactsView extends AbstractLinearLayoutHiddenView {

	private ListView	mListView;
	private Button		mCancelButton;

	/**
	 * @param context
	 */
	public FlickrContactsView(Context context) {
		super(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public FlickrContactsView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public FlickrContactsView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gmail.charleszq.picorner.ui.helper.IHiddenView#init(com.gmail.charleszq
	 * .picorner.ui.command.ICommand,
	 * com.gmail.charleszq.picorner.ui.helper.IHiddenView
	 * .IHiddenViewActionListener)
	 */
	@Override
	public void init(ICommand<?> command, IHiddenViewActionListener listener) {
		super.init(command, listener);
		mListView = (ListView) findViewById(R.id.list_f_friends);
		mCancelButton = (Button) findViewById(R.id.btn_cancel_friends);
		mCancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onAction(ACTION_CANCEL);
			}
		});
	}
}
