package com.xyvcard.push.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.xyvcard.push.XyPushManager;
import com.xyvcard.push.common.PushConstants;

import java.util.List;

/**
 * 2020-03-08 22:43
 * 手机型号判断
 */
public class BadgeUtils {

    /**
     * 打开通知设置界面
     *
     * @param context
     */
    public static void openNotificationSetting(Context context) {
        if (BrandUtils.isBrandHuawei()) {
            openPermissionSetting(context, PushConstants.notifyChannelId);
        } else {
            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.setAction(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, PushConstants.notifyChannelId);
            } else {
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            }
//            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
            intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());
            context.startActivity(intent);
        }
    }

    private static void openPermissionSetting(Context context, String channelID) {
        try {
            Intent localIntent = new Intent();
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //直接跳转到应用通知设置的代码：
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                localIntent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                localIntent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
                localIntent.putExtra(Settings.EXTRA_CHANNEL_ID, channelID);
                context.startActivity(localIntent);
                Log.e("跳转的类型", "-->1");
                return;
            }
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                localIntent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                localIntent.putExtra("app_package", context.getPackageName());
                localIntent.putExtra("app_uid", context.getApplicationInfo().uid);
                context.startActivity(localIntent);
                Log.e("跳转的类型", "-->2");
                return;
            }
            if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                localIntent.addCategory(Intent.CATEGORY_DEFAULT);
                localIntent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivity(localIntent);
                Log.e("跳转的类型", "-->3");
                return;
            }

            //4.4以下没有从app跳转到应用通知设置页面的Action，可考虑跳转到应用详情页面,
            if (Build.VERSION.SDK_INT >= 9) {
                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
                context.startActivity(localIntent);
                Log.e("跳转的类型", "-->4");
                return;
            }

            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setBadge(Context context, int number) {
        if (BrandUtils.isBrandHuawei() || BrandUtils.isBrandHonor()) {
            try {
                Bundle bundle = new Bundle();
                bundle.putString("package", XyPushManager.getInstance().getPackageName()); // com.test.badge is your package name
                bundle.putString("class", PushConstants.huaweiBadgeClassName); // com.test. badge.MainActivity is your apk main activity
                bundle.putInt("badgenumber", number);
                context.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, bundle);
            } catch (Exception e) {
                Log.e("设置红点", "-->不支持");
            }
        } else if (BrandUtils.isBrandVivo()) {
            try {
                Intent intent = new Intent("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");
                intent.putExtra("packageName", context.getPackageName());
                // String launchClassName = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName()).getComponent().getClassName();
                intent.putExtra("className", PushConstants.vivoBadgeClassName);
                intent.putExtra("notificationNum", number);
                context.sendBroadcast(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (BrandUtils.isBrandOppo()) {
            try {
                if (number == 0) {
                    number = -1;
                }
                Intent intent = new Intent("com.oppo.unsettledevent");
                intent.putExtra("pakeageName", context.getPackageName());
                intent.putExtra("number", number);
                intent.putExtra("upgradeNumber", number);
                if (canResolveBroadcast(context, intent)) {
                    context.sendBroadcast(intent);
                } else {
                    try {
                        Bundle extras = new Bundle();
                        extras.putInt("app_badge_count", number);
                        context.getContentResolver().call(Uri.parse("content://com.android.badge/badge"), "setAppBadgeCount", null, extras);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public static boolean canResolveBroadcast(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> receivers = packageManager.queryBroadcastReceivers(intent, 0);
        return receivers != null && receivers.size() > 0;
    }
}
