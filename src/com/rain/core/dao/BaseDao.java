package com.rain.core.dao;

import java.util.List;
import java.util.Map;

import com.rain.core.pojo.BaseBean;

public interface BaseDao{

	public <T extends BaseBean> String insert(T entity);
	
	public <T extends BaseBean> Integer update(T entity);
	
	public <T extends BaseBean> Integer delete(T entity);
	
	public <T extends BaseBean> List<T> queryForList(T entity);
	
	public <T extends BaseBean> List<T> queryForList(T entity,String order);
	
	public <T extends BaseBean> List<Map<String,Object>> queryForListMap(T entity, String order);
	
	public  List<Map<String,Object>> queryForListMap(String sql, Map<String,Object> parameterMap);
}
