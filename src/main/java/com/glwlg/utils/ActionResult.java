package com.glwlg.utils;

/**
 * @author guoluwei
 * @version V1.0
 * @Package com.glwlg.utils
 * @Description: ${todo}(用一句话描述该文件做什么)
 * @date 2016/12/30 23:29
 */
public class ActionResult {
		// 操作结果标志
		private boolean success;

		// 返回信息
		private String msg;

		// service层返回对象
		private Object dataObject;

		// 消息code
		private String msgCode;

		public Object getDataObject() {
			return dataObject;
		}

		public void setDataObject(Object dataObject) {
			this.dataObject = dataObject;
		}

		public boolean isSuccess() {
			return success;
		}

		public void setSuccess(boolean success) {
			this.success = success;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}

		public ActionResult(boolean success, String msg) {
			super();
			this.success = success;
			this.msg = msg;
		}

		public ActionResult() {
			super();
		}

		public String getMsgCode() {
			return msgCode;
		}

		public void setMsgCode(String msgCode) {
			this.msgCode = msgCode;
		}

		public ActionResult toSuccess(Object dataObject) {
			this.success = true;
			this.dataObject = dataObject;
			return this;
		}

		public ActionResult toFail(String msg) {
			this.success = false;
			this.msg = msg;
			return this;
		}

		public ActionResult toFail(String msgCode, String msg) {
			this.success = false;
			this.msgCode = msgCode;
			this.msg = msg;
			return this;
		}
}
