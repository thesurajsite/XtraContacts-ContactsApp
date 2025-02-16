package com.surajverma.xtracontacts.ContactPage

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.surajverma.xtracontacts.R
import com.surajverma.xtracontacts.databinding.ActivityCreateContactPageBinding

class CreateContactPageActivity : AppCompatActivity() {

    private lateinit var  binding: ActivityCreateContactPageBinding
    private lateinit var viewModel: ContactPageViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateContactPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ContactPageViewModel()
        auth = FirebaseAuth.getInstance()

       binding.createContactsPageButton.setOnClickListener {
           val pageName = binding.contactsPageName.text.toString()
           val userId = auth.currentUser?.uid.toString()
           viewModel.createContactsPage(pageName, userId, this)
       }



    }
}