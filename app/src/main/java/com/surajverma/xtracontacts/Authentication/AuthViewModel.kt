package com.surajverma.xtracontacts.Authentication

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.surajverma.xtracontacts.MainActivity

class AuthViewModel() : ViewModel() {

    lateinit var auth: FirebaseAuth

    fun checkLogin(activity: Activity){
        auth= FirebaseAuth.getInstance()
        if(auth.currentUser == null){
            val intent = Intent(activity, LoginActivity::class.java)
            activity.startActivity(intent)
            activity.finish()
        }
    }

    fun userLogin(email: String, password: String, activity: Activity){
        auth= FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(activity, "Logged In Successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(activity, MainActivity::class.java)
                activity.startActivity(intent)
                activity.finish()
            }
        }.addOnFailureListener{
            Toast.makeText(activity, it.localizedMessage, Toast.LENGTH_SHORT).show()
        }

    }

    fun userSignup(email: String, password: String, activity: Activity){
        auth= FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(activity, "Account Created Successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(activity, MainActivity::class.java)
                activity.startActivity(intent)
                activity.finish()
            }

        }.addOnFailureListener{
            Toast.makeText(activity, it.localizedMessage, Toast.LENGTH_SHORT).show()
        }


    }


}