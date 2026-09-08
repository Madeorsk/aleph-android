package org.joinmastodon.android.model;

import com.google.gson.annotations.SerializedName;

public enum StatusContentType{
	@SerializedName("text/plain")
	PLAIN,
	@SerializedName("text/markdown")
	MARKDOWN,
	@SerializedName("text/html")
	HTML;
}
