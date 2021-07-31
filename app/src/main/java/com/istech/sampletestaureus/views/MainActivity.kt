package com.istech.sampletestaureus.views

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.istech.sampletestaureus.R
import com.istech.sampletestaureus.databinding.ActivityMainBinding
import com.istech.sampletestaureus.utils.SessionManager

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        sessionManager = SessionManager(applicationContext)
        initView()

    }

    private fun initView() {
        binding.apply {
            if (!sessionManager.user!!.user_email.equals("null")) {
                tvUserName.text = sessionManager.user!!.user_email
            } else {
                tvUserName.text = sessionManager.user!!.user_name
            }

            btLogout.setOnClickListener {


                try {
                    sessionManager.logout()

                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }
}