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
		postingDefaultContentType=StatusContentType.valueOf(prefs.getString("postingDefaultContentType", StatusContentType.PLAIN.name()));
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
