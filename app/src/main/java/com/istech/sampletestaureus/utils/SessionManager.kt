package com.istech.sampletestaureus.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.istech.sampletestaureus.model.LoginModel


@SuppressLint("CommitPrefEdits")
class SessionManager(context: Context) {
    private val pref: SharedPreferences

    private val editor: SharedPreferences.Editor

    var PREF_NAME = "My_Pref"

    private val USER = "USER"

    var IS_LOGIN = "IS_LOGIN"
    fun saveLogin(value: Boolean?) {
        if (value != null) {
            editor.putBoolean(IS_LOGIN, value)
        }
        editor.apply()
    }

    val login: Boolean
        get() = pref.getBoolean(IS_LOGIN, false)

    fun saveUser(user: LoginModel?) {
        editor.putString(USER, Gson().toJson(user))
        editor.apply()
    }

    val user: LoginModel?
        get() {
            val userString: String = pref.getString(USER, "").toString()
            return if (userString.isNotEmpty()) {
                Gson().fromJson(userString, LoginModel::class.java)
            } else null
        }


    fun logout() {
        try {
            editor.clear().apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        saveLogin(false)
    }

    init {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        editor = pref.edit()

    }
}