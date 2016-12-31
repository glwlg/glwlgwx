package com.glwlg.wx.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blade.kit.DateKit;
import com.blade.kit.StringKit;
import com.blade.kit.http.HttpRequest;
import com.glwlg.tuling.api.TLApi;
import com.glwlg.tuling.api.TLRequest;
import com.glwlg.tuling.api.TLResponse;
import com.glwlg.utils.CookieUtil;
import org.apache.log4j.Logger;
import com.glwlg.utils.Matchers;


import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Hello world!
 *
 */
public class WxApi {

	private static final Logger LOGGER = Logger.getLogger(WxApi.class);

	private String uuid;
	private int tip = 0;
	private String base_uri, redirect_uri, webpush_url = "https://webpush.wx.qq.com/cgi-bin/mmwebwx-bin";

	private String skey, synckey, wxsid, wxuin, pass_ticket, deviceId = "e" + System.currentTimeMillis();

	private String cookie;

	private JSONObject SyncKey, User, BaseRequest;

	// 微信联系人列表，可聊天的联系人列表
	private JSONArray MemberList, ContactList;

	// 微信特殊账号
	private List<String> SpecialUsers = Arrays.asList("newsapp", "fmessage", "filehelper", "weibo", "qqmail", "fmessage", "tmessage", "qmessage", "qqsync", "floatbottle", "lbsapp", "shakeapp", "medianote", "qqfriend", "readerapp", "blogapp", "facebookapp", "masssendapp", "meishiapp", "feedsapp", "voip", "blogappweixin", "weixin", "brandsessionholder", "weixinreminder", "wxid_novlwrv3lqwv11", "gh_22b87fa7cb3c", "officialaccounts", "notification_messages", "wxid_novlwrv3lqwv11", "gh_22b87fa7cb3c", "wxitil", "userexperience_alarm", "notification_messages");

	public WxApi() {
		System.setProperty("jsse.enableSNIExtension", "false");
	}

	private Setting setting;

	/**
	 * 获取UUID
	 */
	public String getUUID() {
		String url = "https://login.weixin.qq.com/jslogin";
		HttpRequest request = HttpRequest.get(url, true,
				"appid", "wx782c26e4c19acffb",
				"fun", "new",
				"lang", "zh_CN",
				"_" , System.currentTimeMillis());

		LOGGER.debug(request);

		String res = request.body();
		request.disconnect();

		if(StringKit.isNotBlank(res)){
			String code = Matchers.match("window.QRLogin.code = (\\d+);", res);
			if(null != code){
				if(code.equals("200")){
					this.uuid = Matchers.match("window.QRLogin.uuid = \"(.*)\";", res);
					return this.uuid;
				} else {
					LOGGER.error("错误的状态码:"+code);
				}
			}
		}
		return null;
	}

	/**
	 * 显示二维码
	 */
	public boolean showQrCode(String path) {

		String url = "https://login.weixin.qq.com/qrcode/" + this.uuid;
		final File output = new File(path);
		if (output.exists() && output.isFile()) {
			output.delete();
		}
		HttpRequest.post(url, true,
				"t", "webwx",
				"_" , System.currentTimeMillis())
				.receive(output);
		return output.exists() && output.isFile();
	}

	/**
	 * 等待登录
	 */
	public String waitForLogin(){
		this.tip = 1;
		String url = "https://login.weixin.qq.com/cgi-bin/mmwebwx-bin/login";
		HttpRequest request = HttpRequest.get(url, true,
				"tip", this.tip,
				"uuid", this.uuid,
				"_" , System.currentTimeMillis());

		LOGGER.debug("" + request.toString());

		String res = request.body();
		request.disconnect();

		if(null == res){
			LOGGER.debug("扫描二维码验证失败");
			return "扫描二维码验证失败";
		}

		String code = Matchers.match("window.code=(\\d+);", res);
		if(null == code){
			LOGGER.debug("扫描二维码验证失败");
			return "扫描二维码验证失败";
		} else {
			if(code.equals("201")){
				LOGGER.debug("成功扫描,请在手机上点击确认以登录");
				tip = 0;
				return "成功扫描,请在手机上点击确认以登录";
			} else if(code.equals("200")){
				LOGGER.debug("正在登录...");
				String pm = Matchers.match("window.redirect_uri=\"(\\S+?)\";", res);
				this.redirect_uri = pm + "&fun=new";
				LOGGER.debug("redirect_uri=%s"+this.redirect_uri);
				this.base_uri = this.redirect_uri.substring(0, this.redirect_uri.lastIndexOf("/"));
				LOGGER.debug("base_uri="+this.base_uri);
			} else if(code.equals("408")){
				LOGGER.debug("登录超时");
				return "登录超时";
			} else {
				LOGGER.debug("扫描code="+code);
			}
		}
		return code;
	}


