package com.surajverma.xtracontacts.ContactPage

import android.os.Bundle
import android.widget.Toast
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
        val userId = auth.currentUser?.uid.toString()

       binding.createContactsPageButton.setOnClickListener {
           val pageName = binding.contactsPageName.text.toString()
           val userId = auth.currentUser?.uid.toString()
           viewModel.createContactsPage(pageName, userId, this)
       }

        binding.addContactsPageButton.setOnClickListener {
            if(binding.contactsPageId.text.toString().isNotEmpty()){
                val pageId = binding.contactsPageId.text.toString()
                viewModel.addContactPage(pageId, userId, this)
            }
            else{
                Toast.makeText(this, "Enter Page ID", Toast.LENGTH_SHORT).show()
            }

        }



    }
}