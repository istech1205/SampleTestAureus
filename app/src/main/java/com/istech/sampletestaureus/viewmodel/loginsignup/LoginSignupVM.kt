package com.istech.sampletestaureus.viewmodel.loginsignup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.facebook.AccessToken
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.istech.sampletestaureus.model.CommonResponse
import com.istech.sampletestaureus.model.LoginModel
import com.istech.sampletestaureus.utils.Commn
import com.istech.sampletestaureus.utils.DBConstants


class LoginSignupVM : ViewModel() {

    var firebaseUser = MutableLiveData<FirebaseUser>()
    var loginResponse = MutableLiveData<LoginModel>()
    var errorInsertResponse = MutableLiveData<CommonResponse>()
    var authFailedResponse = MutableLiveData<String>()


    fun signUpWithEmailPassword(firebaseAuth: FirebaseAuth, email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {

                    val user = firebaseAuth.currentUser
                    firebaseUser.value = user

                }
            }.addOnFailureListener {
                authFailedResponse.value = it.message
                Commn.log("addOnFailureListener:" + it.message)
            }
    }

    fun signInWithEmailPassword(

        firebaseAuth: FirebaseAuth,
        mFireStore: FirebaseFirestore,
        email: String,
        password: String
    ) {

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        mFireStore.collection(DBConstants.users)
                            .document(user.uid)
                            .get()
                            .addOnSuccessListener { db ->
                                val loginModel = db.toObject(LoginModel::class.java)
                                loginResponse.value = loginModel
                            }
                    }
                }
            }.addOnFailureListener {
                authFailedResponse.value = it.message
                Commn.log("addOnFailureListener:" + it.message)
            }
    }

    fun facebookLogin(firebaseAuth: FirebaseAuth, token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                val user = firebaseAuth.currentUser
                firebaseUser.value = user
            }.addOnFailureListener {
                authFailedResponse.value = it.message
                Commn.log("addOnFailureListener:" + it.message)
            }
    }

    fun googleLogin(firebaseAuth: FirebaseAuth, token: String) {
        val credential = GoogleAuthProvider.getCredential(token, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                val user = firebaseAuth.currentUser
                firebaseUser.value = user
            }.addOnFailureListener {
                authFailedResponse.value = it.message
                Commn.log("addOnFailureListener:" + it.message)
            }
    }

    fun resetPassword(
        firebaseAuth: FirebaseAuth,
        email: String

    ) {

        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {

                authFailedResponse.value = "Reset Password link sent on your email"

            }.addOnFailureListener {
                authFailedResponse.value = it.message
                Commn.log("addOnFailureListener:" + it.message)
            }
    }

    fun insertOrUpdateUser(mFireStore: FirebaseFirestore, params: LoginModel) {
        Commn.log("addOnFailureListener:$params")
        val ref = mFireStore.collection(DBConstants.users).document(params.user_id)
        ref.get()
            .addOnCompleteListener {
                if (it.result!!.exists()) {
                    val body = CommonResponse()
                    body.error = false
                    body.message = "Welcome"
                    errorInsertResponse.value = body
                } else {
                    ref.set(params)
                        .addOnSuccessListener {
                            val body = CommonResponse()
                            body.error = false
                            body.message = "Welcome"
                            errorInsertResponse.value = body
                        }.addOnFailureListener {
                            val body = CommonResponse()
                            body.error = true
                            body.message = "Something is wrong"
                            errorInsertResponse.value = body
                        }
                }

            }
    }

}