/**
 * 
 */
package com.gmail.charleszq.picorner.ui.px500;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.ui.command.ICommand;
import com.gmail.charleszq.picorner.ui.helper.AbstractHiddenView;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class Px500SearchView extends AbstractHiddenView {

	private Button mCancelButton;
	private Button mSearchButton;
	private EditText mTermEdit, mTagEdit;

	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			InputMethodManager imm = (InputMethodManager) v.getContext()
					.getSystemService(Service.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mTermEdit.getWindowToken(), 0);
			imm.hideSoftInputFromWindow(mTagEdit.getWindowToken(), 0);

			if (v == mCancelButton) {
				onAction(ACTION_CANCEL);
			} else if (v == mSearchButton) {
				doSearch(v.getContext());
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
			mView = LayoutInflater.from(ctx).inflate(
					R.layout.px500_search_view, null);
		}
		return mView;
	}

	private void doSearch(Context context) {
		
		String term = mTermEdit.getText().toString();
		if( term != null ) {
			term = term.trim().length() == 0 ? null : term.trim();
		}
		String tag = mTagEdit.getText().toString();
		if( tag != null ) {
			tag = tag.trim().length() == 0 ? null : tag.trim();
		}
		
		if( term == null && tag == null ) {
			Toast.makeText(context, context.getString(R.string.msg_pls_input_term_and_tag)
					, Toast.LENGTH_SHORT).show();
			return;
		}
		
		onAction(ACTION_DO, term, tag);
	}

	@Override
	public void init(ICommand<?> command, IHiddenViewActionListener listener) {
		super.init(command, listener);
		Context ctx = (Context) command.getAdapter(Context.class);
		if (mView == null) {
			getView(ctx);
		}

		mCancelButton = (Button) mView.findViewById(R.id.btn_cancel_search);
		mCancelButton.setOnClickListener(mOnClickListener);

		mSearchButton = (Button) mView.findViewById(R.id.btn_search);
		mSearchButton.setOnClickListener(mOnClickListener);
		
		mTermEdit = (EditText) mView.findViewById(R.id.txt_500px_term);
		mTagEdit = (EditText) mView.findViewById(R.id.txt_500px_tag);
	}

}
