package com.surajverma.xtracontacts.ContactPage

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
                val contactDetails = ContactsModel("", name, number, email, instagram, x, linkedin, pageName, pageID, ownerId)
                viewModel.createContacts(contactDetails, userId, this)
            }else{
                Toast.makeText(this, "Please Enter Name", Toast.LENGTH_SHORT).show()

            }
        }


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

    }
}