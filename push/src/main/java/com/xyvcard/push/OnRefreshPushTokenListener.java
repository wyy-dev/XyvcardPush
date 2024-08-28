package com.xyvcard.push;

/**
 * @author: WYY
 * @date: 2024/8/27
 * @description：刷新推送Token监听
 */
public interface OnRefreshPushTokenListener {

    /**
     * token 该表监听
     *
     * @param token      1.华为->token
     *                   2.小米->RegId
     *                   3.荣耀->pushToken
     *                   4.oppo->registerID
     *                   5.vivo->regId
     * @param deviceType 设备类型
     */
    void onRefreshToken(String token, String deviceType);

}
