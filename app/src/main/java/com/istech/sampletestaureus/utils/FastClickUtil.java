package com.istech.sampletestaureus.utils;

import android.util.Log;

public class FastClickUtil {
    public static final int MIN_DELAY_TIME= 500;  // 两次点击间隔不能少于500ms
    private static long lastClickTime;
    public static final int MIN_ADS_DELAY_TIME= 20000;
    private static long lastNoRecordClickTime;
    private static long sLastClickTime;
    public static boolean isFastClick() {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= MIN_DELAY_TIME) {
            flag = false;
        }
        lastClickTime = currentClickTime;
        lastNoRecordClickTime = currentClickTime;
        return flag;
    }
    public static boolean isFastLoading() {
        long currentClickTime = System.currentTimeMillis();
        boolean isFastClick = (currentClickTime - sLastClickTime) <= MIN_ADS_DELAY_TIME;
        Log.e("TAG", "log_common_FastClickUtil : " + (currentClickTime - sLastClickTime));
        sLastClickTime = currentClickTime;
        return isFastClick;
    }
    /**
     * 用于判断录制按钮和其他按钮同时按下
     * @return
     */
    public static boolean isRecordWithOtherClick() {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastNoRecordClickTime) >= MIN_DELAY_TIME) {
            flag = false;
        }
        lastClickTime = currentClickTime;
        return false;
    }
}
