package cn.arvix.vrdata.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.arvix.vrdata.been.UserVO;
import cn.arvix.vrdata.service.AuthService;
import cn.arvix.vrdata.service.UserService;

@Controller
@RequestMapping("/auth")
public class AuthController {
	@Autowired
	AuthService authService;
	@Autowired
	UserService userService;
	@ResponseBody
	@RequestMapping("/register")
	public Map<String, Object> register(UserVO userVO,HttpServletRequest request) {
		return authService.register(userVO, request);
	}

	@ResponseBody
	@RequestMapping("/checkUsername/{username}")
	public Map<String, Object> checkUsername(@PathVariable("username") String username) {
		return authService.checkUsername(username);
	}

	@ResponseBody
	@RequestMapping("/currentUser")
	public Object currentUser() {
		return userService.currentUser();
	}

	
	/**
	 * 注册验证码 
	 * @param username 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/sendRegisterSmsCode/{username}")
	public Map<String, Object> sendRegisterSmsCode(@PathVariable("username") String username,HttpServletRequest request) {	
		return authService.sendRegisterSmsCode(username, request);
	}
	/**
	 * 重置密码
	 * @param username
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/sendFindPwSmsCode/{username}")
	public Map<String, Object> sendFindPwSmsCode(@PathVariable("username") String username,
			HttpServletRequest request) {		
		return authService.sendFindPwSmsCode(username, request);
	}

	@ResponseBody
	@RequestMapping("/resetPassword")
	public Map<String, Object> resetPassword(UserVO userVO,
			HttpServletRequest request) {	
		return authService.resetPassword(userVO, request);
	}
	
	
	@RequestMapping("/login")
	public String resetPassword(
			HttpServletRequest request) {	
		return "login";
	}


}
