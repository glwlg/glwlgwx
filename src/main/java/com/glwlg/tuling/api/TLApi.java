package com.glwlg.tuling.api;

import com.alibaba.fastjson.JSON;
import com.blade.kit.http.HttpRequest;

/**
 * @author guoluwei
 * @version V1.0
 * @Package com.glwlg.tuling.api
 * @Description: ${todo}(用一句话描述该文件做什么)
 * @date 2016/12/29 16:04
 */
public class TLApi {

	private final static String key = "82a99c7bd74248f1bf6dcfe527871ae6";
	private final static String TLurl = "http://www.tuling123.com/openapi/api";

	public TLResponse talk(TLRequest request) {
		TLResponse response=new TLResponse();
		String parm=JSON.toJSONString(request);
		HttpRequest http = HttpRequest.post(TLurl, true,
				"key", key,
				"info", request.getInfo(),
				"userid",request.getUserid()
				);
		String res = http.body();
		http.disconnect();
		response = JSON.parseObject(res, TLResponse.class);
		return response;
	}

	public static void main(String[] args) {
		TLRequest request=new TLRequest();
		TLApi api=new TLApi();
		request.setInfo("今天杭州天气怎么样");
		TLResponse response = api.talk(request);
		System.out.println(response.getText());

	}

}
