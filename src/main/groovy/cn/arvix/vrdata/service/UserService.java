package cn.arvix.vrdata.service;

import java.util.Map;

import cn.arvix.vrdata.been.UserVO;
import cn.arvix.vrdata.domain.User;

/**
 * 
 * @author asdtiang
 *
 */
public interface UserService {

    /**
     * 创建用户
     * @param user
     */
    public User createUser(UserVO userVO);
    User currentUser();

    public User  updateUser(User user);

    public void deleteUser(User user);


    /**
     * 修改密码
     * @param userId
     * @param newPassword
     */
    public void changePassword(User user, String newPassword);
    /**
     * 根据用户名查找用户
     * @param username
     * @return
     */
    public User findByUsername(String username);
    
    public User findById(Long id);
    /**
     * login success call ,store  dbInfo key to shiro session
     */
    void loginAction();
	 JdbcPage list(String searchStr,String roleString,Integer max,Integer offset);
	public Map<String, Object> update(Long id, String nickname, String username,
			boolean enabled, String password);
}
