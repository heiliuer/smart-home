package shared.heiliuer.shared;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesHelper {

	public static PropertiesHelper getPropertiesHelper(InputStream in) {
		Properties p = new Properties();
		try {
			p.load(in);
			return new PropertiesHelper(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private PropertiesHelper(Properties p) {
		this.properties = p;
	}

	public String get(String key) {
		return properties.getProperty(key);
	}

	private Properties properties;

}
