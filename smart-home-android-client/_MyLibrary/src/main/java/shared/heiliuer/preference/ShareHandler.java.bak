package shared.heiliuer.preference;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ShareHandler implements InvocationHandler {

	
	public interface Share {

		//1
		public int getInt(String key, int defValue);

		public void setInt(String key, int value);
		//2
		public String getString(String key, String defValue);

		public void setString(String key, String value);
		//3
		public float getFloat(String key, float defValue);

		public void setFloat(String key, float value);
		//4
		public boolean getBoolean(String key, boolean defValue);

		public void setBoolean(String key, boolean value);
		//5
		public long getLong(String key, long defValue);

		public void setLong(String key, long value);
	}

	private SharedPreferences shared;
	private Editor editor;
	private Share proxyShare;
	public static Share getProxyInstance( Context c,
			String shareName){
		return getProxyInstance( new Share(){

			@Override
			public int getInt(String key, int defValue) {
				
				return 0;
			}

			@Override
			public void setInt(String key, int value) {
				
				
			}

			@Override
			public String getString(String key, String defValue) {
				
				return null;
			}

			@Override
			public void setString(String key, String value) {
				
				
			}

			@Override
			public float getFloat(String key, float defValue) {
				
				return 0;
			}

			@Override
			public void setFloat(String key, float value) {
				
				
			}

			@Override
			public boolean getBoolean(String key, boolean defValue) {
				
				return false;
			}

			@Override
			public void setBoolean(String key, boolean value) {
				
				
			}

			@Override
			public long getLong(String key, long defValue) {
				
				return 0;
			}

			@Override
			public void setLong(String key, long value) {
				
				
			}},  c,
				 shareName);
	}
	public static Share getProxyInstance(Share proxy, Context c,
			String shareName) {
		return (Share) Proxy.newProxyInstance(proxy.getClass().getClassLoader(), proxy
				.getClass().getInterfaces(), new ShareHandler(proxy, c,
				shareName));
	}

	private ShareHandler(Share proxy, Context c, String shareName) {
		shared = c.getSharedPreferences(shareName, Context.MODE_PRIVATE);
		this.proxyShare = proxy;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		String mName = method.getName();
		if (editor == null)
			editor = shared.edit();
		// int
		if (mName.equals("setInt")) {
			editor.putInt((String) args[0], (Integer) args[1]);
		} else if (mName.equals("getInt")) {
			return shared.getInt((String) args[0], (Integer) args[1]);
		}
		// String
		else if (mName.equals("setString")) {
			editor.putString((String) args[0], (String) args[1]);
		} else if (mName.equals("getString")) {
			return shared.getString((String) args[0], (String) args[1]);
		}
		// String
		else if (mName.equals("setFloat")) {
			editor.putFloat((String) args[0], (Float) args[1]);
		} else if (mName.equals("getFloat")) {
			return shared.getFloat((String) args[0], (Float) args[1]);
		}
		// boolean
		else if (mName.equals("setBoolean")) {
			editor.putBoolean((String) args[0], (Boolean) args[1]);
		} else if (mName.equals("getBoolean")) {
			return shared.getBoolean((String) args[0], (Boolean) args[1]);
		}
		// long
		else if (mName.equals("setLong")) {
			editor.putLong((String) args[0], (Long) args[1]);
		} else if (mName.equals("getLong")) {
			return shared.getLong((String) args[0], (Long) args[1]);
		} else {
			return method.invoke(proxyShare, args);
		}
		editor.commit();
		return null;

	}
}
