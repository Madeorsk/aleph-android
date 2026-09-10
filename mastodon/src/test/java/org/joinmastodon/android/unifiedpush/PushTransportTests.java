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
		assertEquals(PushTransport.FCM, PushTransport.parse(PushTransport.FCM.serialize(), INSTALLED));
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
	public void a_distributor_needs_a_package_name(){
		assertThrows(IllegalArgumentException.class, ()->PushTransport.distributor(null));
		assertThrows(IllegalArgumentException.class, ()->PushTransport.distributor(""));
	}
}
