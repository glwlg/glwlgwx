package com.glwlg.wx.api;

/**
 * @author guoluwei
 * @version V1.0
 * @Package com.glwlg.wx.api
 * @Description: ${todo}(用一句话描述该文件做什么)
 * @date 2016/12/31 15:31
 */
public class Setting {

	public static final int ISTALK=1;
	public static final int MSGHEAD=2;
	public static final int ISQUN=3;
	public static final int ISFRIEND=4;
	public static final int ISHEAD=5;

	private String msghead="#";
	private boolean isQun=false;
	private boolean isFriend=false;
	private boolean isHead=true;
	private boolean isSetMode=false;
	private boolean isTalk=true;
	public String getMsghead() {
		return msghead;
	}

	public void setMsghead(String msghead) {
		this.msghead = msghead;
	}

	public boolean isQun() {
		return isQun;
	}

	public void setQun(boolean qun) {
		isQun = qun;
	}

	public boolean isFriend() {
		return isFriend;
	}

	public void setFriend(boolean friend) {
		isFriend = friend;
	}

	public boolean isHead() {
		return isHead;
	}

	public void setHead(boolean head) {
		isHead = head;
	}

	public boolean isSetMode() {
		return isSetMode;
	}

	public void setSetMode(boolean setMode) {
		isSetMode = setMode;
	}

	public boolean isTalk() {
		return isTalk;
	}

	public void setTalk(boolean talk) {
		isTalk = talk;
	}
}
