package cn.arvix.matterport.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.arvix.matterport.domain.Resource;
import cn.arvix.matterport.repository.ResourceRepository;
/**
 * 
 * @author abel.lee
 *  2014年9月23日
 */
@Repository
public class ResourceDaoImpl implements ResourceDao {
    @Autowired
    private ResourceRepository resourceRepository;
    
    public Resource createResource(final Resource resource) {
        return resourceRepository.save(resource);
    }

    @Override
    public Resource updateResource(Resource resource) {
    	  return resourceRepository.save(resource);
    }

    public void deleteResource(Long resourceId) {
    	  resourceRepository.delete(resourceId);
    }

    @Override
    public Resource findOne(Long resourceId) {
    	 return resourceRepository.findOne(resourceId);
    }

    @Override
    public List<Resource> findAll() {
    	return resourceRepository.findAll();
    }

}
