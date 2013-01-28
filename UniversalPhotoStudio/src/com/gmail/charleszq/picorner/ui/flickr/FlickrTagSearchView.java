/**
 * 
 */
package com.gmail.charleszq.picorner.ui.flickr;

import android.app.Service;
import android.content.Context;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.model.FlickrTagSearchParameter;
import com.gmail.charleszq.picorner.model.FlickrTagSearchParameter.FlickrTagSearchMode;
import com.gmail.charleszq.picorner.ui.command.ICommand;
import com.gmail.charleszq.picorner.ui.helper.AbstractLinearLayoutHiddenView;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class FlickrTagSearchView extends AbstractLinearLayoutHiddenView {

	private Button						mCancelButton;
	private Button						mSearchButton;
	private EditText					mTagText;
	private RadioButton					mRadioAnd, mRadioOr;
	private CheckBox					mCheckInCommon, mCheckHasGeo;

	private FlickrTagSearchParameter	mSearchParameter;

	private OnClickListener				mOnClickListener			= new OnClickListener() {

																		@Override
																		public void onClick(
																				View v) {
																			// hide
																			// the
																			// soft
																			// keyboard
																			InputMethodManager imm = (InputMethodManager)v.getContext()
																					.getSystemService(
																							Service.INPUT_METHOD_SERVICE);
																			imm.hideSoftInputFromWindow(
																					mTagText.getWindowToken(),
																					0);
																			if (v == mCancelButton) {
																				onAction(ACTION_CANCEL);
																			} else if (v == mSearchButton) {
																				doSearch(v.getContext());
																			} else if (v == mRadioAnd) {
																				mSearchParameter
																						.setSearchMode(FlickrTagSearchMode.ALL);
																			} else if (v == mRadioOr) {
																				mSearchParameter
																						.setSearchMode(FlickrTagSearchMode.ANY);
																			}
																		}
																	};

	private OnCheckedChangeListener		mOnCheckedChangeListener	= new OnCheckedChangeListener() {

																		@Override
																		public void onCheckedChanged(
																				CompoundButton buttonView,
																				boolean isChecked) {
																			if (buttonView == mCheckInCommon) {
																				mSearchParameter
																						.setSearchInCommon(isChecked);
																			} else if (buttonView == mCheckHasGeo) {
																				mSearchParameter
																						.setHasGeoInformation(isChecked);
																			}
																		}

																	};

	@Override
	public void init(ICommand<?> command, IHiddenViewActionListener listener) {
		super.init(command, listener);
		mSearchParameter = new FlickrTagSearchParameter();

		Context ctx = (Context) command.getAdapter(Context.class);
		if (mView == null) {
			getView(ctx);
		}
		mTagText = (EditText) mView.findViewById(R.id.txt_flickr_tag_search);
		mTagText.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					doSearch(v.getContext());
					return true;
				}
				return false;
			}
		});
		Editable tag = mTagText.getText();
		if (tag != null)
			mSearchParameter.setTags(tag.toString().trim());

		mCancelButton = (Button) mView.findViewById(R.id.btn_cancel_search);
		mCancelButton.setOnClickListener(mOnClickListener);

		mSearchButton = (Button) mView.findViewById(R.id.btn_search);
		mSearchButton.setOnClickListener(mOnClickListener);

		mRadioAnd = (RadioButton) mView.findViewById(R.id.radio_and);
		mRadioAnd.setOnClickListener(mOnClickListener);
		mRadioOr = (RadioButton) mView.findViewById(R.id.radio_or);
		mRadioOr.setOnClickListener(mOnClickListener);

		mCheckInCommon = (CheckBox) mView
				.findViewById(R.id.cb_f_search_in_common);
		mCheckHasGeo = (CheckBox) mView.findViewById(R.id.cb_f_search_has_geo);
		mCheckInCommon.setOnCheckedChangeListener(mOnCheckedChangeListener);
		mCheckHasGeo.setOnCheckedChangeListener(mOnCheckedChangeListener);

		// initialize the values for the search parameter
		mSearchParameter
				.setSearchMode(mRadioAnd.isChecked() ? FlickrTagSearchMode.ALL
						: FlickrTagSearchMode.ANY);
		mSearchParameter.setHasGeoInformation(mCheckHasGeo.isChecked());
		mSearchParameter.setSearchInCommon(mCheckInCommon.isChecked());
	}

	private void doSearch(Context ctx) {
		String s = mTagText.getText().toString();
		if (s == null || s.trim().length() == 0) {
			Toast.makeText(ctx,
					ctx.getString(R.string.msg_flickr_tag_search_empty_tag),
					Toast.LENGTH_LONG).show();
			return;
		}

		mSearchParameter.setTags(s);
		onAction(ACTION_DO, mSearchParameter);
	}

	@Override
	public View getView(Context ctx) {
		if (mView == null) {
			mView = LayoutInflater.from(ctx).inflate(
					R.layout.flickr_tag_search, null);
		}
		return mView;
	}

}
