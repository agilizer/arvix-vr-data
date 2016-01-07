package cn.arvix.matterport.service;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class JpaShareServiceImpl implements JpaShareService {
	private static final Logger log = LoggerFactory
			.getLogger(JpaShareServiceImpl.class);
	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public void save(Object object) {
		try {
			entityManager.persist(object);
		} catch (Exception e) {
			log.error("save object error!",e);
		}
	}

	@Transactional
	public void delete(Object object) {
		entityManager.remove(entityManager.merge(object));
	}

	@Transactional
	public void update(Object object) {
		entityManager.merge(object);

	}

	public <T> T find(Class<T> classType, Long id) {
		return entityManager.find(classType, id);
	}

	public List<?> queryForHql(String hql, Map<String, Object> parameters) {
		Query query = entityManager.createQuery(hql);
		if(parameters!=null&&parameters.size()>0){
			for (Entry<String, Object> entry : parameters.entrySet()) {
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}
        return query.getResultList();
	}

	@Override
	public JdbcPage queryForHql(String hql, String countHql, int max,
			int offset, Map<String, Object> parameters) {
		JdbcPage jdbcPage = new JdbcPage();
		Query countQuery = entityManager.createQuery(countHql);
		Query query = entityManager.createQuery(hql);
		if(parameters!=null&&parameters.size()>0){
			for (Entry<String, Object> entry : parameters.entrySet()) {
				countQuery.setParameter(entry.getKey(), entry.getValue());
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}
		long sumItem = (long)countQuery.getResultList().get(0);
		jdbcPage.setSumItem(sumItem);
		jdbcPage.setMax(max);
		jdbcPage.setOffset(offset);
		query.setFirstResult(offset);
		query.setMaxResults(max);
		jdbcPage.setPageItems(query.getResultList());
		long temp = sumItem/max;
		jdbcPage.setPagesAvailable(sumItem%max==0?temp:(temp+1));
		if(offset>=max){
			jdbcPage.setPageNumber(offset/max);
		}else{
			jdbcPage.setPageNumber(0);
		}
		return jdbcPage;
	}

	@Transactional
	@Override
	public void executeNativeSql(String sql) {
		Query query = entityManager.createNativeQuery(sql);
		query.executeUpdate();
	}

	@Transactional
	@Override
	public void executeNativeSql(String sql, Object... arg) {
		Query query = entityManager.createNativeQuery(sql);
		int position = 1;
		for(Object obj:arg){
			query.setParameter(position++, obj);
		}
		query.executeUpdate();
	}

	@Transactional
	@Override
	public int updateForHql(String hql, Map<String, Object> parameters) {
		Query query = entityManager.createQuery(hql);
		if(parameters!=null&&parameters.size()>0){
			for (Entry<String, Object> entry : parameters.entrySet()) {
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}
		return query.executeUpdate();
	}
	@Transactional
	@Override
	public void executeForHql(String hql, Object... arg) {
		Query query = entityManager.createQuery(hql);
		int position = 1;
		for(Object obj:arg){
			query.setParameter(position++, obj);
		}
		query.executeUpdate();
		
	}
	@Transactional
	@Override
	public List<?> queryForHqlArg(String hql, Object... arg) {
		Query query = entityManager.createQuery(hql);
		int position = 1;
		if(null!=arg&&arg.length>0){
			for(Object obj:arg){
				query.setParameter(position++, obj);
			}
		}
		
		return query.getResultList();
	}

	@Override
	public List<?> queryForHqlMax(String hql, int max) {
		Query query = entityManager.createQuery(hql);
		query.setMaxResults(max);
		return query.getResultList();
	}

	@Override
	public List<?> queryForHqlMaxAndArg(String hql, int max, Object... arg) {
		Query query = entityManager.createQuery(hql);
		int position = 1;
		for(Object obj:arg){
			query.setParameter(position++, obj);
		}
		query.setMaxResults(max);
		return query.getResultList();
	}

	@Override
	public Integer queryForIntHqlArg(String hql, Object... arg) {
		List<?> list = this.queryForHqlArg(hql, arg);
		Integer result = null;
		if(list.size()>0){
			result = (Integer) list.get(0);
		}
		return result;
	}

	@Override
	public Long queryForLongArg(String hql, Object... arg) {
		List<?> list = this.queryForHqlArg(hql, arg);
		Long result = null;
		if(list.size()>0){
			result = (Long) list.get(0);
		}
		return result;
	}
}