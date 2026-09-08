package org.joinmastodon.android.api.session;

import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/** In-memory {@link SharedPreferences}, for tests that need values to survive a read-write round trip. */
class FakeSharedPreferences implements SharedPreferences{
	private final Map<String, Object> values=new HashMap<>();

	@Override
	public Map<String, ?> getAll(){
		return values;
	}

	@Override
	public String getString(String key, String defValue){
		return values.containsKey(key) ? (String) values.get(key) : defValue;
	}

	@Override
	public Set<String> getStringSet(String key, Set<String> defValues){
		return values.containsKey(key) ? (Set<String>) values.get(key) : defValues;
	}

	@Override
	public int getInt(String key, int defValue){
		return values.containsKey(key) ? (Integer) values.get(key) : defValue;
	}

	@Override
	public long getLong(String key, long defValue){
		return values.containsKey(key) ? (Long) values.get(key) : defValue;
	}

	@Override
	public float getFloat(String key, float defValue){
		return values.containsKey(key) ? (Float) values.get(key) : defValue;
	}

	@Override
	public boolean getBoolean(String key, boolean defValue){
		return values.containsKey(key) ? (Boolean) values.get(key) : defValue;
	}

	@Override
	public boolean contains(String key){
		return values.containsKey(key);
	}

	@Override
	public Editor edit(){
		return new Editor(){
			@Override
			public Editor putString(String key, String value){
				values.put(key, value);
				return this;
			}

			@Override
			public Editor putStringSet(String key, Set<String> value){
				values.put(key, value);
				return this;
			}

			@Override
			public Editor putInt(String key, int value){
				values.put(key, value);
				return this;
			}

			@Override
			public Editor putLong(String key, long value){
				values.put(key, value);
				return this;
			}

			@Override
			public Editor putFloat(String key, float value){
				values.put(key, value);
				return this;
			}

			@Override
			public Editor putBoolean(String key, boolean value){
				values.put(key, value);
				return this;
			}

			@Override
			public Editor remove(String key){
				values.remove(key);
				return this;
			}

			@Override
			public Editor clear(){
				values.clear();
				return this;
			}

			@Override
			public boolean commit(){
				return true;
			}

			@Override
			public void apply(){}
		};
	}

	@Override
	public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener){}

	@Override
	public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener){}
}
