package org.joinmastodon.android.unifiedpush;

import android.util.Log;

import androidx.annotation.NonNull;

import org.joinmastodon.android.MastodonApp;
import org.joinmastodon.android.PushNotificationHandler;
import org.joinmastodon.android.api.session.AccountSession;
import org.joinmastodon.android.api.session.AccountSessionManager;
import org.unifiedpush.android.connector.FailedReason;
import org.unifiedpush.android.connector.PushService;
import org.unifiedpush.android.connector.data.PublicKeySet;
import org.unifiedpush.android.connector.data.PushEndpoint;
import org.unifiedpush.android.connector.data.PushMessage;

/**
 * Receives the push events of every account registered with a UnifiedPush distributor.
 * The instance string is the account ID.
 */
public class UnifiedPushService extends PushService{
	private static final String TAG="UnifiedPushService";

	@Override
	public void onNewEndpoint(@NonNull PushEndpoint endpoint, @NonNull String instance){
		AccountSession session=AccountSessionManager.getInstance().tryGetAccount(instance);
		if(session==null){
			Log.w(TAG, "onNewEndpoint: account '"+instance+"' not found");
			return;
		}
		PublicKeySet keys=endpoint.getPubKeySet();
		if(keys==null){
			Log.w(TAG, "onNewEndpoint: distributor gave no Web Push keys for account "+instance);
			return;
		}
		session.getPushSubscriptionManager().registerUnifiedPushEndpoint(endpoint.getUrl(), keys.getPubKey(), keys.getAuth());
	}

	@Override
	public void onMessage(@NonNull PushMessage message, @NonNull String instance){
		if(!message.getDecrypted()){
			Log.w(TAG, "onMessage: dropping notification that could not be decrypted for account "+instance);
			return;
		}
		// The connector unbinds this service a few seconds from now, so the notification work outlives it.
		PushNotificationHandler.handleDecryptedPayload(MastodonApp.context, instance, message.getContent());
	}

	@Override
	public void onUnregistered(@NonNull String instance){
		AccountSession session=AccountSessionManager.getInstance().tryGetAccount(instance);
		if(session==null)
			return;
		Log.i(TAG, "onUnregistered: registering account "+instance+" again");
		session.getPushSubscriptionManager().reRegister(session);
	}

	@Override
	public void onRegistrationFailed(@NonNull FailedReason reason, @NonNull String instance){
		AccountSession session=AccountSessionManager.getInstance().tryGetAccount(instance);
		if(session==null)
			return;
		Log.w(TAG, "onRegistrationFailed: account "+instance+" falls back to FCM, reason "+reason);
		session.getPushSubscriptionManager().fallBackToFCM(session);
	}
}
