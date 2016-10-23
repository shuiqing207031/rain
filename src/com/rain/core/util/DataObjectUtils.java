package com.rain.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Method;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DataObjectUtils {
	
	private static Logger log = LoggerFactory.getLogger(DataObjectUtils.class);
	
	
	public static String clobToString(Clob clob) throws SQLException, IOException{
		StringBuffer sb = new StringBuffer();
		Reader r = clob.getCharacterStream();
		BufferedReader br = new BufferedReader(r);
		String s = br.readLine();
		while (s != null){
			sb.append(s);
			s = br.readLine();
		}
		return sb.toString();
		
	}
	
	/**
	 * 
	 * @param <T>
	 * @param o 被转换的对象
	 * @param clazz  转换成的类型
	 * @return
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 */
	public static <T> T converObjectToType(Object o ,Class<T> clazz) throws Exception{
		if (o == null) return null;

		//数值类
		if(o instanceof Number && Number.class.isAssignableFrom(clazz)){
			Number value = (Number)o;
			Method m = clazz.getMethod("valueOf",String.class);
			return (T)m.invoke(null,o.toString());
		}
		
       //CLOB 类型
		if (o instanceof Clob && clazz == String.class){
			return (T)clobToString((Clob)o);
		}
		
		//Date类型
		if (o instanceof java.util.Date){
			if (clazz == String.class){
				return (T)o.toString();
			} else if (clazz == Date.class){
				return (T)o;
			}
		}

		if (clazz == String.class){
			return (T)o.toString();
		}

		log.warn("hello unsupport convert,please check the type:"+o.getClass().getName()+" to "+clazz.getName());
		return null;
	}

}
