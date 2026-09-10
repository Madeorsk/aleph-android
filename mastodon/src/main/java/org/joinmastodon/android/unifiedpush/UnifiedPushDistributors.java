package org.joinmastodon.android.unifiedpush;

import org.joinmastodon.android.MastodonApp;
import org.unifiedpush.android.connector.UnifiedPush;

import java.util.List;

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