	/**
	 * 登录
	 */
	public boolean login(){

		HttpRequest request = HttpRequest.get(this.redirect_uri);

		LOGGER.debug("" + request);

		String res = request.body();
		this.cookie = CookieUtil.getCookie(request);

		request.disconnect();

		if(StringKit.isBlank(res)){
			return false;
		}

		this.skey = Matchers.match("<skey>(\\S+)</skey>", res);
		this.wxsid = Matchers.match("<wxsid>(\\S+)</wxsid>", res);
		this.wxuin = Matchers.match("<wxuin>(\\S+)</wxuin>", res);
		this.pass_ticket = Matchers.match("<pass_ticket>(\\S+)</pass_ticket>", res);

		LOGGER.debug("skey"+ this.skey);
		LOGGER.debug("wxsid"+ this.wxsid);
		LOGGER.debug("wxuin"+ this.wxuin);
		LOGGER.debug("pass_ticket"+ this.pass_ticket);

		this.BaseRequest = new JSONObject();
		BaseRequest.put("Uin", this.wxuin);
		BaseRequest.put("Sid", this.wxsid);
		BaseRequest.put("Skey", this.skey);
		BaseRequest.put("DeviceID", this.deviceId);

		setting=new Setting();
		return true;
	}

	/**
	 * 微信初始化
	 */
	public boolean wxInit(){

		String url = this.base_uri + "/webwxinit?r=" + System.currentTimeMillis() + "&pass_ticket=" + this.pass_ticket +
				"&skey=" + this.skey;

		JSONObject body = new JSONObject();
		body.put("BaseRequest", this.BaseRequest);

		HttpRequest request = HttpRequest.post(url)
				.header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", this.cookie)
				.send(body.toString());

		LOGGER.debug("" + request);
		String res = request.body();
		request.disconnect();

		if(StringKit.isBlank(res)){
			return false;
		}

		try {
			JSONObject jsonObject = JSON.parseObject(res);
			if(null != jsonObject){
				JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
				if(null != BaseResponse){
					int ret = BaseResponse.getIntValue("Ret");
					if(ret == 0){
						this.SyncKey = jsonObject.getJSONObject("SyncKey");
						this.User = jsonObject.getJSONObject("User");

						StringBuilder synckey = new StringBuilder();

						JSONArray list = SyncKey.getJSONArray("List");
						for(int i=0, len=list.size(); i<len; i++){
							JSONObject item = list.getJSONObject(i);
							synckey.append("|").append(item.getIntValue("Key")).append("_").append(item.getIntValue("Val"));
						}

						this.synckey = synckey.substring(1);

						return true;
					}
				}
			}
		} catch (Exception ignored) {
		}
		return false;
	}

	/**
	 * 微信状态通知
	 */
	public boolean wxStatusNotify (){

		String url = this.base_uri + "/webwxstatusnotify?lang=zh_CN&pass_ticket=" + this.pass_ticket;

		JSONObject body = new JSONObject();
		body.put("BaseRequest", BaseRequest);
		body.put("Code", 3);
		body.put("FromUserName", this.User.getString("UserName"));
		body.put("ToUserName", this.User.getString("UserName"));
		body.put("ClientMsgId", System.currentTimeMillis());

		HttpRequest request = HttpRequest.post(url)
				.header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", this.cookie)
				.send(body.toString());

		LOGGER.debug("" + request);
		String res = request.body();
		request.disconnect();

		if(StringKit.isBlank(res)){
			return false;
		}

		try {
			JSONObject jsonObject = JSON.parseObject(res);
			JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
			if(null != BaseResponse){
				int ret = BaseResponse.getIntValue("Ret");
				return ret == 0;
			}
		} catch (Exception ignored) {
		}
		return false;
	}

