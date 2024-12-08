package com.surajverma.xtracontacts

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.surajverma.xtracontacts.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    private lateinit var authViewModel: AuthViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth=FirebaseAuth.getInstance()
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        val emailEditText=findViewById<EditText>(R.id.emailEditText)
        val passwordEditText=findViewById<EditText>(R.id.passwordEditText)
        val loginButton=findViewById<Button>(R.id.loginButton)
        val createAccountText=findViewById<TextView>(R.id.createAccountText)

        binding.createAccountText.setOnClickListener {
            val intent= Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.loginButton.setOnClickListener {
            val email=emailEditText.text.toString()
            val password=passwordEditText.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty()){
                authViewModel.userLogin(email, password, this)
            }
            else{
                Toast.makeText(this, "Fill All Details", Toast.LENGTH_SHORT).show()
            }
        }

        binding.googleCardView.setOnClickListener {

        }



    }
}