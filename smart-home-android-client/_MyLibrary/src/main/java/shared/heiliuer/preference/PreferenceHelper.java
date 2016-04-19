package shared.heiliuer.preference;


import android.content.Context;

/**
 * 已测试通过
 * 
 * @author heiliuer
 * 
 */
public class PreferenceHelper {
	private static PreferenceHelper data;
	private ShareHelper share;

	public static PreferenceHelper getPreferenceHelper() {
		if (data == null)
			throw new NullPointerException("在使用GlobalData前未初始化");
		return data;
	}

	/**
	 * 
	 * @param applicationContext
	 *            强烈建议用applicationContext
	 * @return
	 */
	public static PreferenceHelper initPreferenceHelper(
			Context applicationContext) {
		if (applicationContext == null)
			throw new NullPointerException("applicationContext is null");
		data = new PreferenceHelper(applicationContext);
		return data;
	}

	private PreferenceHelper(Context applicationContext) {
		share = ShareHelper.getShareHelperInstance(applicationContext,
				applicationContext.getPackageName());
		initData();
	}

	// 自定义share数据
	// begin

	private void initData() {
		size = share.getInt("size", 0);
	}

	private int size;

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
		share.setInt("size", size);
	}
	// end

}
