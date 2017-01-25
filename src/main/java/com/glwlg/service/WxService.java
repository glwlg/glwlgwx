package com.glwlg.service;

import com.glwlg.utils.ActionResult;

/**
 * Created by guoluwei on 2017/1/25.
 */
public interface WxService {

    public ActionResult getUUID();

    /**
     * 显示二维码
     */
    public ActionResult showQrCode(String uuid,String path);

    /**
     * 等待登录
     */
    public ActionResult waitForLogin(String uuid);


    public ActionResult start(String uuid);



}
