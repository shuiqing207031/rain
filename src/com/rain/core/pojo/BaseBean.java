package com.rain.core.pojo;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rain.core.util.StringUtils;


public  abstract class BaseBean implements Serializable{
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(BaseBean.class);
	private String tableName;
	
	public  String getTableName(){
		if(tableName == null)
			init();
		return tableName;
	}
	
	public abstract String getSequenceName();
	
	public abstract String getPKName();
	
	public abstract String getPKValue();
	
	protected List<String> fieldNames = new ArrayList<String>(); //属性列表
	protected List<String> columnNames = new ArrayList<String>(); //对应的列名,大写
	protected Map<String,Field> fieldMap = new HashMap<String,Field>(); //K:数据库字段名大写,V:对应的字段
	protected Map<String,Method> setterMethodMap = new HashMap<String,Method>();//大写
	protected Map<String,Method> getterMethodMap = new HashMap<String,Method>();
	
	protected  final void init(){
		if (this.getClass().isAnnotationPresent(Table.class)){
			Table t = this.getClass().getAnnotation(Table.class);
			tableName = t.name();
		}
		
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field f : fields){
			if (f.isAnnotationPresent(Column.class)){
				Column column = f.getAnnotation(Column.class);
				String colName = column.name();
				if(StringUtils.isEmpty(colName)){
					colName = f.getName();
				}
				fieldMap.put(colName.toUpperCase(), f);
				fieldNames.add(f.getName());
				columnNames.add(colName.toUpperCase());
			}
		}
		
		Method[] methods = this.getClass().getDeclaredMethods();
		for (Method method : methods){
			String methodName = method.getName();
			if (methodName != null && Modifier.isPublic(method.getModifiers())){
				String fName = methodName.substring(3);
				String firstChar = String.valueOf(fName.charAt(0)).toLowerCase();
				String realName = firstChar+fName.substring(1);
				 
				if (fieldNames.contains(realName) && methodName.startsWith("set")){
					setterMethodMap.put(realName, method);
				} else if (fieldNames.contains(realName) && methodName.startsWith("get")){
					getterMethodMap.put(realName, method);
				}
			}
		}
		if(tableName == null || fieldNames.size() == 0){
			throw new RuntimeException("不是一个POJO类，请加上table和column注解:"+this.getClass().getName());
		}
	}
	
	/**
	 * 设置字段值
	 * @param fieldName
	 * @param value
	 */
	public void setValue(String fieldName,Object value){
		Method method = this.getSetterMethodMap().get(fieldName);
		if (method == null){
			log.error("the pojo has not field named {}",fieldName);
			throw new RuntimeException("the pojo has not field named "+fieldName);
		} else {
			try {
				method.invoke(this, value);
			} catch (Exception e) {
				log.error(e.getMessage());
				throw new RuntimeException(e);
			}
		}
	}
	
	
	
	
	/**
	 * 根据字段名获取值
	 * @param fieldName 字段名
	 * @return
	 */
	public Object getValue(String fieldName){
		Method method = this.getGetterMethodMap().get(fieldName);
		if (method == null){
			log.error("the pojo has not field named {}",fieldName);
			throw new RuntimeException("the pojo has not field named "+fieldName);
		} else {
			try {
				return method.invoke(this, null);
			} catch (Exception e) {
				log.error(e.getMessage());
				return null;
			} 
		}
		
	}

	public <T> T getValue(String fieldName,Class<T> clazz){
		 Object value = getValue(fieldName);
		 if (value == null) return null;
		return (T)value;
	}
	
	
	
	/**
	 * 对对象中所有字符串都进行trim操作
	 * @param defaultEmptyStr 为空时的填充默认字段
	 */
	public void trimAndEmptyDefault(String defaultEmptyStr){
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field f : fields){
			Class clazz = f.getType();
			if (clazz == String.class){
				String value = this.getValue(f.getName(), String.class);
				if (value!=null && !StringUtils.isEmpty(value)){
					this.setValue(f.getName(),value.trim());
				} else {
					this.setValue(f.getName(), defaultEmptyStr);
				}
			}
		}
	}

	public Map<String, Field> getFieldMap() {
		if (fieldMap == null || fieldMap.size() == 0){
			init();
		}
		return fieldMap;
	}

	public Map<String, Method> getSetterMethodMap() {
		if (setterMethodMap == null || setterMethodMap.size() == 0)
			init();
		return setterMethodMap;
	}

	public Map<String, Method> getGetterMethodMap() {
		if (getterMethodMap == null || getterMethodMap.size() == 0)
			init();
		
		return getterMethodMap;
	}

	public List<String> getFieldNames() {
		if (fieldNames == null || fieldNames.size() == 0)
			init();
		return fieldNames;
	}
	
	public List<String> getColumnNames() {
		if (columnNames == null || columnNames.size() == 0)
			init();
		return columnNames;
	}

	
	
	
}
