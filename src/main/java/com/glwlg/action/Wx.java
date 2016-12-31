package com.glwlg.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.glwlg.utils.ActionResult;
import com.glwlg.wx.api.WxApi;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	@ModelAttribute
	public void setReqAndRes(HttpServletRequest request, HttpServletResponse response){
		this.request = request;
		this.response = response;
		this.session = request.getSession();
	}

	@RequestMapping("/showQrCode")
	public String showQrCode() {
		ActionResult result = new ActionResult();
		String path = session.getServletContext().getRealPath("image/");
		WxApi api = new WxApi();
		String uuid = api.getUUID();
		session.setAttribute("api",api);
		request.setAttribute("haslogin",false);
		String qrCodeName="QrCode";
		if (api.showQrCode(path + qrCodeName + ".jpg")) {
			return "qrCode";
		} else {
			return showQrCode();
		}

	}
	@RequestMapping("/login")
	public void login(){
		ActionResult result = new ActionResult();
		WxApi api = (WxApi) session.getAttribute("api");
		String status=api.waitForLogin();
		if ("200".equals(status)) {
			result.setSuccess(true);
			responseJson(result);
		} else {
			result.setMsg(status);
			responseJson(result);
		}
		session.setAttribute("api",api);
	}

	@RequestMapping("/success")
	public String success(){
		start();
		return "success";
	}

	public String start() {
		ActionResult result = new ActionResult();
		Map<String, WxApi> apiMap = (HashMap<String, WxApi>) session.getAttribute("apiMap");
		if (apiMap == null) {
			apiMap = new HashMap<String, WxApi>();
		} else {

		}
		WxApi api = (WxApi) session.getAttribute("api");
				if(!api.login()){
					logger.debug("微信登录失败");
					return "fail";
				}
				String wxuin=api.getUUID();
				try {
					WxApi oldapi = apiMap.get(wxuin);
					if (oldapi != null) {
						oldapi.loginOut();
						apiMap.remove(oldapi);
					}
				} catch (Exception ignored) {

				}
				logger.debug("[*] 微信登录成功");

				if(!api.wxInit()){
					logger.debug("[*] 微信初始化失败");
					return "fail";
				}

				logger.debug("[*] 微信初始化成功");

				if(!api.wxStatusNotify()){
					logger.debug("[*] 开启状态通知失败") ;
					return "fail";
				}

				logger.debug("[*] 开启状态通知成功");

				if(!api.getContact()){
					logger.debug("[*] 获取联系人失败");
					return "fail";
				}

				logger.debug("[*] 获取联系人成功");
				logger.debug("[*] 共有"+api.getContactList().size()+"位联系人");

				// 监听消息
				api.listenMsgMode();
				session.setAttribute("api",null);
				session.setAttribute("apiMap",apiMap);
		return "start";
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