	/**
	 * 获取联系人
	 */
	public boolean getContact(){

		String url = this.base_uri + "/webwxgetcontact?pass_ticket=" + this.pass_ticket + "&skey=" + this.skey + "&r=" + System.currentTimeMillis();

		JSONObject body = new JSONObject();
		body.put("BaseRequest", BaseRequest);

		HttpRequest request = HttpRequest.post(url)
				.header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", this.cookie)
				.send(body.toString());

		LOGGER.debug("" + request);
		String res = request.body();
		request.disconnect();

		if(StringKit.isBlank(res)){
			return false;
		}

		try {
			JSONObject jsonObject = JSON.parseObject(res);
			JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
			if(null != BaseResponse){
				int ret = BaseResponse.getIntValue("Ret");
				if(ret == 0){
					this.MemberList = jsonObject.getJSONArray("MemberList");
					this.ContactList = new JSONArray();
					if(null != MemberList){
						for(int i=0, len=MemberList.size(); i<len; i++){
							JSONObject contact = this.MemberList.getJSONObject(i);
							//公众号/服务号
							if(contact.getIntValue("VerifyFlag") == 8){
								continue;
							}
							//特殊联系人
							if(SpecialUsers.contains(contact.getString("UserName"))){
								continue;
							}
							//群聊
							if(contact.getString("UserName").contains("@@")){
								continue;
							}
							//自己
							if(contact.getString("UserName").equals(this.User.getString("UserName"))){
								continue;
							}
							ContactList.add(contact);
						}
						return true;
					}
				}
			}
		} catch (Exception ignored) {
		}
		return false;
	}

	/**
	 * 消息检查
	 */
	private int[] syncCheck(){

		int[] arr = new int[2];

		String url = this.webpush_url + "/synccheck";

		HttpRequest request = HttpRequest.get(url, true,
				"r", System.currentTimeMillis() + StringKit.getRandomNumber(5),
				"skey", this.skey,
				"uin", this.wxuin,
				"sid", this.wxsid,
				"deviceid", this.deviceId,
				"synckey", this.synckey,
				"_", System.currentTimeMillis())
				.header("Cookie", this.cookie);

		LOGGER.debug("" + request);
		String res = request.body();
		request.disconnect();

		if(StringKit.isBlank(res)){
			return arr;
		}

		String retcode = Matchers.match("retcode:\"(\\d+)\",", res);
		String selector = Matchers.match("selector:\"(\\d+)\"}", res);
		if(null != retcode && null != selector){
			arr[0] = Integer.parseInt(retcode);
			arr[1] = Integer.parseInt(selector);
			return arr;
		}
		return arr;
	}

	private void webwxsendmsg(String content, String to) {

		String url = this.base_uri + "/webwxsendmsg?lang=zh_CN&pass_ticket=" + this.pass_ticket;

		JSONObject body = new JSONObject();

		String clientMsgId = System.currentTimeMillis() + StringKit.getRandomNumber(5);
		JSONObject Msg = new JSONObject();
		Msg.put("Type", 1);
		Msg.put("Content", content);
		Msg.put("FromUserName", User.getString("UserName"));
		Msg.put("ToUserName", to);
		Msg.put("LocalID", clientMsgId);
		Msg.put("ClientMsgId", clientMsgId);

		body.put("BaseRequest", this.BaseRequest);
		body.put("Msg", Msg);

		HttpRequest request = HttpRequest.post(url)
				.header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", this.cookie)
				.send(body.toString());

		LOGGER.debug("" + request);
		request.body();
		request.disconnect();
	}

	/**
	 * 获取最新消息
	 */
	private JSONObject webwxsync(){

		String url = this.base_uri + "/webwxsync?lang=zh_CN&pass_ticket=" + this.pass_ticket
				+ "&skey=" + this.skey + "&sid=" + this.wxsid + "&r=" + System.currentTimeMillis();

		JSONObject body = new JSONObject();
		body.put("BaseRequest", BaseRequest);
		body.put("SyncKey", this.SyncKey);
		body.put("rr", System.currentTimeMillis());

		HttpRequest request = HttpRequest.post(url)
				.header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", this.cookie)
				.send(body.toString());

		LOGGER.debug("" + request);
		String res = request.body();
		request.disconnect();

		if(StringKit.isBlank(res)){
			return null;
		}

		JSONObject jsonObject = JSON.parseObject(res);
		JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
		if(null != BaseResponse){
			int ret = BaseResponse.getIntValue("Ret");
			if(ret == 0){
				this.SyncKey = jsonObject.getJSONObject("SyncKey");

				StringBuilder synckey = new StringBuilder();
				JSONArray list = SyncKey.getJSONArray("List");
				for(int i=0, len=list.size(); i<len; i++){
					JSONObject item = list.getJSONObject(i);
					synckey.append("|").append(item.getIntValue("Key")).append("_").append(item.getIntValue("Val"));
				}
				this.synckey = synckey.substring(1);
			}
		}
		return jsonObject;
	}

