package org.joinmastodon.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import org.joinmastodon.android.api.MastodonAPIController;
import org.joinmastodon.android.api.session.AccountSession;
import org.joinmastodon.android.api.session.AccountSessionManager;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PushNotificationReceiver extends BroadcastReceiver{
	private static final String TAG="PushNotificationReceive";

	public static final int NOTIFICATION_ID=178;

	@Override
	public void onReceive(Context context, Intent intent){
		if(BuildConfig.DEBUG){
			Log.e(TAG, "received: "+intent);
			Bundle extras=intent.getExtras();
			for(String key : extras.keySet()){
				Log.i(TAG, key+" -> "+extras.get(key));
			}
		}
		if("com.google.android.c2dm.intent.RECEIVE".equals(intent.getAction())){
			String subtype=intent.getStringExtra("subtype");
			if(subtype==null || !subtype.startsWith("wp:")){
				Log.w(TAG, "Subtype doesn't start with 'wp:'");
				return;
			}
			String pushAccountID=Uri.parse(subtype.substring(3)).getFragment();
			byte[] rawData=intent.getByteArrayExtra("rawData");
			boolean isRFC=!intent.hasExtra("crypto-key") && !intent.hasExtra("encryption");
			String encryptionParam=intent.getStringExtra("encryption");
			String cryptoKeyParam=intent.getStringExtra("crypto-key");
			if(!TextUtils.isEmpty(pushAccountID) && rawData!=null && (isRFC || !TextUtils.isEmpty(encryptionParam)) && (isRFC || !TextUtils.isEmpty(cryptoKeyParam))){
				MastodonAPIController.runInBackground(()->{
					try{
						List<AccountSession> accounts=AccountSessionManager.getInstance().getLoggedInAccounts();
						AccountSession account=null;
						for(AccountSession acc:accounts){
							if(pushAccountID.equals(acc.pushAccountID)){
								account=acc;
								break;
							}
						}
						if(account==null){
							Log.w(TAG, "onReceive: account for id '"+pushAccountID+"' not found");
							return;
						}
						String accountID=account.getID();
						if(isRFC!=AccountSessionManager.get(accountID).pushEncryptionFinalRFC){
							Log.i(TAG, "onReceive: isRFC mismatch between client and server");
							return;
						}
						byte[] decodedServerKey, decodedPayload, decodedSalt;
						if(isRFC){
							if(rawData.length<22){
								Log.i("TAG", "onReceive: payload is too short");
								return;
							}
							DataInputStream in=new DataInputStream(new ByteArrayInputStream(rawData));
							decodedSalt=new byte[16];
							in.readFully(decodedSalt);
							int rs=in.readInt();
							int idLen=in.read();
							decodedServerKey=new byte[idLen];
							in.readFully(decodedServerKey);
							decodedPayload=new byte[in.available()];
							in.readFully(decodedPayload);
						}else{
							Map<String, String> encryptionParams=parseKeyValueThing(encryptionParam);
							Map<String, String> cryptoKeyParams=parseKeyValueThing(cryptoKeyParam);
							String serverKey=cryptoKeyParams.get("dh");
							String salt=encryptionParams.get("salt");
							if(TextUtils.isEmpty(serverKey) || TextUtils.isEmpty(salt)){
								Log.i(TAG, "onReceive: server key or salt is invalid");
								return;
							}
							decodedServerKey=Base64.decode(serverKey, Base64.URL_SAFE);
							decodedPayload=rawData;
							decodedSalt=Base64.decode(salt, Base64.URL_SAFE);
						}
						byte[] decrypted=AccountSessionManager.getInstance().getAccount(accountID).getPushSubscriptionManager().decryptNotification(decodedServerKey, decodedPayload, decodedSalt);
						if(decrypted==null){
							Log.i(TAG, "onReceive: failed to decrypt payload");
							return;
						}
						PushNotificationHandler.handleDecryptedPayload(context, accountID, decrypted);
					}catch(Exception x){
						Log.w(TAG, x);
					}
				});
			}else{
				Log.w(TAG, "onReceive: invalid push notification format");
			}
		}
	}

	private Map<String, String> parseKeyValueThing(String thing){
		HashMap<String, String> res=new HashMap<>();
		for(String part:thing.split(";")){
			part=part.trim();
			if(!part.contains("="))
				continue;
			String[] kv=part.split("=", 2);
			res.put(kv[0], kv[1]);
		}
		return res;
	}
}
