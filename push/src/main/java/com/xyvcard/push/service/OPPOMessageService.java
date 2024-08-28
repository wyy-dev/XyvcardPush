package com.xyvcard.push.service;

import com.heytap.msp.push.callback.ICallBackResultService;
import com.xyvcard.push.XyPushManager;

/**
 * @author: WYY
 * @date: 2024/8/28
 * @description：
 */
public class OPPOMessageService implements ICallBackResultService {
    @Override
    public void onRegister(int responseCode, String registerID, String packageName, String miniPackageName) {
        if (responseCode == 0) {
            XyPushManager.getInstance().refreshPushToken(registerID);
        }
    }

    @Override
    public void onUnRegister(int i, String s, String s1) {

    }

    @Override
    public void onSetPushTime(int i, String s) {

    }

    @Override
    public void onGetPushStatus(int i, int i1) {

    }

    @Override
    public void onGetNotificationStatus(int i, int i1) {

    }

    @Override
    public void onError(int i, String s, String s1, String s2) {

    }
}
