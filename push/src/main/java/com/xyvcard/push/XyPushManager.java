package com.xyvcard.push;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.xyvcard.push.common.MyContext;
import com.xyvcard.push.common.PushConstants;

import java.lang.ref.WeakReference;

/**
 * @author: WYY
 * @date: 2024/8/27
 * @description：推送管理类
 */
public abstract class XyPushManager {

    private static class PushApplicationBinder {
        static final XyPushManagerImpl push = new XyPushManagerImpl();
    }

    private static WeakReference<Context> mContext;

    public static XyPushManager getInstance() {
        return XyPushManager.PushApplicationBinder.push;
    }

    /**
     * 初始化
     *
     * @param context
     */
    protected static void init(Context context) {
        mContext = new WeakReference<>(context);
        MyContext.getInstance().init(context);
        notifyChannel(context);
    }

    /**
     * 注册推送
     */
    public abstract void registerPush();

    public Context getContext() {
        if (mContext == null) {
            return null;
        }
        return mContext.get();
    }

    /**
     * 获取应用报名
     *
     * @return
     */
    public String getPackageName() {
        if (mContext == null) {
            return null;
        }
        return mContext.get().getPackageName();
    }

    public abstract void addOnRefreshPushTokenListener(OnRefreshPushTokenListener listener);

    public abstract void removeOnRefreshPushTokenListener(OnRefreshPushTokenListener listener);

    public abstract void refreshPushToken(String token);

    private static void notifyChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = PushConstants.notifyChannelId;
            String channelName = PushConstants.notifyChannelName;
            String channelDescription = PushConstants.notifyChannelDescription;
            NotificationChannel mNotificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationChannel.setDescription(channelDescription);
            ((NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE)).createNotificationChannel(mNotificationChannel);
        }
    }

}
