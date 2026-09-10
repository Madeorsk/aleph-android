package org.joinmastodon.android.fragments.settings;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import org.joinmastodon.android.GlobalUserPreferences;
import org.joinmastodon.android.R;
import org.joinmastodon.android.api.PushSubscriptionManager;
import org.joinmastodon.android.model.viewmodel.CheckableListItem;
import org.joinmastodon.android.model.viewmodel.ListItem;
import org.joinmastodon.android.unifiedpush.PushTransport;
import org.joinmastodon.android.unifiedpush.UnifiedPushDistributors;

import java.util.ArrayList;
import java.util.List;

public class SettingsAppNotificationsFragment extends BaseSettingsFragment<PushTransport>{
	/**
	 * The name to show for a transport: the distributor's app name when it is one, otherwise a fixed label.
	 */
	public static String getTransportLabel(Context context, PushTransport transport){
		if(transport.isAutomatic())
			return context.getString(R.string.push_transport_automatic);
		if(transport.isFCM())
			return context.getString(R.string.push_transport_fcm);
		String distributor=transport.getDistributor();
		PackageManager pm=context.getPackageManager();
		try{
			return pm.getApplicationInfo(distributor, 0).loadLabel(pm).toString();
		}catch(PackageManager.NameNotFoundException x){
			return distributor;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setTitle(R.string.settings_notifications);

		List<ListItem<PushTransport>> items=new ArrayList<>();
		for(PushTransport transport:PushTransport.options(UnifiedPushDistributors.installed())){
			items.add(new CheckableListItem<>(getTransportLabel(getActivity(), transport), getTransportSubtitle(transport),
					CheckableListItem.Style.RADIO, transport.equals(GlobalUserPreferences.pushTransport),
					this::onTransportClick, transport));
		}
		onDataLoaded(items);
	}

	private String getTransportSubtitle(PushTransport transport){
		if(transport.isAutomatic())
			return getString(R.string.push_transport_automatic_subtitle);
		if(transport.isFCM())
			return getString(R.string.push_transport_fcm_subtitle);
		return transport.getDistributor();
	}

	@Override
	protected void doLoadData(int offset, int count){}

	private void onTransportClick(ListItem<?> item){
		PushTransport transport=((CheckableListItem<PushTransport>)item).parentObject;
		if(transport.equals(GlobalUserPreferences.pushTransport))
			return;
		GlobalUserPreferences.pushTransport=transport;
		GlobalUserPreferences.save();
		for(ListItem<PushTransport> other:data){
			CheckableListItem<PushTransport> checkable=(CheckableListItem<PushTransport>)other;
			boolean checked=checkable.parentObject.equals(transport);
			if(checked!=checkable.checked){
				checkable.setChecked(checked);
				rebindItem(checkable);
			}
		}
		PushSubscriptionManager.tryRegisterAll();
	}
}
