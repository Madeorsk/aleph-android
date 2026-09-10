package org.joinmastodon.android.unifiedpush;

import java.util.Collection;
import java.util.Objects;

/**
 * How push notifications are delivered to this device: automatically chosen, Google FCM, or a named UnifiedPush distributor.
 */
public final class PushTransport{
	private static final String FCM_VALUE="fcm";

	public static final PushTransport AUTOMATIC=new PushTransport(null);
	public static final PushTransport FCM=new PushTransport(FCM_VALUE);

	private final String value;

	private PushTransport(String value){
		this.value=value;
	}

	/**
	 * A transport forced onto the UnifiedPush distributor with this package name.
	 */
	public static PushTransport distributor(String packageName){
		if(packageName==null || packageName.isEmpty() || FCM_VALUE.equals(packageName))
			throw new IllegalArgumentException("Invalid distributor package name '"+packageName+"'");
		return new PushTransport(packageName);
	}

	/**
	 * Reads back a {@link #serialize() serialized} transport.
	 * An unknown or absent value, and a distributor that is not in {@code installedDistributors}, both give {@link #AUTOMATIC}.
	 */
	public static PushTransport parse(String stored, Collection<String> installedDistributors){
		if(stored==null || stored.isEmpty())
			return AUTOMATIC;
		if(FCM_VALUE.equals(stored))
			return FCM;
		return installedDistributors.contains(stored) ? new PushTransport(stored) : AUTOMATIC;
	}

	/**
	 * The value to persist, null for {@link #AUTOMATIC}.
	 */
	public String serialize(){
		return value;
	}

	public boolean isAutomatic(){
		return value==null;
	}

	public boolean isFCM(){
		return FCM_VALUE.equals(value);
	}

	/**
	 * The forced distributor package name, or null when this transport is not a distributor.
	 */
	public String getDistributor(){
		return isAutomatic() || isFCM() ? null : value;
	}

	@Override
	public boolean equals(Object o){
		return o instanceof PushTransport other && Objects.equals(value, other.value);
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(value);
	}

	@Override
	public String toString(){
		return isAutomatic() ? "automatic" : value;
	}
}
