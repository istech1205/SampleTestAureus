package com.istech.sampletestaureus.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

public class Commn {

    public static void myToast(Context context, String msg) {
        if (context == null) {
            return;
        }
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();

    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static ProgressDialog progressDialog = null;

    public static void progress(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("please wait...");
        progressDialog.show();
    }

    public static void hideProgress() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    public static void log(String log) {
        Log.d("common_log", log + "");
    }


}
