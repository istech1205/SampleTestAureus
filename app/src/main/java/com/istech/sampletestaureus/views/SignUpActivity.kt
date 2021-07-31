package com.istech.sampletestaureus.views

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.istech.sampletestaureus.R
import com.istech.sampletestaureus.databinding.ActivitySignUpBinding
import com.istech.sampletestaureus.model.LoginModel
import com.istech.sampletestaureus.utils.BaseClass
import com.istech.sampletestaureus.utils.Commn
import com.istech.sampletestaureus.viewmodel.loginsignup.LoginSignupVM


class SignUpActivity : BaseClass() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var loginSignUpVM: LoginSignupVM
    private var isValid: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)

        initView()
        initObserver()
        handleClick()
    }

    private fun initObserver() {
        loginSignUpVM.firebaseUser.observe(this, {
            if (it != null) {
                val user = LoginModel()
                user.user_id = it.uid
                user.user_email = it.email.toString()
                user.user_name = it.displayName.toString()
                loginSignUpVM.insertOrUpdateUser(mFireStore, user)
            }
        })
        loginSignUpVM.errorInsertResponse.observe(this, {
            if (it != null) {
                Commn.hideProgress()
                Commn.myToast(this, it.message + "")
                if (!it.error) {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }
            }
        })
        loginSignUpVM.authFailedResponse.observe(this, {
            if (it != null) {
                Commn.hideProgress()
                Commn.myToast(this, it + "")
            }
        })
    }

    private fun initView() {
        loginSignUpVM = ViewModelProvider(this).get(LoginSignupVM::class.java)
    }

    private fun handleClick() {
        binding.apply {
            btSignUp.setOnClickListener {
                validateSignUp()
            }
            tvLoginNow.setOnClickListener {
                val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun validateSignUp() {
        isValid = true

        if (!Commn.isValidEmail(binding.etEmail.text.toString())) {
            binding.etEmail.error = "Enter valid email"
            binding.etEmail.requestFocus()
            isValid = false
        }
        if (TextUtils.isEmpty(binding.etPassword.text.toString())) {
            binding.etPassword.error = "Enter valid password"
            binding.etPassword.requestFocus()
            isValid = false
        }
        if (isValid) {
            Commn.progress(this)
            loginSignUpVM.signUpWithEmailPassword(
                firebaseAuth,
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString()
            )
        }
    }


}