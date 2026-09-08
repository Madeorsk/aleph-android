package org.joinmastodon.android.api.session;

import android.content.SharedPreferences;

import org.joinmastodon.android.model.StatusContentType;

public class AccountLocalPreferences{
	private final SharedPreferences prefs;

	public boolean serverSideFiltersSupported;
	public boolean adminReportsNotifications, adminSignupsNotifications;
	public StatusContentType postingDefaultContentType;

	public AccountLocalPreferences(SharedPreferences prefs){
		this.prefs=prefs;
		serverSideFiltersSupported=prefs.getBoolean("serverSideFilters", false);
		adminReportsNotifications=prefs.getBoolean("adminReports", true);
		adminSignupsNotifications=prefs.getBoolean("adminSignups", true);
		postingDefaultContentType=readContentType();
	}

	private StatusContentType readContentType(){
		String name=prefs.getString("postingDefaultContentType", null);
		if(name==null)
			return StatusContentType.PLAIN;
		try{
			return StatusContentType.valueOf(name);
		}catch(IllegalArgumentException x){
			// A type written by a newer version of the app
			return StatusContentType.PLAIN;
		}
	}

	public long getNotificationsPauseEndTime(){
		return prefs.getLong("notificationsPauseTime", 0L);
	}

	public void setNotificationsPauseEndTime(long time){
		prefs.edit().putLong("notificationsPauseTime", time).apply();
	}

	public void save(){
		prefs.edit()
				.putBoolean("serverSideFilters", serverSideFiltersSupported)
				.putBoolean("adminReports", adminReportsNotifications)
				.putBoolean("adminSignups", adminSignupsNotifications)
				.putString("postingDefaultContentType", postingDefaultContentType.name())
				.apply();
	}
}
