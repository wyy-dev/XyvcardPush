package com.xyvcard.push;

import android.text.TextUtils;
import android.util.Log;

import com.heytap.msp.push.HeytapPushManager;
import com.hihonor.push.sdk.HonorPushCallback;
import com.hihonor.push.sdk.HonorPushClient;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;
import com.vivo.push.IPushActionListener;
import com.vivo.push.PushClient;
import com.vivo.push.listener.IPushQueryActionListener;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xyvcard.push.common.MyContext;
import com.xyvcard.push.common.PushConstants;
import com.xyvcard.push.service.OPPOMessageService;
import com.xyvcard.push.utils.BadgeUtils;
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
        Log.d(TAG, "registerPush: 1");
        if (BrandUtils.isBrandHuawei()) {
            new Thread(() -> initHuaWeiPush()).start();
        } else if (BrandUtils.isBrandXiaoMi()) {
            initXiaomiPush();
        } else if (BrandUtils.isBrandOppo()) {
            initOppoPush();
        } else if (BrandUtils.isBrandVivo()) {
            Log.d(TAG, "registerPush: vivo");
            initVIVOPush();
        } else if (BrandUtils.isBrandHonor()) {
            initHonorPush();
        }
    }

    @Override
    public void updateBadge(int number) {
        BadgeUtils.setBadge(getContext(), number);
    }

    /**
     * 初始化华为推送
     */
    private void initHuaWeiPush() {
        try {
            String token = HmsInstanceId.getInstance(getContext()).getToken(PushConstants.huaweiAPPID, PushConstants.huaweiTokenScope);
            // 判断token是否为空
            if (!TextUtils.isEmpty(token)) {
                Log.e("华为推送token", token);
                refreshPushToken(token);
            }
        } catch (ApiException e) {
            Log.e(TAG, "initHuaWeiPush: ", e);
            refreshPushToken("");
        }
    }

    /**
     * 初始化消息推送
     */
    private void initXiaomiPush() {
        MiPushClient.registerPush(getContext(), PushConstants.xiaoMiAppID, PushConstants.xiaoMiAppKey);
    }

    /**
     * 初始化OPPO推送
     */
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

    /**
     * 初始化VIVO推送
     */
    private void initVIVOPush() {
        PushClient.getInstance(getContext().getApplicationContext()).turnOnPush(new IPushActionListener() {
            @Override
            public void onStateChanged(int state) {
                // TODO: 开关状态处理， 0代表成功，获取regid建议在state=0后获取；
                if (state == 0) {
                    PushClient.getInstance(getContext()).getRegId(new IPushQueryActionListener() {
                        @Override
                        public void onSuccess(String regId) {
                            Log.d(TAG, "onStateChanged: " + regId);
                            refreshPushToken(regId);
                        }

                        @Override
                        public void onFail(Integer integer) {
                            Log.d(TAG, "onStateChanged: onFail->" + integer);
                        }
                    });
                } else {
                    Log.d(TAG, "onStateChanged: " + state);
                }
            }
        });
    }

    /**
     * 初始化荣耀推送
     */
    private void initHonorPush() {
        // 获取PushToken
        HonorPushClient.getInstance().getPushToken(new HonorPushCallback<String>() {
            @Override
            public void onSuccess(String pushToken) {
                refreshPushToken(pushToken);
            }

            @Override
            public void onFailure(int errorCode, String errorString) {
                refreshPushToken("");
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
