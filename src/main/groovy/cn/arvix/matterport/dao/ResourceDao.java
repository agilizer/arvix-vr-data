package cn.arvix.matterport.dao;

import java.util.List;

import cn.arvix.matterport.domain.Resource;

/**
 * 
 * @author abel.lee
 *  2014年9月23日
 */
public interface ResourceDao {

    public Resource createResource(Resource resource);
    public Resource updateResource(Resource resource);
    public void deleteResource(Long resourceId);

    Resource findOne(Long resourceId);
    List<Resource> findAll();

}
