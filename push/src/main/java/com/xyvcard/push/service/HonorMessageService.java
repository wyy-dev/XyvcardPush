package com.xyvcard.push.service;

import android.text.TextUtils;

import com.hihonor.push.sdk.HonorPushDataMsg;
import com.xyvcard.push.XyPushManager;

/**
 * @author: WYY
 * @date: 2024/8/27
 * @description：荣耀消息服务
 */
public class HonorMessageService extends com.hihonor.push.sdk.HonorMessageService {

    //Token发生变化时，会以onNewToken方法返回
    @Override
    public void onNewToken(String pushToken) {
        if (TextUtils.isEmpty(pushToken)) {
            return;
        }
        XyPushManager.getInstance().refreshPushToken(pushToken);
    }

    @Override
    public void onMessageReceived(HonorPushDataMsg msg) {
        // TODO: 处理收到的透传消息。
    }

}
