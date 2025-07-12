package com.surajverma.xtracontacts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.surajverma.xtracontacts.Authentication.LoginActivity
import com.surajverma.xtracontacts.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var vibrator: Vibrator
    val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        binding.logoutButton.setOnClickListener {
            vibrator.vibrate(50)
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }

        if (user != null) {
            val profileImageUrl = user.photoUrl?.toString()
            if (!profileImageUrl.isNullOrEmpty()) {
                binding.profileImage.load(profileImageUrl) {
                    placeholder(R.drawable.person)
                    error(R.drawable.person)
                }
            }
        }

        if(user!=null){
            val email = user.email
            binding.emailTextView.text=email
        }

    }
}