	/**
	 * 处理最新消息
	 */
	private void handleMsg(JSONObject data){
		if(null == data){
			return;
		}

		JSONArray AddMsgList = data.getJSONArray("AddMsgList");

		for(int i=0,len=AddMsgList.size(); i<len; i++){
			LOGGER.debug("你有新的消息，请注意查收");
			JSONObject msg = AddMsgList.getJSONObject(i);
			int msgType = msg.getIntValue("MsgType");
			String name = getUserRemarkName(msg.getString("FromUserName"));
			String content = msg.getString("Content");

			if(msgType == 51){
				LOGGER.debug("成功截获微信初始化消息");
			} else if(msgType == 1){
				String fromUserName=msg.getString("FromUserName");
				if(!(SpecialUsers.contains(msg.getString("ToUserName"))||fromUserName.equals(User.getString("UserName")))) {
					if (fromUserName.contains("@@")) { //群消息
						qunMsg(content, name, fromUserName);
					} else { //个人消息
						friendMsg(content, name, fromUserName);
					}
				}
			} else if(msgType == 3){
//				webwxsendmsg("我还不支持图片呢", msg.getString("FromUserName"));
				LOGGER.debug(name + " 给你发送了一张图片:");
			} else if(msgType == 34){
				LOGGER.debug(name + " 给你发送了一张语音:");
//				webwxsendmsg("我还不支持语音呢", msg.getString("FromUserName"));
			} else if(msgType == 42){
				LOGGER.debug(name + " 给你发送了一张名片:");
			} else if(msgType == 47){
				LOGGER.debug(name + " 给你发送了一张表情:");
//				webwxsendmsg("发什么表情", msg.getString("FromUserName"));
			}
		}
	}

	public void loginOut() {
		String url=base_uri+"/webwxlogout";
		HttpRequest request = HttpRequest.post(url, true,
				"redirect", 1,
				"type",1,
				"skey", this.skey
				)
				.header("Cookie", this.cookie);
		request.disconnect();
	}

	private void qunMsg(String content,String name,String fromUserName) {
		String[] peopleContent = content.split(":<br/>");
		content=peopleContent[1];
		LOGGER.debug("|" + name + "| " + peopleContent[0] + ":\n" + content.replace("<br/>", "\n"));
		if ("!".equals(content.substring(0, 1))||"！".equals(content.substring(0, 1))){
			if("设置".equals(content.substring(1))){
				setting.setSetMode(true);
				webwxsendmsg(setMsg(), fromUserName);
				return;
			}
			if("设置完成".equals(content.substring(1))){
				setting.setSetMode(false);
				webwxsendmsg(setMsg(), fromUserName);
				return;
			}
		}
		if (setting.isSetMode()) {
			if (setMode(content)) {
				webwxsendmsg(setMsg(), fromUserName);
			} else {
				webwxsendmsg("输入有误", fromUserName);
			}
		}else if (setting.isTalk()&&setting.isQun()&&(!setting.isHead()||setting.getMsghead().equals(content.substring(0,1)))) {
			String ans = tuling(content.substring(1), peopleContent[0]); //以发送人做userid
			webwxsendmsg(ans, fromUserName);
			LOGGER.debug("自动回复 " + ans);
		}
	}

	private void friendMsg(String content,String name,String fromUserName) {
		if ("!".equals(content.substring(0, 1))||"！".equals(content.substring(0, 1))){
			if("设置".equals(content.substring(1))){
				setting.setSetMode(true);
				webwxsendmsg(setMsg(), fromUserName);
				return;
			}
			if("设置完成".equals(content.substring(1))){
				setting.setSetMode(false);
				webwxsendmsg(setMsg(), fromUserName);
				return;
			}
		}
		if (setting.isSetMode()) {
			if (setMode(content)) {
				webwxsendmsg(setMsg(), fromUserName);
			} else {
				webwxsendmsg("输入有误", fromUserName);
			}
		}else if (setting.isTalk()&&setting.isFriend()&&(!setting.isHead()||setting.getMsghead().equals(content.substring(0,1)))) {
			String ans = tuling(content.substring(1),fromUserName);
			webwxsendmsg(ans, fromUserName);
			LOGGER.debug("自动回复 " + ans);
		}
		LOGGER.debug(name + ": " + content);
	}

	private String setMsg(){
		String set="1:"+(setting.isTalk()?"自动回复开启":"自动回复关闭");
		set += "\r\n";
		set += "2:" + ("对话前缀:" + setting.getMsghead());
		set += "\r\n";
		set += "3:" + (setting.isQun() ? "回复群消息开启" : "回复群消息关闭");
		set += "\r\n";
		set += "4:" + (setting.isFriend() ? "回复好友消息开启" : "回复好友消息关闭");
		set += "\r\n";
		set += "5:" + (setting.isHead() ? "对话前缀开启" : "对话前缀关闭");
		return set;
	}

