package shared.heiliuer.shared;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.text.TextUtils;

public class GetNetString {

	public static final String METHOD_GET = "GET";
	public static final String METHOD_POST = "POST";
	public static final int STATUS_SUCCESS = 1;
	public static final int STATUS_RESULT_NULL = 0;
	public static final int STATUS_URL_EXCETION = -1;
	public static final int STATUS_IOEXCEPTION = -3;

	public static void get(String url, OnSearchResult onSearchResult) {
		get(url, METHOD_GET, onSearchResult);
	}

	public static void get(String url, String method,
			OnSearchResult onSearchResult) {
		new Thread(new Search().init(url, onSearchResult, method)).start();
	}

	private static class Search implements Runnable {
		private String url;
		private OnSearchResult onSearchResult;
		private String method;

		public Runnable init(String url, OnSearchResult onSearchResult,
				String method) {
			this.method = method;
			this.url = url;
			this.onSearchResult = onSearchResult;
			return this;
		}

		@Override
		public void run() {
			try {
				URL u = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) u.openConnection();
				conn.setRequestMethod(method);
				conn.setConnectTimeout(6000);
				if (conn.getResponseCode() == 200) {
					String results = Utils.getStringFromInputStream(conn
							.getInputStream());
					if (!TextUtils.isEmpty(results)) {
						onSearchResult.searchResult(results, STATUS_SUCCESS);
					} else {
						onSearchResult.searchResult(null, STATUS_RESULT_NULL);
					}
					return;
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
				onSearchResult.searchResult(null, STATUS_URL_EXCETION);
			} catch (IOException e) {
				e.printStackTrace();
				onSearchResult.searchResult(null, STATUS_IOEXCEPTION);
			}
		}
	}

	public static interface OnSearchResult {
		public void searchResult(String result, int statu);
	}
}
