package com.istech.sampletestaureus.utils

import android.content.Context
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication



class AppController : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()


    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(applicationContext)
    }
}