package cn.arvix.vrdata.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.arvix.vrdata.been.UserVO;
import cn.arvix.vrdata.consants.ArvixDataConstants;
import cn.arvix.vrdata.util.StaticMethod;

@Service
public class AuthServiceImpl implements AuthService {
	private static final Logger log = LoggerFactory
			.getLogger(AuthServiceImpl.class);

	@Autowired
	ShareService shareService;
	@Autowired
	UserService userService;

	@Override
	public boolean checkSmsCode(HttpSession session, String code) {
		boolean isRight = false;
		if (code.equals(session
				.getAttribute(ArvixDataConstants.SMS_CODE_SESSION_NAME))) {
			isRight = true;
		}
		return isRight;
	}

	private boolean sendSmsCode(HttpSession session, String phoneNumber,
			String content) {
		boolean isRight = false;
		if (shareService.devEnv()) {
			session.setAttribute("smsCode", "123456");
			isRight = true;
		} else {
			String captchaSolution = RandomStringUtils.random(
					ArvixDataConstants.DEFAULT_LENGTH,
					ArvixDataConstants.DEFAULT_CAPTCHA_CHARS.toCharArray());
			String sendStr = content.replace(ArvixDataConstants.SMS_VAR_CHECK_CODE,
					captchaSolution);
			session.setAttribute(ArvixDataConstants.SMS_CODE_SESSION_NAME,
					captchaSolution);
			log.info("check code:" + captchaSolution +"   sms content:"+sendStr);
			// TODO send sms
			isRight = true;

		}
		return isRight;

	}

	@Override
	public Map<String, Object> register(UserVO userVO,
			HttpServletRequest request) {
		Map<String, Object> result = StaticMethod.getResult();
		boolean isRight = true;
		if (userVO.getRemember() == null) {
			isRight = false;
		} else if (!(userVO.getUsername().trim().matches("^1[0-9]{10}"))) {
			isRight = false;
		} else if (userVO.getNickname().trim() == null
				|| userVO.getNickname().length() > 100) {
			isRight = false;
		} else if (!userVO.getPassword().equals(userVO.getPasswordRepeat())) {
			isRight = false;
		} else if (userVO.getCode() == null) {
			isRight = false;
		} else if (!checkSmsCode(request.getSession(), userVO.getCode())) {
			isRight = false;
		} else if (userService.createUser(userVO) == null) {
			isRight = false;
		}
		if (isRight) {
			result.put(ArvixDataConstants.SUCCESS, true);
		}
		return result;
	}

	@Override
	public Map<String, Object> checkUsername(String username) {
		Map<String, Object> result = StaticMethod.getResult();
		if (userService.findByUsername(username) == null
				&& username.trim().matches("^1[0-9]{10}")) {
			result.put(ArvixDataConstants.SUCCESS, true);
		}
		return result;
	}

	@Override
	public Map<String, Object> sendRegisterSmsCode(String username,
			HttpServletRequest request) {
		Map<String, Object> result = StaticMethod.getResult();
		if (userService.findByUsername(username) == null) {
			boolean sendResult = sendSmsCode(request.getSession(), username,
					ArvixDataConstants.SMS_REGISTER_NOTE);
			result.put(ArvixDataConstants.SUCCESS, sendResult);
		}
		return result;
	}

	@Override
	public Map<String, Object> sendFindPwSmsCode(String username,
			HttpServletRequest request) {
		Map<String, Object> result = StaticMethod.getResult();
		if (userService.findByUsername(username) != null) {
			boolean sendResult = sendSmsCode(request.getSession(), username,
					ArvixDataConstants.SMS_FINDPW_NOTE);
			result.put(ArvixDataConstants.SUCCESS, sendResult);
		}
		return result;
	}

	@Override
	public Map<String, Object> resetPassword(UserVO userVO,
			HttpServletRequest request) {
		Map<String, Object> result = StaticMethod.getResult();
		boolean isRight = true;
		if (userService.findByUsername(userVO.getUsername()) == null) {
			isRight = false;
		} else if (userVO.getCode() == null) {
			isRight = false;
		} else if (!userVO.getPassword().equals(userVO.getPasswordRepeat())) {
			isRight = false;
		} else if (!checkSmsCode(request.getSession(), userVO.getCode())) {
			isRight = false;
		}

		if (isRight) {
			userService.changePassword(
					userService.findByUsername(userVO.getUsername()),
					userVO.getPassword());
			result.put(ArvixDataConstants.SUCCESS, true);
		}
		return result;
	}

}
