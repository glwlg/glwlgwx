package com.glwlg.service;

import com.glwlg.utils.ActionResult;
import com.glwlg.wx.api.WxApi;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

/**
 * Created by guoluwei on 2017/1/25.
 */
@Repository
public class WxServiceImpl implements WxService{

    protected Logger logger = Logger.getLogger(this.getClass());

    public ActionResult start(String uuid) {
        ActionResult result = new ActionResult();
        result.setSuccess(false);
        WxApi api = new WxApi(uuid);
        if(!api.login()){
            logger.debug("微信登录失败");
            result.toFail("微信登录失败");
            return result;
        }

        logger.debug("[*] 微信登录成功");

        if(!api.wxInit()){
            logger.debug("[*] 微信初始化失败");
            result.toFail("微信初始化失败");
            return result;
        }

        logger.debug("[*] 微信初始化成功");

        if(!api.wxStatusNotify()){
            logger.debug("[*] 开启状态通知失败") ;
            result.toFail("开启状态通知失败");
            return result;
        }

        logger.debug("[*] 开启状态通知成功");

        if(!api.getContact()){
            logger.debug("[*] 获取联系人失败");
            result.toFail("获取联系人失败");
            return result;
        }

        logger.debug("[*] 获取联系人成功");
        logger.debug("[*] 共有"+api.getContactList().size()+"位联系人");

        // 监听消息
        api.listenMsgMode();
        result.setMsg("微信登录成功");
        result.setSuccess(true);
        return result;
    }

    @Override
    public ActionResult getUUID() {
        WxApi api=new WxApi();
        ActionResult result = new ActionResult();
        String uuid = api.getUUID();
        if (StringUtils.isNotEmpty(uuid)) {
            result.setSuccess(true);
            result.setDataObject(api.getUUID());
        }
        return result;
    }

    @Override
    public ActionResult showQrCode(String uuid,String path) {
        ActionResult result = new ActionResult();
        WxApi api = new WxApi(uuid);
        if (!api.showQrCode(path)) {
            result.toFail("二维码获取失败");
            return result;
        }
        result.setSuccess(true);
        return result;
    }

    @Override
    public ActionResult waitForLogin(String uuid) {
        ActionResult result = new ActionResult();
        WxApi api = new WxApi(uuid);
        String status = api.waitForLogin();
        if ("200".equals(status)) {
            result.setSuccess(true);
        } else {
            result.setSuccess(false);
            result.setMsg(status);
        }
        return result;
    }



}
