/**
 * 
 */
package com.gmail.charleszq.picorner.task.px500;

import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.net.Uri;
import android.util.Log;

import com.github.yuyang226.j500px.J500px;
import com.github.yuyang226.j500px.oauth.OAuthToken;
import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.task.AbstractContextAwareTask;
import com.gmail.charleszq.picorner.utils.IConstants;

/**
 * @author charleszq
 *
 */
public class Px500OAuthTask extends
		AbstractContextAwareTask<Void, Integer, String> {
	
	private final String TAG = Px500OAuthTask.class.getSimpleName();

	private static final Uri OAUTH_CALLBACK_URI = Uri.parse(IConstants.PX500_OAUTH_CALLBACK_SCHEMA
			+ "://oauth"); //$NON-NLS-1$
	
	private ProgressDialog mProgressDialog;

	public Px500OAuthTask(Context ctx) {
		super(ctx);
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mProgressDialog = ProgressDialog.show(mContext,
				"", mContext.getString(R.string.auth_gen_req_token)); //$NON-NLS-1$
		mProgressDialog.setCanceledOnTouchOutside(true);
		mProgressDialog.setCancelable(true);
		mProgressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dlg) {
				Px500OAuthTask.this.cancel(true);
			}
		});
	}

	@Override
	protected String doInBackground(Void... params) {
		try {
			J500px j500px = new J500px(IConstants.PX500_CONSUMER_KEY, IConstants.PX500_CONSUMER_SECRET);
			OAuthToken token = j500px.getOAuthInterface().getRequestToken(OAUTH_CALLBACK_URI.toString());
			saveTokenSecrent(token.getOauthTokenSecret());
			URL oauthUrl = j500px.getOAuthInterface().buildAuthenticationUrl(token);
			return oauthUrl.toString();
		} catch (Exception e) {
			Log.e(TAG, "Error to oauth", e); //$NON-NLS-1$
			return null;
		}
	}

	private void saveTokenSecrent(String oauthTokenSecret) {
		Activity act = (Activity) mContext;
		PicornerApplication app = (PicornerApplication) act
				.getApplication();
		app.savePx500TokenSecret(oauthTokenSecret);
	}

	/* (non-Javadoc)
	 * @see com.gmail.charleszq.picorner.task.AbstractGeneralTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(String result) {
		if( mProgressDialog != null ) {
			mProgressDialog.cancel();
		}
		super.onPostExecute(result);
	}
}
