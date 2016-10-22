package com.rain.core.util;

public class StringUtils {

	public static boolean isEmpty(String str){
		if (str == null){
			return true;
		}
		if (str.trim().length() == 0){
			return true;
		}
		
		return false;
		
	}
		
	
}
