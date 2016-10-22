package com.rain.core.dao.impl;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.rain.core.dao.BaseDao;
import com.rain.core.pojo.BaseBean;
import com.rain.core.util.DataObjectUtils;
import com.rain.core.util.StringUtils;

@Repository("baseDao")
@SuppressWarnings("unchecked")
public class BaseDaoImpl implements BaseDao {

	private static Logger log = LoggerFactory.getLogger(BaseDaoImpl.class);
 
	@Autowired
	protected NamedParameterJdbcTemplate template;

	@Override
	public <T extends BaseBean> String insert(T entity) {
		String sql = this.generateInsertSql(entity);
		log.info("PreparedStatement sql :{}", sql);
		
		KeyHolder keyHolder = new  GeneratedKeyHolder();
		SqlParameterSource s = new BeanPropertySqlParameterSource(entity);
		
		int rows = template.update(sql, s, keyHolder);
		Number n = keyHolder.getKey();
		String pkValue;
		if (n!= null){
			pkValue = n.toString();
		} else {
			pkValue = entity.getPKValue();
		}
		log.info(" insert {} rows and primary key is {} ",rows,pkValue);
		return pkValue;
	}
	

	@Override
	public <T extends BaseBean> Integer update(T entity) {
		String sql = this.generateUpdateSql(entity);
		log.info("PreparedStatement sql :{}", sql);
		SqlParameterSource s = new BeanPropertySqlParameterSource(entity);
		int rows = template.update(sql, s);
		log.info(" update {} rows ",rows);
		return rows;
	}

	@Override
	public <T extends BaseBean> Integer delete(T entity) {
		String sql = this.generateDeleteSql(entity);
		log.info("PreparedStatement sql :{}", sql);
		SqlParameterSource s = new BeanPropertySqlParameterSource(entity);
		int rows = template.update(sql, s);
		log.info(" delete {} rows ",rows);
		return rows;
	}


	@Override
	public <T extends BaseBean> List<T> queryForList(T entity) {
		String sql = this.generateQuerySql(entity);
		log.info("PreparedStatement sql :{}", sql);
		SqlParameterSource s = new BeanPropertySqlParameterSource(entity);
		List<T> list = template.query(sql, s,getRowMapper(entity));
		log.info(" complete and result size :{}",list.size());
		return list;
	}

	@Override
	public <T extends BaseBean> List<T> queryForList(T entity, String order) {
		String sql = this.generateQuerySql(entity,order);
		log.info("PreparedStatement sql :{}", sql);
		SqlParameterSource s = new BeanPropertySqlParameterSource(entity);
		System.out.println(entity.getClass().getName());
		List<T> list = (List<T>) template.query(sql, s, getRowMapper(entity));
		log.info(" query result size :{}",list.size());
		return list;
	}
	
	@Override
	public <T extends BaseBean> List<Map<String,Object>> queryForListMap(T entity, String order) {
		String sql = this.generateQuerySql(entity,order);
		log.info("PreparedStatement sql :{}", sql);
		SqlParameterSource s = new BeanPropertySqlParameterSource(entity);
		System.out.println(entity.getClass().getName());
		List<Map<String,Object>> list =  template.queryForList(sql, s);
		log.info(" query result size :{}",list.size());
		return list;
	}
	
	public  List<Map<String,Object>> queryForListMap(String sql, Map<String,Object> parameterMap) {
		log.info("PreparedStatement sql :{}", sql);
		List<Map<String,Object>> list =  template.queryForList(sql, parameterMap);
		log.info(" query result size :{}",list.size());
		return list;
	}


