package org.joinmastodon.android.api.requests.statuses;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joinmastodon.android.api.ObjectValidationException;
import org.joinmastodon.android.model.StatusContentType;
import org.junit.Test;

import static org.junit.Assert.*;

public class GetStatusSourceTextTests{
	private final Gson gson=new GsonBuilder()
			.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
			.create();

	private GetStatusSourceText.Response parse(String json) throws ObjectValidationException{
		GetStatusSourceText.Response response=gson.fromJson(json, GetStatusSourceText.Response.class);
		response.postprocess();
		return response;
	}

	@Test
	public void reads_the_content_type_when_present() throws Exception{
		assertEquals(StatusContentType.MARKDOWN, parse("{\"id\":\"1\",\"text\":\"hi\",\"spoiler_text\":\"\",\"content_type\":\"text/markdown\"}").contentType);
	}

	@Test
	public void stays_valid_without_a_content_type() throws Exception{
		assertNull(parse("{\"id\":\"1\",\"text\":\"hi\",\"spoiler_text\":\"\"}").contentType);
	}

	@Test
	public void still_rejects_a_missing_text() {
		assertThrows(ObjectValidationException.class, ()->parse("{\"id\":\"1\",\"spoiler_text\":\"\"}"));
	}

	@Test
	public void still_rejects_a_missing_spoiler_text(){
		assertThrows(ObjectValidationException.class, ()->parse("{\"id\":\"1\",\"text\":\"hi\"}"));
	}
}
