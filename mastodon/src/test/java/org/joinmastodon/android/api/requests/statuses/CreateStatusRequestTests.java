package org.joinmastodon.android.api.requests.statuses;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joinmastodon.android.api.gson.IsoInstantTypeAdapter;
import org.joinmastodon.android.model.StatusContentType;
import org.junit.Test;

import java.time.Instant;

import static org.junit.Assert.*;

public class CreateStatusRequestTests{
	private final Gson gson=new GsonBuilder()
			.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
			.registerTypeAdapter(Instant.class, new IsoInstantTypeAdapter())
			.create();

	@Test
	public void omits_content_type_when_unset(){
		CreateStatus.Request req=new CreateStatus.Request();
		req.status="hello";
		assertFalse(gson.toJson(req).contains("content_type"));
	}

	@Test
	public void sends_the_content_type_when_set(){
		CreateStatus.Request req=new CreateStatus.Request();
		req.status="hello";
		req.contentType=StatusContentType.MARKDOWN;
		assertTrue(gson.toJson(req).contains("\"content_type\":\"text/markdown\""));
	}
}
