package org.joinmastodon.android.unifiedpush;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class PushTransportTests{
	private static final List<String> INSTALLED=List.of("org.unifiedpush.distributor.ntfy", "io.heckel.ntfy");

	@Test
	public void nothing_stored_reads_back_as_automatic(){
		assertEquals(PushTransport.AUTOMATIC, PushTransport.parse(null, INSTALLED));
		assertEquals(PushTransport.AUTOMATIC, PushTransport.parse("", INSTALLED));
	}

	@Test
	public void fcm_survives_a_round_trip(){
		assertEquals("fcm", PushTransport.FCM.serialize());
		assertEquals(PushTransport.FCM, PushTransport.parse("fcm", INSTALLED));
		assertEquals(PushTransport.FCM, PushTransport.parse("fcm", List.of()));
	}

	@Test
	public void transports_are_only_equal_to_themselves(){
		assertNotEquals(PushTransport.AUTOMATIC, PushTransport.FCM);
		assertNotEquals(PushTransport.FCM, PushTransport.distributor("io.heckel.ntfy"));
		assertNotEquals(PushTransport.distributor("io.heckel.ntfy"), PushTransport.distributor("org.unifiedpush.distributor.ntfy"));
		assertNotEquals(PushTransport.AUTOMATIC, null);
		assertEquals(PushTransport.distributor("io.heckel.ntfy").hashCode(), PushTransport.distributor("io.heckel.ntfy").hashCode());
	}

	@Test
	public void an_installed_distributor_survives_a_round_trip(){
		PushTransport transport=PushTransport.distributor("io.heckel.ntfy");
		PushTransport read=PushTransport.parse(transport.serialize(), INSTALLED);
		assertEquals(transport, read);
		assertEquals("io.heckel.ntfy", read.getDistributor());
	}

	@Test
	public void a_distributor_that_is_no_longer_installed_reads_back_as_automatic(){
		assertEquals(PushTransport.AUTOMATIC, PushTransport.parse("org.uninstalled.distributor", INSTALLED));
		assertEquals(PushTransport.AUTOMATIC, PushTransport.parse("io.heckel.ntfy", List.of()));
	}

	@Test
	public void automatic_is_stored_as_nothing(){
		assertNull(PushTransport.AUTOMATIC.serialize());
		assertTrue(PushTransport.AUTOMATIC.isAutomatic());
		assertNull(PushTransport.AUTOMATIC.getDistributor());
	}

	@Test
	public void fcm_is_neither_automatic_nor_a_distributor(){
		assertFalse(PushTransport.FCM.isAutomatic());
		assertTrue(PushTransport.FCM.isFCM());
		assertNull(PushTransport.FCM.getDistributor());
	}

	@Test
	public void a_distributor_is_neither_automatic_nor_fcm(){
		PushTransport transport=PushTransport.distributor("io.heckel.ntfy");
		assertFalse(transport.isAutomatic());
		assertFalse(transport.isFCM());
	}

	@Test
	public void a_stored_transport_is_read_back_as_written(){
		assertEquals(PushTransport.AUTOMATIC, PushTransport.of(null));
		assertEquals(PushTransport.FCM, PushTransport.of(PushTransport.FCM.serialize()));
		assertEquals(PushTransport.distributor("org.uninstalled.distributor"), PushTransport.of("org.uninstalled.distributor"));
	}

	@Test
	public void automatic_resolves_to_the_first_installed_distributor(){
		assertEquals(PushTransport.distributor("org.unifiedpush.distributor.ntfy"),
				PushTransport.resolve(PushTransport.AUTOMATIC, INSTALLED, true));
	}

	@Test
	public void automatic_resolves_to_fcm_without_a_distributor(){
		assertEquals(PushTransport.FCM, PushTransport.resolve(PushTransport.AUTOMATIC, List.of(), true));
	}

	@Test
	public void a_forced_distributor_wins_over_the_first_installed_one(){
		PushTransport forced=PushTransport.distributor("io.heckel.ntfy");
		assertEquals(forced, PushTransport.resolve(forced, INSTALLED, true));
	}

	@Test
	public void forced_fcm_is_kept_even_with_a_distributor_installed(){
		assertEquals(PushTransport.FCM, PushTransport.resolve(PushTransport.FCM, INSTALLED, true));
	}

	@Test
	public void a_forced_distributor_that_is_not_installed_falls_back_to_an_installed_one(){
		assertEquals(PushTransport.distributor("org.unifiedpush.distributor.ntfy"),
				PushTransport.resolve(PushTransport.distributor("org.uninstalled.distributor"), INSTALLED, true));
	}

	@Test
	public void a_server_without_standard_web_push_stays_on_fcm(){
		assertEquals(PushTransport.FCM, PushTransport.resolve(PushTransport.AUTOMATIC, INSTALLED, false));
		assertEquals(PushTransport.FCM, PushTransport.resolve(PushTransport.distributor("io.heckel.ntfy"), INSTALLED, false));
	}

	@Test
	public void the_choices_follow_the_order_automatic_picks_them_in(){
		assertEquals(List.of(PushTransport.AUTOMATIC,
						PushTransport.distributor("org.unifiedpush.distributor.ntfy"), PushTransport.distributor("io.heckel.ntfy"),
						PushTransport.FCM),
				PushTransport.options(INSTALLED));
	}

	@Test
	public void the_choices_without_a_distributor_are_automatic_and_fcm(){
		assertEquals(List.of(PushTransport.AUTOMATIC, PushTransport.FCM), PushTransport.options(List.of()));
	}

	@Test
	public void a_distributor_needs_a_package_name(){
		assertThrows(IllegalArgumentException.class, ()->PushTransport.distributor(null));
		assertThrows(IllegalArgumentException.class, ()->PushTransport.distributor(""));
		assertThrows(IllegalArgumentException.class, ()->PushTransport.distributor("fcm"));
	}
}
