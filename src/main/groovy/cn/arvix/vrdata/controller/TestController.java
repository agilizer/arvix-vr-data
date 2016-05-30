package cn.arvix.vrdata.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public class TestController {

	@RequestMapping("/websocket")
	public String index(Model model) {
		return "admin/syncLog";
	}
}
