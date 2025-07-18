package com.surajverma.xtracontacts.ContactPage

import android.os.Bundle
import android.os.Vibrator
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
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

       binding.createContactsPageButton.setOnClickListener {
           vibrator.vibrate(50)
           val pageName = binding.contactsPageName.text.toString()
           val userId = auth.currentUser?.uid.toString()
           if(pageName.isNotEmpty()){
               viewModel.createContactsPage(pageName, userId, this){ onSuccess->
                   if(onSuccess){
                       finish()
                   }
               }
           }else{
               Toast.makeText(this, "Enter Page Name", Toast.LENGTH_SHORT).show()
           }
       }

        binding.addContactsPageButton.setOnClickListener {
            vibrator.vibrate(50)
            if(binding.contactsPageId.text.toString().isNotEmpty()){
                val pageId = binding.contactsPageId.text.toString()
                viewModel.addContactPage(pageId, userId, this){ onSuccess->
                    if(onSuccess){
                        finish()
                    }
                }
            }
            else{
                Toast.makeText(this, "Enter Page ID", Toast.LENGTH_SHORT).show()
            }

        }



    }
}