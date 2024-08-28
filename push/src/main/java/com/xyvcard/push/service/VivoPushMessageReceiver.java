package com.xyvcard.push.service;

import android.content.Context;

import com.vivo.push.sdk.OpenClientPushMessageReceiver;
import com.xyvcard.push.XyPushManager;

/**
 * @author: WYY
 * @date: 2024/8/28
 * @description：vivo 推送广播
 */
public class VivoPushMessageReceiver extends OpenClientPushMessageReceiver {

    @Override
    public void onReceiveRegId(Context context, String regId) {
        super.onReceiveRegId(context, regId);
        XyPushManager.getInstance().refreshPushToken(regId);
    }
}
