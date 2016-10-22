package com.rain.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesUtil {

	public static Map<String,String> resolveProperties (File proFile){
		
		Map<String,String> m = new HashMap<String,String>();
		Properties pro = new Properties();
		try {
			pro.load(new FileInputStream(proFile));
			Enumeration keys = pro.propertyNames();
			while (keys.hasMoreElements()){
				String key = (String)keys.nextElement();
				String value = pro.getProperty(key);
				m.put(key, value);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return m;
		
	}
}
