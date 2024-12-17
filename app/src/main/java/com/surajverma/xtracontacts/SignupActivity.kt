package com.surajverma.xtracontacts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.surajverma.xtracontacts.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    private lateinit var vibrator: Vibrator
    private lateinit var authViewModel: AuthViewModel
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth=FirebaseAuth.getInstance()
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        val emailEditText=findViewById<EditText>(R.id.emailEditText)
        val passwordEditText=findViewById<EditText>(R.id.passwordEditText)
        val confirmPasswordEditText=findViewById<EditText>(R.id.confirmPasswordEditText)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.loginText.setOnClickListener {
            vibrator.vibrate(50)
            val intent= Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.createAccountButton.setOnClickListener {
            vibrator.vibrate(50)
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty() && password.equals(confirmPassword)){
                authViewModel.userSignup(email, password, this)
            }
            else{
                Toast.makeText(this, "Fill All Details", Toast.LENGTH_SHORT).show()
            }

        }

        binding.googleCardView.setOnClickListener {
            vibrator.vibrate(50)
            googleSignInClient.signOut()
            startActivityForResult(googleSignInClient.signInIntent, 123)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123 && resultCode ==  RESULT_OK) {
            Log.d("XtraContacts", "onActivityResult")
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!)

        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, /*accessToken=*/ null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Signed in Successfully", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Some Error Occured", Toast.LENGTH_LONG).show()
                Log.d("XtraContacts", it.localizedMessage!!)
            }
    }
}