	private <T extends BaseBean> String generateInsertSql(T entity) {
	
		List<String> columnNames = entity.getColumnNames();
		Map<String,Field> m = entity.getFieldMap();
		
		StringBuffer sql = new StringBuffer(" insert into ");
		StringBuffer param = new StringBuffer();
		sql.append(entity.getTableName()).append(" ( ");
		
		for (String column : columnNames) {
			String fieldName = m.get(column).getName();
			Object value = entity.getValue(fieldName);
			if (value == null || value.toString().trim() == "") {
				continue;
			}
			sql.append(column).append(",");
			param.append(":").append(fieldName).append(",");
		}
		
		sql.deleteCharAt(sql.length()-1);
		param.deleteCharAt(param.length()-1);
		sql.append(") values(").append(param).append(")");
		return sql.toString();
	}

	
	private  <T extends BaseBean> String generateUpdateSql(T entity) {
		List<String> columnNames = entity.getColumnNames();
		Map<String,Field> m = entity.getFieldMap();
		String tableName = entity.getTableName();
		
		StringBuffer sql = new StringBuffer(" update ");
		sql.append(tableName).append(" set ");
		
		for (String column : columnNames) {
			String fieldName = m.get(column).getName();
			Object value = entity.getValue(fieldName);
			if (value == null || value.toString().trim() == "" ) {
				continue;
			}
			sql.append(column).append("=:").append(fieldName).append(",");
		}
		
		sql.deleteCharAt(sql.length()-1);
		String pkName = entity.getPKName().toUpperCase();
		sql.append(" where ").append(pkName).append("=:").append(m.get(pkName).getName());
		return sql.toString();
	}
	
	private  <T extends BaseBean> String generateDeleteSql(T entity) {
		List<String> columnNames = entity.getColumnNames();
		Map<String,Field> m = entity.getFieldMap();
		String tableName = entity.getTableName();
		
		StringBuffer sql = new StringBuffer(" delete from  ");
		sql.append(tableName).append(" where 1=1  ");
		for (String column : columnNames) {
			String fieldName = m.get(column).getName();
			Object value = entity.getValue(fieldName);
			if (value == null || value.toString().trim() == "") {
				continue;
			}
			sql.append(" and ").append(column).append("=:").append(fieldName);
		}
		
		return sql.toString();
	}
	
	
	private  <T extends BaseBean> String generateQuerySql(T entity) {
		List<String> columnNames = entity.getColumnNames();
		Map<String,Field> m = entity.getFieldMap();
		String tableName = entity.getTableName();
		
		StringBuffer sql = new StringBuffer(" select ");
		StringBuffer whereSql = new StringBuffer(" where 1=1 ");
		for (String column : columnNames){
			sql.append(column).append(",");
			
			String fieldName = m.get(column).getName();
			Object value = entity.getValue(fieldName);
			if (value == null || value.toString().trim() == "") {
				continue;
			}
			whereSql.append(" and ").append(column).append("=:").append(fieldName);
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(" from ").append(tableName);
		sql.append(whereSql);
		return sql.toString();
	}
	
	
	private  <T extends BaseBean> String generateQuerySql(T entity,String order) {
		String sql = generateQuerySql(entity);
		if (order != null){
			sql += " order by "+order;
		}
		return sql;
	}
	
	/**
	 * 获取主键的值
	 * oracle数据库使用
	 */
	private  <T extends BaseBean> Object getPKValue(T entity) {
		String pkName = entity.getPKName();
		String tableName = entity.getTableName();
		Object pkValue = null;
		String sql;

		pkValue = entity.getValue(pkName);
		if (pkValue == null) {
			String sequence = entity.getSequenceName();
			if (StringUtils.isEmpty(sequence)) {
				sql = " select max(" + pkName + ") from " + tableName;
			} else {
				sql = " select " + sequence + ".nextval from  dual";
			}
			pkValue = template.queryForInt(sql, new HashMap());

			log.info("get the primary key: {} and the value is {}",new Object[] { sql, pkValue });
		}
		return pkValue;
	}

   
	private <T extends BaseBean> RowMapper<T> getRowMapper(T entity){
		final Class clazz = entity.getClass();
		RowMapper<T> r = new RowMapper<T>(){
			public T mapRow(ResultSet rs,int rownum)throws SQLException {
				try {
					T t = (T)clazz.newInstance();
					Map<String,Field> m = t.getFieldMap();
					for (String column : m.keySet()){
						Field f = m.get(column);
						t.setValue(f.getName(), DataObjectUtils.converObjectToType(rs.getObject(column), f.getType()));
					}
					return t;
				} catch (Exception e) {
					e.printStackTrace();
					String errmsg = "RowSet 转换成POJO时,对象创建失败:"+e.getMessage();
					log.error(errmsg);
					throw new RuntimeException(errmsg);
				} 
			}
		};
		return r;
	}
	
	
}
