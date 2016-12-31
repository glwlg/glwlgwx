package com.glwlg.tuling.api;

/**
 * @author guoluwei
 * @version V1.0
 * @Package com.glwlg.tuling.api
 * @Description: ${todo}(用一句话描述该文件做什么)
 * @date 2016/12/29 16:00
 */
public class TLNews {

	private String article;
	private String source;
	private String icon;
	private String detailurl;

	public String getArticle() {
		return article;
	}

	public void setArticle(String article) {
		this.article = article;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getDetailurl() {
		return detailurl;
	}

	public void setDetailurl(String detailurl) {
		this.detailurl = detailurl;
	}
}
