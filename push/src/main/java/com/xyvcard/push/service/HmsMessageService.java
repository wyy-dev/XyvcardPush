package com.xyvcard.push.service;

import android.text.TextUtils;

import com.xyvcard.push.XyPushManager;

/**
 * @author: WYY
 * @date: 2024/8/27
 * @description：华为推送服务
 */
public class HmsMessageService extends com.huawei.hms.push.HmsMessageService {

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        if (TextUtils.isEmpty(token)) {
            return;
        }
        XyPushManager.getInstance().refreshPushToken(token);
    }
}
