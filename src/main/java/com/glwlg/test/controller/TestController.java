package com.glwlg.test.controller;

import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author guoluwei
 * @version V1.0
 * @Package com.glwlg.test.controller
 * @Description: ${todo}(用一句话描述该文件做什么)
 * @date 2016/12/30 19:45
 */
@Controller
public class TestController {
	@Autowired
	HttpServletRequest request;
	@RequestMapping("/admin")
	@ResponseBody
	public String testSpring() {

		return request.getSession().getServletContext().getRealPath("/image");


	}
}
