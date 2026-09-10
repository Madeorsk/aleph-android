package org.joinmastodon.android;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joinmastodon.android.model.PushNotification;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class PushNotificationPayloadTests{
	private final Gson gson=new GsonBuilder()
			.disableHtmlEscaping()
			.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
			.create();

	private PushNotification parse(String json){
		return PushNotificationHandler.parsePayload(gson, json.getBytes(StandardCharsets.UTF_8));
	}

	@Test
	public void parses_a_notification_payload(){
		PushNotification pn=parse("""
				{"access_token":"tok","preferred_locale":"en","notification_id":"12345",\
				"notification_type":"mention","icon":"https://example.org/avatar.png",\
				"title":"Someone mentioned you","body":"hello there"}""");
		assertNotNull(pn);
		assertEquals("12345", pn.notificationId);
		assertEquals(PushNotification.Type.MENTION, pn.notificationType);
		assertEquals("https://example.org/avatar.png", pn.icon);
		assertEquals("Someone mentioned you", pn.title);
		assertEquals("hello there", pn.body);
	}

	@Test
	public void parses_a_payload_without_the_optional_fields(){
		PushNotification pn=parse("""
				{"notification_type":"favourite","icon":"https://example.org/avatar.png","title":"t","body":"b"}""");
		assertNotNull(pn);
		assertEquals(PushNotification.Type.FAVORITE, pn.notificationType);
		assertNull(pn.notificationId);
		assertNull(pn.accessToken);
	}

	@Test
	public void rejects_a_payload_without_an_icon(){
		assertNull(parse("""
				{"notification_id":"12345","notification_type":"mention","title":"t","body":"b"}"""));
	}

	@Test
	public void rejects_a_notification_type_this_version_does_not_know(){
		assertNull(parse("""
				{"notification_type":"admin.sign_up","icon":"https://example.org/avatar.png","title":"t","body":"b"}"""));
	}

	@Test
	public void rejects_a_payload_that_is_not_valid_json(){
		assertNull(parse("not json at all"));
	}

	@Test
	public void rejects_an_empty_payload(){
		assertNull(parse(""));
	}
}
