package com.xyvcard.push.utils;

import static android.provider.Settings.EXTRA_APP_PACKAGE;
import static android.provider.Settings.EXTRA_CHANNEL_ID;

import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.xyvcard.push.XyPushManager;
import com.xyvcard.push.common.PushConstants;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 2020-03-08 22:43
 * 手机型号判断
 */
public class BadgeUtils {
    private static final String TAG = "BadgeUtils";

    /**
     * 打开通知设置界面
     *
     * @param context
     */
    public static void openNotificationSetting(Context context) {
        try {
            // 根据isOpened结果，判断是否需要提醒用户跳转AppInfo页面，去打开App通知权限
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            //这种方案适用于 API 26, 即8.0（含8.0）以上可以用
            intent.putExtra(EXTRA_APP_PACKAGE, context.getPackageName());
            intent.putExtra(EXTRA_CHANNEL_ID, context.getApplicationInfo().uid);

            //这种方案适用于 API21——25，即 5.0——7.1 之间的版本可以使用
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
            context.startActivity(intent);
        } catch (Exception e) {
            // 出现异常则跳转到应用设置界面：锤子坚果3——OC105 API25
            Intent intent = new Intent();
            //下面这种方案是直接跳转到当前应用的设置界面。
            //https://blog.csdn.net/ysy950803/article/details/71910806
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
            intent.setData(uri);
            context.startActivity(intent);
        }
//        if (BrandUtils.isBrandHuawei()) {
//            openPermissionSetting(context, PushConstants.notifyChannelId);
//        } else {
//            Intent intent = new Intent();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                intent.setAction(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
//                intent.putExtra(Settings.EXTRA_CHANNEL_ID, PushConstants.notifyChannelId);
//            } else {
//                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
//            }
////            intent.putExtra("app_package", context.getPackageName());
//            intent.putExtra("app_uid", context.getApplicationInfo().uid);
//            intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());
//            context.startActivity(intent);
//        }
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
//            Log.d(TAG, "setBadge: vivo");
//            Uri uri = Uri.parse("content://" + "com.vivo.abe.provider.launcher.notification.num");
//            Bundle extra = new Bundle();
//            extra.putString("package", context.getPackageName()); //接入的App包名
//            extra.putString("class", PushConstants.vivoBadgeClassName);//接入的App class名
//            extra.putInt("badgenumber", number);//目标的角标数
//            /*这里一定要先使用 ContentProviderClient 建立非稳连接，不可以直接通过 getContentResolver()调用 call 方法，会有 Server 端崩溃带崩 Client 端的风险*/
//            ContentProviderClient client = null;
//            try {
//                client = context.getContentResolver().acquireUnstableContentProviderClient(uri);
//                if (client != null) {
//                    int result = client.call("change_badge", null, extra).getInt("result");
//                    Log.d(TAG, "setBadge: " + result);
//                } else {
//                    // TODO 调用角标接口失败或者不支持
//                    Log.d(TAG, "setBadge: 不支持设置角标");
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                Log.e(TAG, "setBadge: ", e);
//            } finally {
//                if (client != null) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        client.close();
//                    } else {
//                        client.release();
//                    }
//                }
//            }
//            try {
//                Intent intent = new Intent();
//                intent.setAction("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");
//                intent.putExtra("packageName", context.getPackageName());//接入方自己的包名
//                intent.putExtra("className", PushConstants.vivoBadgeClassName);//对应接入方的launcher入口的activity全路径activity名字（AndroidManifest中标识了android.intent.category.LAUNCHER的activity）
//                intent.putExtra("notificationNum", number);
//                intent.addFlags(invokeIntconstants(Intent.class.getCanonicalName(), "FLAG_RECEIVER_INCLUDE_BACKGROUND", 0));
//                context.sendBroadcast(intent);
//            } catch (Exception e) {
//                Log.e(TAG, "setBadge: ", e);
//            }
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

    private static int invokeIntconstants(String CanonicalName, String name, int default_value) {
        int value = default_value;
        try {
            Class<?> c = Class.forName(CanonicalName);
            Field Field = c.getField(name);
            value = (int) Field.get(c);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return value;
        }
    }
}
