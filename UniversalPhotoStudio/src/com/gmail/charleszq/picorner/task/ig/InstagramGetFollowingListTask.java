/**
 * 
 */
package com.gmail.charleszq.picorner.task.ig;

import java.util.ArrayList;
import java.util.List;

import org.jinstagram.AdvancedInstagram;
import org.jinstagram.entity.common.Pagination;
import org.jinstagram.entity.users.feed.UserFeed;
import org.jinstagram.entity.users.feed.UserFeedData;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.gmail.charleszq.picorner.BuildConfig;
import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.model.Author;
import com.gmail.charleszq.picorner.task.AbstractContextAwareTask;
import com.gmail.charleszq.picorner.utils.IConstants;
import com.gmail.charleszq.picorner.utils.InstagramHelper;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class InstagramGetFollowingListTask extends
		AbstractContextAwareTask<Void, Integer, List<Author>> {

	public InstagramGetFollowingListTask(Context ctx) {
		super(ctx);
	}

	@Override
	protected List<Author> doInBackground(Void... params) {
		PicornerApplication app = (PicornerApplication) ((Activity) mContext)
				.getApplication();
		String igUserId = app.getInstagramUserId();
		AdvancedInstagram ig = InstagramHelper.getInstance()
				.getAuthedInstagram(app.getInstagramAuthToken());
		List<Author> users = new ArrayList<Author>();
		try {
			UserFeed uf = ig.getUserFollowList(Long.parseLong(igUserId));
			processUserDataFeed(uf,users);
			Pagination page = uf.getPagination();
			while( page != null && page.getNextUrl() != null ) {
				uf = ig.getUserNextPage(page, IConstants.DEF_IG_PAGE_SIZE);
				processUserDataFeed(uf,users);
			}
		} catch (Exception e) {
			if (BuildConfig.DEBUG)
				Log.w(TAG,
						"error to instagram following list: " + e.getMessage()); //$NON-NLS-1$
		}
		return users;
	}
	
	private void processUserDataFeed(UserFeed uf, List<Author> users) {
		for (UserFeedData ufd : uf.getUserList()) {
			Author a = new Author();
			a.setUserId(String.valueOf(ufd.getId()));
			a.setUserName(ufd.getUserName());
			a.setBuddyIconUrl(ufd.getProfilePictureUrl());
			users.add(a);
		}
	}

}
