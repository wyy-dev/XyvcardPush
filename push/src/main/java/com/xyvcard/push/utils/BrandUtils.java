package com.xyvcard.push.utils;

import android.os.Build;

/**
 * @author: WYY
 * @date: 2024/8/27
 * @description：OS 工具类
 */
public class BrandUtils {
    public static boolean isBrandXiaoMi() {
        return "xiaomi".equalsIgnoreCase(getBrand()) || "xiaomi".equalsIgnoreCase(getManufacturer());
    }

    public static boolean isBrandHuawei() {
        return "huawei".equalsIgnoreCase(getBrand()) || "huawei".equalsIgnoreCase(getManufacturer());
    }

//    public static boolean isBrandMeizu() {
//        return "meizu".equalsIgnoreCase(getBrand()) || "meizu".equalsIgnoreCase(getManufacturer()) || "22c4185e".equalsIgnoreCase(getBrand())
//                || MzSystemUtils.isBrandMeizu(TUIOfflinePushConfig.getInstance().getContext());
//    }

    public static boolean isBrandOppo() {
        return "oppo".equalsIgnoreCase(getBrand()) || "realme".equalsIgnoreCase(getBrand()) || "oneplus".equalsIgnoreCase(getBrand())
                || "oppo".equalsIgnoreCase(getManufacturer()) || "realme".equalsIgnoreCase(getManufacturer())
                || "oneplus".equalsIgnoreCase(getManufacturer());
    }

    public static boolean isBrandVivo() {
        return "vivo".equalsIgnoreCase(getBrand()) || "vivo".equalsIgnoreCase(getManufacturer());
    }

    public static boolean isBrandHonor() {
        return "honor".equalsIgnoreCase(getBrand()) && "honor".equalsIgnoreCase(getManufacturer());
    }

    public static String getBrand() {
        return Build.BRAND;
    }

    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    public static String getDeviceType() {
        if (isBrandHuawei()) {
            return "HMS";
        } else if (isBrandXiaoMi()) {
            return "MI";
        } else if (isBrandOppo()) {
            return "OPPO";
        } else if (isBrandVivo()) {
            return "VIVO";
        } else if (isBrandHuawei()) {
            return "HONOR";
        } else {
            return "";
        }
    }

}
