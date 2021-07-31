package com.istech.sampletestaureus.views

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.istech.sampletestaureus.R
import com.istech.sampletestaureus.utils.BaseClass

class SplashActivity : BaseClass() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper())
            .postDelayed({
                if (sessionManager.login) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                } else {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }
            }, 2000)
    }
}