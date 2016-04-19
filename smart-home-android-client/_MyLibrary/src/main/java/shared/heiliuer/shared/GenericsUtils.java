package shared.heiliuer.shared;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GenericsUtils {

	public static String getGenricName(Class<?> clazz) {
		return clazz.getSimpleName();
	}

	/**
	 * 得到泛型类 泛型（T）的类型
	 * 
	 * @param clazz
	 * @return
	 */
	public static Class<?> getGenricType(Class<?> clazz) {
		return getGenricType(clazz, 0);
	}

	/**
	 * 得到泛型类 泛型（T）的类型
	 * 
	 * @param clazz
	 * @param index
	 *            上溯深度 0表示本类
	 * @return
	 */
	public static Class<?> getGenricType(Class<?> clazz, int index) {
		Type genType = clazz.getGenericSuperclass();
		if (!(genType instanceof ParameterizedType)) {
			Utils.d(clazz.getSimpleName()
					+ "'s superclass not ParameterizedType");
			return Object.class;
		}
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		if ((index >= params.length) || (index < 0)) {
			Utils.d("Index: " + index + ",Size of " + clazz.getSimpleName()
					+ "'s Parameterized Type:" + params.length);
			return Object.class;
		}
		if (!(params[index] instanceof Class)) {
			Utils.d(clazz.getSimpleName()
					+ " not set the actual class on superclass generic parameter");
			return Object.class;
		}
		return ((Class<?>) params[index]);
	}
}
