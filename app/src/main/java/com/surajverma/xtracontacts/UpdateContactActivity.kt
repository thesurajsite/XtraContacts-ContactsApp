package com.surajverma.xtracontacts

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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

    }
}