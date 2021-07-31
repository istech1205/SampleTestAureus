package com.istech.sampletestaureus.views

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.gson.Gson
import com.istech.sampletestaureus.R
import com.istech.sampletestaureus.databinding.ActivityLoginBinding
import com.istech.sampletestaureus.model.LoginModel
import com.istech.sampletestaureus.utils.BaseClass
import com.istech.sampletestaureus.utils.Commn
import com.istech.sampletestaureus.utils.FastClickUtil
import com.istech.sampletestaureus.viewmodel.loginsignup.LoginSignupVM

import java.util.*


class LoginActivity : BaseClass() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginSignUpVM: LoginSignupVM
    private var isValid: Boolean = false
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager
    private var RC_SIGN_IN = 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        initView()
        handleClick()
        initObserver()

    }


    private fun initObserver() {
        loginSignUpVM.authFailedResponse.observe(this, {
            if (it != null) {
                Commn.hideProgress()
                Commn.myToast(this, it + "")
            }
        })
        loginSignUpVM.loginResponse.observe(this, {
            if (it != null) {
                Commn.hideProgress()
                sessionManager.saveUser(it)
                sessionManager.saveLogin(true)
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }
        })

        loginSignUpVM.firebaseUser.observe(this, {
            if (it != null) {
                val user = LoginModel()
                user.user_id = it.uid
                user.user_email = it.email.toString()
                user.user_name = it.displayName.toString()
                sessionManager.saveUser(user)
                sessionManager.saveLogin(true)

                loginSignUpVM.insertOrUpdateUser(mFireStore, user)
                try {
                    LoginManager.getInstance().logOut()
                    googleSignInClient.signOut()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
        loginSignUpVM.errorInsertResponse.observe(this, {
            if (it != null) {
                Commn.hideProgress()
                Commn.myToast(this, it.message + "")
                if (!it.error) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }
            }
        })
    }

    private fun initView() {
        loginSignUpVM = ViewModelProvider(this).get(LoginSignupVM::class.java)
        callbackManager = CallbackManager.Factory.create()
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)


    }

    private fun handleClick() {

        binding.apply {
            tvJoinNow.setOnClickListener {
                val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
                startActivity(intent)
            }
            btLogin.setOnClickListener {
                validateLogin()
            }
            tvForgotPassword.setOnClickListener {
                if (!Commn.isValidEmail(binding.etEmail.text.toString())) {
                    binding.etEmail.error = "Enter valid email"
                    binding.etEmail.requestFocus()
                    return@setOnClickListener
                }
                Commn.progress(this@LoginActivity)
                loginSignUpVM.resetPassword(firebaseAuth, etEmail.text.toString())
            }
            ivFacebook.setOnClickListener {
                if (FastClickUtil.isFastClick()) {
                    return@setOnClickListener
                }
                initFacebookLogin()
            }
            ivGoogle.setOnClickListener {
                if (FastClickUtil.isFastClick()) {
                    return@setOnClickListener
                }
                initGoogleLogin()
            }

        }
    }

    private fun initGoogleLogin() {
        // Configure Google Sign In
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun initFacebookLogin() {
        binding.facebookButton.performClick()
        binding.facebookButton.setReadPermissions("email", "public_profile")
        binding.facebookButton.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Commn.log("facebook:onSuccess:$loginResult")

                handleFacebookAccessToken(loginResult.accessToken)

            }

            override fun onCancel() {

            }

            override fun onError(error: FacebookException) {
                Commn.log("facebook:onError:$error")
            }
        })
    }

    // ...
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Commn.log("firebaseAuthWithGoogle:" + account.id)
                Commn.progress(this)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Commn.log("Google sign in failed:$e")

            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun handleFacebookAccessToken(token: AccessToken?) {
        Commn.log("handleFacebookAccessToken:$token")
        if (token != null) {
            loginSignUpVM.facebookLogin(firebaseAuth, token)
        }
    }

    private fun validateLogin() {
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
            loginSignUpVM.signInWithEmailPassword(
                firebaseAuth,
                mFireStore,
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString()
            )
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        if (idToken != null) {
            loginSignUpVM.googleLogin(firebaseAuth, idToken)
        }
    }
}





