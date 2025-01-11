package com.surajverma.xtracontacts

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.surajverma.xtracontacts.databinding.ActivityUpdateContactBinding

class UpdateContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateContactBinding
    lateinit var auth: FirebaseAuth
    private val db: FirebaseFirestore by lazy { Firebase.firestore}
    private lateinit var contactViewModel: ContactViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityUpdateContactBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        auth=FirebaseAuth.getInstance()
        contactViewModel = ViewModelProvider(this).get(ContactViewModel::class.java)

        binding.nameeditText.setText(intent.getStringExtra("name"))
        binding.numberEditText.setText(intent.getStringExtra("number"))
        binding.emailEditText.setText(intent.getStringExtra("email"))
        binding.instagramEditText.setText(intent.getStringExtra("instagram"))
        binding.XEditText.setText(intent.getStringExtra("x"))
        binding.linkedEditText.setText(intent.getStringExtra("linkedin"))

        // Setting Drawable start image to EditText
        val nameLogo = ContextCompat.getDrawable(this, R.drawable.name_logo)
        nameLogo?.setBounds(0, 0, 60, 60)
        binding.nameeditText.setCompoundDrawablesRelative(nameLogo, null, null, null)

        val phoneLogo = ContextCompat.getDrawable(this, R.drawable.phone_logo)
        phoneLogo?.setBounds(0, 0, 60, 60)
        binding.numberEditText.setCompoundDrawablesRelative(phoneLogo, null, null, null)

        val gmailLogo = ContextCompat.getDrawable(this, R.drawable.gmail_logo)
        gmailLogo?.setBounds(0, 0, 60, 60)
        binding.emailEditText.setCompoundDrawablesRelative(gmailLogo, null, null, null)

        val instaLogo = ContextCompat.getDrawable(this, R.drawable.insta_logo)
        instaLogo?.setBounds(0, 0, 60, 60)
        binding.instagramEditText.setCompoundDrawablesRelative(instaLogo, null, null, null)

        val linkedinLogo = ContextCompat.getDrawable(this, R.drawable.linkedin_logo)
        linkedinLogo?.setBounds(0, 0, 60, 60)
        binding.linkedEditText.setCompoundDrawablesRelative(linkedinLogo, null, null, null)


        val x_logo = ContextCompat.getDrawable(this, R.drawable.x_logo)
        x_logo?.setBounds(0, 0, 60,60) // Width and height in pixels
        binding.XEditText.setCompoundDrawablesRelative(x_logo, null, null, null)

        binding.saveButton.setOnClickListener {
            val id = intent.getStringExtra("id")
            val name = binding.nameeditText.text.toString()
            val number = binding.numberEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val instagram = binding.instagramEditText.text.toString()
            val x = binding.XEditText.text.toString()
            val linkedin = binding.linkedEditText.text.toString()

            val contactDetails =  ContactsModel(id, name, number, email, instagram, x, linkedin)
            contactViewModel.updateContact(contactDetails, this)
        }

        binding.deleteButton.setOnClickListener {
            val id = intent.getStringExtra("id")
            contactViewModel.deleteContact(id!!, this)
        }

    }
}