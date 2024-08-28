package com.xyvcard.push;

import android.util.Log;

import com.heytap.msp.push.HeytapPushManager;
import com.hihonor.push.sdk.HonorPushCallback;
import com.hihonor.push.sdk.HonorPushClient;
import com.huawei.hms.push.HmsMessaging;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xyvcard.push.common.MyContext;
import com.xyvcard.push.common.PushConstants;
import com.xyvcard.push.service.OPPOMessageService;
import com.xyvcard.push.utils.BrandUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: WYY
 * @date: 2024/8/26
 * @description：推送入口
 */
class XyPushManagerImpl extends XyPushManager {
    private static final String TAG = "XyPushManagerImpl";

    private final List<OnRefreshPushTokenListener> refreshPushTokenListeners = new ArrayList<>();


    protected XyPushManagerImpl() {
    }

    @Override
    public void registerPush() {
        if (getContext() == null || getContext() == null) return;
        if (BrandUtils.isBrandHuawei()) {
            new Thread(() -> initHuaWeiPush()).start();
        } else if (BrandUtils.isBrandXiaoMi()) {
            initXiaomiPush();
        } else if (BrandUtils.isBrandOppo()) {
//                initOPPO();
        } else if (BrandUtils.isBrandVivo()) {
//                initVIVO();
        } else if (BrandUtils.isBrandHonor()) {
            initHonorPush();
        }
    }

    /**
     * 初始化华为推送
     */
    private void initHuaWeiPush() {
        HmsMessaging.getInstance(getContext()).setAutoInitEnabled(true);
    }

    private void initXiaomiPush() {
        MiPushClient.registerPush(getContext(), PushConstants.xiaoMiAppID, PushConstants.xiaoMiAppKey);
    }

    private void initOppoPush() {
        try {
            HeytapPushManager.init(getContext(), false);
            if (HeytapPushManager.isSupportPush(getContext())) {
                HeytapPushManager.register(getContext(), PushConstants.oppoAppKey, PushConstants.oppoAppSecret, new OPPOMessageService());
                // OPPO 手机默认关闭通知，需要申请
                HeytapPushManager.requestNotificationPermission();
            }
        } catch (Exception e) {
            Log.e(TAG, "initOppoPush: ", e);
        }
    }

    private void initHonorPush() {
        // 荣耀推送
        boolean isSupport = HonorPushClient.getInstance().checkSupportHonorPush(getContext().getApplicationContext());
        if (!isSupport) {
           return;
        }
        HonorPushClient.getInstance().init(getContext().getApplicationContext(), true);
        // 获取PushToken
        HonorPushClient.getInstance().getPushToken(new HonorPushCallback<String>() {
            @Override
            public void onSuccess(String pushToken) {
                refreshPushToken(pushToken);
            }

            @Override
            public void onFailure(int errorCode, String errorString) {
            }
        });
    }

    @Override
    public void addOnRefreshPushTokenListener(OnRefreshPushTokenListener listener) {
        if (listener == null) {
            return;
        }
        MyContext.getInstance().runOnMainThread(() -> {
            if (refreshPushTokenListeners.contains(listener)) {
                return;
            }
            refreshPushTokenListeners.add(listener);
        });
    }

    @Override
    public void removeOnRefreshPushTokenListener(OnRefreshPushTokenListener listener) {
        if (listener == null) {
            return;
        }
        MyContext.getInstance().runOnMainThread(() -> {
            refreshPushTokenListeners.remove(listener);
        });
    }

    @Override
    public void refreshPushToken(String token) {
        MyContext.getInstance().runOnMainThread(() -> {
            for (OnRefreshPushTokenListener listener : refreshPushTokenListeners) {
                listener.onRefreshToken(token, BrandUtils.getDeviceType());
            }
        });
    }


}
