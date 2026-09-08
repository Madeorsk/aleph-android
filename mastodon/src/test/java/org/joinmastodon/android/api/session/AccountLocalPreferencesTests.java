package org.joinmastodon.android.api.session;

import org.joinmastodon.android.model.StatusContentType;
import org.junit.Test;

import static org.junit.Assert.*;

public class AccountLocalPreferencesTests{
	@Test
	public void defaults_the_content_type_to_plain(){
		assertEquals(StatusContentType.PLAIN, new AccountLocalPreferences(new FakeSharedPreferences()).postingDefaultContentType);
	}

	@Test
	public void keeps_the_saved_content_type(){
		FakeSharedPreferences prefs=new FakeSharedPreferences();
		AccountLocalPreferences written=new AccountLocalPreferences(prefs);
		written.postingDefaultContentType=StatusContentType.MARKDOWN;
		written.save();
		assertEquals(StatusContentType.MARKDOWN, new AccountLocalPreferences(prefs).postingDefaultContentType);
	}

	@Test
	public void falls_back_to_plain_on_an_unknown_content_type(){
		FakeSharedPreferences prefs=new FakeSharedPreferences();
		prefs.edit().putString("postingDefaultContentType", "BBCODE").apply();
		assertEquals(StatusContentType.PLAIN, new AccountLocalPreferences(prefs).postingDefaultContentType);
	}
}
