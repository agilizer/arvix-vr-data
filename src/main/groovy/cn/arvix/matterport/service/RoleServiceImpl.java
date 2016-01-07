package cn.arvix.matterport.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.arvix.matterport.domain.Resource;
import cn.arvix.matterport.domain.Role;
import cn.arvix.matterport.repository.RoleRepository;

/**
 * 
 * @author asdtiang
 *
 */
@Service

public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ResourceService resourceService;

    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    public Role updateRole(Role role) {
        return roleRepository.save(role);
    }

    public void deleteRole(Long roleId) {
    	roleRepository.delete(roleId);;
    }

    @Override
    public Role findOne(Long roleId) {
        return roleRepository.findOne(roleId);
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public Set<String> findRoles(Long... roleIds) {
        Set<String> roles = new HashSet<String>();
        for(Long roleId : roleIds) {
            Role role = findOne(roleId);
            if(role != null) {
                roles.add(role.getAuthority());
            }
        }
        return roles;
    }

    @Override
    public Set<String> findPermissions(List<Role> roles) {
        Set<String> permissions = new HashSet<String>();
        for(Role role : roles) {
            for(Resource resource:role.getResources()) {
            	permissions.add(resource.getPermission());
            }
        }
        return permissions;
    }
}
