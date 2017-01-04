package com.glwlg.tuling.api;

import java.util.ArrayList;

/**
 * @author guoluwei
 * @version V1.0
 * @Package com.glwlg.tuling.api
 * @Description: ${todo}(用一句话描述该文件做什么)
 * @date 2016/12/29 15:58
 */
public class TLResponse {

	private String code;
	private String text;
	private String url;
	private ArrayList<TLNews> list;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public ArrayList<TLNews> getList() {
		return list;
	}

	public void setList(ArrayList<TLNews> list) {
		this.list = list;
	}

	public String getMsg(){
		if (url != null) {
			return text+"\r\n"+url;
		}
		return text;
	}
}
