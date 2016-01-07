package cn.arvix.matterport.service;

import java.util.List;
import java.util.Map;

public interface JpaShareService {
	/**
	 * 持久化object
	 * @param object
	 */
    void save(Object object);
    /**
	 * 删除object
	 * @param object
	 */
    void delete(Object object);
    /**
	 * 更新object
	 * @param object
	 */
    void update(Object object);
    /**
	 * 查找
     * @param <T>
     * @param <T>
	 * @param object
	 */
     <T> T find(Class<T> classType,Long id);
    /**
     * 
     * @param hql
     * @param parameters
     * @return
     */
    List<?> queryForHql(String hql, Map<String, Object> parameters);
    
    List<?> queryForHqlArg(String hql, Object... arg);
    
    Integer queryForIntHqlArg(String hql, Object... arg);
    Long queryForLongArg(String hql, Object... arg);
    
    List<?> queryForHqlMax(String hql, int max);
    
    List<?> queryForHqlMaxAndArg(String hql, int max,Object... arg);
    
    int updateForHql(String hql, Map<String, Object> parameters);
    void executeForHql(String hql, Object... arg);
    
 /**
  * 分页查询，sort写到hql里面。max offset-->sql: limit offset max
  * @param hql
  * @param countHql
  * @param max 每页显示的数据行数
  * @param offset 取结果的开始点
  * @param parameters named parameter
  * @return JdbcPage
  */
    JdbcPage queryForHql(String hql, String countHql,int max,int offset,Map<String, Object> parameters);

	void executeNativeSql(String sql);
	void executeNativeSql(String sql,Object... arg);
	
}