	private boolean setMode(String content){
		String[] set = content.split(":");
		try {
			int setNum=Integer.valueOf(set[0]);
			switch (setNum) {
				case Setting.ISTALK:
					setting.setTalk(!setting.isTalk());
					break;
				case Setting.MSGHEAD:
					setting.setMsghead(set[1]);
					break;
				case Setting.ISQUN:
					setting.setQun(!setting.isQun());
					break;
				case Setting.ISFRIEND:
					setting.setFriend(!setting.isFriend());
					break;
				case Setting.ISHEAD:
					setting.setHead(!setting.isHead());
					break;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private String tuling(String msg,String userid) {
		TLApi api = new TLApi();
		TLRequest request=new TLRequest();
		request.setInfo(msg);
		request.setUserid(userid);
		TLResponse response=api.talk(request);
		return response.getText();
	}

	private String getUserRemarkName(String id) {
		String name = "这个人物名字未知";
		for(int i=0, len=MemberList.size(); i<len; i++){
			JSONObject member = this.MemberList.getJSONObject(i);
			if(member.getString("UserName").equals(id)){
				if(StringKit.isNotBlank(member.getString("RemarkName"))){
					name = member.getString("RemarkName");
				} else {
					name = member.getString("NickName");
				}
				return name;
			}
		}
		return name;
	}

//	private void threadStop(String name) {
//		ThreadGroup group = Thread.currentThread().getThreadGroup();
//		ThreadGroup topGroup = group;
//    /* 遍历线程组树，获取根线程组 */
//		while ( group != null )
//		{
//			topGroup    = group;
//			group        = group.getParent();
//		}
//    /* 激活的线程数加倍 */
//		int estimatedSize = topGroup.activeCount() * 2;
//		Thread[] slackList = new Thread[estimatedSize];
//    /* 获取根线程组的所有线程 */
//		int actualSize = topGroup.enumerate( slackList );
//    /* copy into a list that is the exact size */
//		Thread[] list = new Thread[actualSize];
//		System.arraycopy( slackList, 0, list, 0, actualSize );
//		for (Thread thread : list) {
//			if (name.equals(thread.getName())) {
//				thread.stop();
//			}
//		}
//	}
	public void listenMsgMode(){
//		threadStop(wxuin);//如果已经登陆,先结束进程
		new Thread(new Runnable() {
			@Override
			public void run() {
				LOGGER.debug("进入消息监听模式 ...");
				while(true){
					int[] arr = syncCheck();
					LOGGER.debug("retcode="+arr[0]+",selector="+arr[1]);
					if(arr[0] >= 1100){
						arr = syncCheck();
					}

					if(arr[0] == 0){
						JSONObject data;
						switch (arr[1]) {
							case 0:
								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								break;
							case 2:
								data = webwxsync();
								handleMsg(data);
								break;
							case 3:
								LOGGER.debug("状态3");
								break;
							case 6:
								data = webwxsync();
								handleMsg(data);
								break;
							case 7:
								webwxsync();
								break;
							default:
								break;
						}
					} else {
						break;
//						try {
//							Thread.sleep(1000);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
					}
				}
			}
		}, wxuin).start();
	}

	public JSONArray getContactList() {
		return ContactList;
	}


	public static void main(String[] args) throws InterruptedException {
		WxApi app = new WxApi();
		String uuid = app.getUUID();
		if(null == uuid){
			LOGGER.debug("uuid获取失败");
		} else {
			LOGGER.debug("获取到uuid为 "+ app.uuid);
			app.showQrCode("D:/temp.jpg");
			while(!app.waitForLogin().equals("200")){
				Thread.sleep(2000);
			}

			if(!app.login()){
				LOGGER.debug("微信登录失败");
				return;
			}

			LOGGER.debug("微信登录成功");

			if(!app.wxInit()){
				LOGGER.debug("微信初始化失败");
				return;
			}

			LOGGER.debug("微信初始化成功");

			if(!app.wxStatusNotify()){
				LOGGER.debug("开启状态通知失败") ;
				return;
			}

			LOGGER.debug("开启状态通知成功");

			if(!app.getContact()){
				LOGGER.debug("获取联系人失败");
				return;
			}

			LOGGER.debug("获取联系人成功");
			LOGGER.debug("共有"+app.ContactList.size()+"位联系人");

			// 监听消息
			app.listenMsgMode();

		}
	}

}
