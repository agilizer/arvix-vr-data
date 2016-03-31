package cn.arvix.vrdata.service;

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
    /**
     * login success call ,store  dbInfo key to shiro session
     */
    void loginAction();
}
