package com.surajverma.xtracontacts.ContactPage

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.surajverma.xtracontacts.ContactsModel
import com.surajverma.xtracontacts.R
import com.surajverma.xtracontacts.databinding.ActivityAllContactsBinding
import com.surajverma.xtracontacts.databinding.ActivityCreateContactForContactPageBinding

class CreateContactForContactPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateContactForContactPageBinding
    private lateinit var viewModel: ContactPageViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateContactForContactPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid.toString()
        val pageName = intent.getStringExtra("pageName")
        val pageID = intent.getStringExtra("pageID")
        val ownerId = intent.getStringExtra("ownerId")

        viewModel = ContactPageViewModel()


        binding.saveButton.setOnClickListener {

            val name = binding.nameeditText.text.toString()
            val number = binding.numberEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val instagram = binding.instagramEditText.text.toString()
            val x = binding.XEditText.text.toString()
            val linkedin = binding.linkedEditText.text.toString()

            if(name.isNotEmpty()){
                val contactDetails = ContactsModel("", name, number, email, instagram, x, linkedin)
                viewModel.createContacts(contactDetails, pageName!!, pageID!!, ownerId!!, userId, this)
            }else{
                Toast.makeText(this, "Please Enter Name", Toast.LENGTH_SHORT).show()

            }
        }

    }
}