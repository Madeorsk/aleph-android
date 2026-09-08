package org.joinmastodon.android.model;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class InstanceContentTypeSupportTests{
	private InstanceV2 instanceWithApiVersions(Map<String, Long> apiVersions){
		InstanceV2 instance=new InstanceV2();
		instance.apiVersions=apiVersions;
		return instance;
	}

	@Test
	public void supported_when_glitch_api_is_advertised(){
		assertTrue(instanceWithApiVersions(Map.of("mastodon", 11L, "glitch", 1L)).supportsContentTypes());
	}

	@Test
	public void supported_on_later_glitch_api_versions(){
		assertTrue(instanceWithApiVersions(Map.of("glitch", 2L)).supportsContentTypes());
	}

	@Test
	public void not_supported_without_a_glitch_key(){
		assertFalse(instanceWithApiVersions(Map.of("mastodon", 11L)).supportsContentTypes());
	}

	@Test
	public void not_supported_without_api_versions(){
		assertFalse(instanceWithApiVersions(null).supportsContentTypes());
	}

	@Test
	public void not_supported_on_v1_instances(){
		assertFalse(new InstanceV1().supportsContentTypes());
	}
}
