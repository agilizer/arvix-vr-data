package cn.arvix.vrdata.service;

import java.util.List;

import cn.arvix.vrdata.domain.Resource;


public interface ResourceService {


    public Resource createResource(Resource resource);
    public Resource updateResource(Resource resource);
    public void deleteResource(Long resourceId);

    Resource findOne(Long resourceId);
    List<Resource> findAll();

}
