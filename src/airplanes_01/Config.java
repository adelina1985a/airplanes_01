package airplanes_01;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
	static Properties prop = new Properties();
	static boolean testMode;
	static String[] playerPlanes;
	static String[] aiPlanes;
	static boolean skipPlayerInput;

	static {
		InputStream input = null;

		try {
			input = new FileInputStream("config.properties");
			prop.load(input);

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @param key
	 * @return value for specified <b>key</b>, or <b>null</b> if empty property
	 */
	public static String getProp(String key) {
		String s = prop.getProperty(key);
		return s.length() != 0 ? s : null;
	}

	public static String[] getPropList(String key) {
		String s = prop.getProperty(key);
		return s.length() != 0 ? s.split(",") : null;
	}
	
	public static boolean getPropBool(String key){
		return prop.getProperty(key).equals("" + true) ? true : false;
	}

	public Config() {
		testMode = getPropBool("testMode");
		if (testMode) {// read the other test related props
			playerPlanes = getPropList("playerPlanes");
			aiPlanes = getPropList("aiPlanes");
			skipPlayerInput = getPropBool("skipPlayerInput");
		}
	}
}
