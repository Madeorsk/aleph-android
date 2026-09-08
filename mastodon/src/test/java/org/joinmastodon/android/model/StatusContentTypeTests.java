package org.joinmastodon.android.model;

import com.google.gson.Gson;

import org.junit.Test;

import static org.junit.Assert.*;

public class StatusContentTypeTests{
	private final Gson gson=new Gson();

	@Test
	public void serializes_plain_as_mime_type(){
		assertEquals("\"text/plain\"", gson.toJson(StatusContentType.PLAIN));
	}

	@Test
	public void serializes_markdown_as_mime_type(){
		assertEquals("\"text/markdown\"", gson.toJson(StatusContentType.MARKDOWN));
	}

	@Test
	public void serializes_html_as_mime_type(){
		assertEquals("\"text/html\"", gson.toJson(StatusContentType.HTML));
	}

	@Test
	public void deserializes_plain_from_mime_type(){
		assertEquals(StatusContentType.PLAIN, gson.fromJson("\"text/plain\"", StatusContentType.class));
	}

	@Test
	public void deserializes_markdown_from_mime_type(){
		assertEquals(StatusContentType.MARKDOWN, gson.fromJson("\"text/markdown\"", StatusContentType.class));
	}

	@Test
	public void deserializes_html_from_mime_type(){
		assertEquals(StatusContentType.HTML, gson.fromJson("\"text/html\"", StatusContentType.class));
	}

	@Test
	public void deserializes_unknown_content_type_as_null(){
		assertNull(gson.fromJson("\"text/nonsense\"", StatusContentType.class));
	}
}
