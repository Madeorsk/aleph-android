package org.joinmastodon.android.unifiedpush;

import org.joinmastodon.android.MastodonApp;

import java.util.List;

import org.unifiedpush.android.connector.UnifiedPush;

/**
 * The UnifiedPush distributors installed on this device.
 */
public class UnifiedPushDistributors{
	private UnifiedPushDistributors(){}

	/**
	 * Package names of every installed distributor, in the order the connector reports them.
	 */
	public static List<String> installed(){
		return UnifiedPush.getDistributors(MastodonApp.context);
	}
}
