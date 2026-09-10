package org.joinmastodon.android.unifiedpush;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
	 * Reads back a {@link #serialize() serialized} transport as it was written, whether it is still usable or not.
	 * An absent value gives {@link #AUTOMATIC}.
	 */
	public static PushTransport of(String stored){
		if(stored==null || stored.isEmpty())
			return AUTOMATIC;
		return FCM_VALUE.equals(stored) ? FCM : new PushTransport(stored);
	}

	/**
	 * Reads back a {@link #serialize() serialized} preference.
	 * An absent value, and a distributor that is not in {@code installedDistributors}, both give {@link #AUTOMATIC}.
	 */
	public static PushTransport parse(String stored, Collection<String> installedDistributors){
		PushTransport transport=of(stored);
		String distributor=transport.getDistributor();
		return distributor==null || installedDistributors.contains(distributor) ? transport : AUTOMATIC;
	}

	/**
	 * The transport to actually register with, never {@link #AUTOMATIC}.
	 * A forced choice wins, as long as it is usable: a distributor needs to be installed, and needs a server able to
	 * send RFC 8291 payloads. Otherwise the first installed distributor is used, and FCM when there is none.
	 */
	public static PushTransport resolve(PushTransport preference, Collection<String> installedDistributors, boolean serverSupportsStandardWebPush){
		if(preference.isFCM() || !serverSupportsStandardWebPush)
			return FCM;
		if(!preference.isAutomatic() && installedDistributors.contains(preference.getDistributor()))
			return preference;
		return installedDistributors.isEmpty() ? FCM : distributor(installedDistributors.iterator().next());
	}

	/**
	 * The transports the user can choose from: {@link #AUTOMATIC} first, then the others in the order
	 * {@link #resolve(PushTransport, Collection, boolean) resolve} would pick them.
	 */
	public static List<PushTransport> options(Collection<String> installedDistributors){
		List<PushTransport> options=new ArrayList<>(installedDistributors.size()+2);
		options.add(AUTOMATIC);
		for(String distributor:installedDistributors)
			options.add(distributor(distributor));
		options.add(FCM);
		return options;
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
