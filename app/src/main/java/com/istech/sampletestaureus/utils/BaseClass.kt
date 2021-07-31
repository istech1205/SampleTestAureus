package com.istech.sampletestaureus.utils

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

open class BaseClass : AppCompatActivity() {
    public lateinit var firebaseAuth: FirebaseAuth
    public lateinit var sessionManager: SessionManager
    public lateinit var mFireStore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = Firebase.auth
        sessionManager = SessionManager(applicationContext)
        mFireStore = FirebaseFirestore.getInstance()
    }
}