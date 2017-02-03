package com.glwlg.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.glwlg.service.WxService;
import com.glwlg.utils.ActionResult;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.util.*;

import static java.awt.SystemColor.info;

/**
 * @author guoluwei
 * @version V1.0
 * @Package com.glwlg.action
 * @Description: ${todo}(用一句话描述该文件做什么)
 * @date 2016/12/30 18:28
 */
@Controller public class Wx {
	@Autowired
	HttpServletRequest request;

	HttpServletResponse response;
	HttpSession session;
	protected Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	private WxService wxService;

	@ModelAttribute
	public void setReqAndRes(HttpServletRequest request, HttpServletResponse response){
		this.request = request;
		this.response = response;
		this.session = request.getSession();
	}

	@RequestMapping("/index")
	public ModelAndView index() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/index");
		ActionResult result = new ActionResult();
		String path = session.getServletContext().getRealPath("images/");
		result=wxService.getUUID();
		if (!result.isSuccess()) {
			modelAndView.addObject("success", false);
			return modelAndView;
		}
		//添加数据库之前先把信息放在session里
		String uuid= (String) result.getDataObject();

		result=wxService.showQrCode(uuid,path+uuid+".jpg");
		if (!result.isSuccess()) {
			modelAndView.addObject("success", false);
			return modelAndView;
		}

		modelAndView.addObject("uuid", uuid);
		modelAndView.addObject("success", true);
		modelAndView.addObject("qrCodePath", uuid+".jpg");

		return modelAndView;
	}

	@RequestMapping("/login")
	public void login(){
		ActionResult result = new ActionResult();
		String uuid = request.getParameter("uuid");
		result = wxService.waitForLogin(uuid);
		if (result.isSuccess()) {
			result = wxService.start(uuid);
			responseJson(result);
		}
		result.setDataObject(uuid);
		responseJson(result);
	}


	public void responseJson(Object obj) {
		PrintWriter out = null;
		try {
			String json = JSON.toJSON(obj).toString();
			logger.debug("json:" + json);
			response.setContentType("text/json; charset=UTF-8");
			out = response.getWriter();
			out.print(json);
			out.flush();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			if (null != out) {
				try {
					out.close();
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
		}
	}
}
