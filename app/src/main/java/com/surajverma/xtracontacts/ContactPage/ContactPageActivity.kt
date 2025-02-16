package com.surajverma.xtracontacts.ContactPage

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.surajverma.xtracontacts.databinding.ActivityContactPageBinding

class ContactPageActivity : AppCompatActivity() {

    private lateinit var  binding: ActivityContactPageBinding
    private lateinit var arrContactPage: ArrayList<ContactPageDetailsModel>
    private lateinit var viewModel: ContactPageViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityContactPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        arrContactPage= ArrayList()
        auth= FirebaseAuth.getInstance()
        val userId=auth.currentUser?.uid.toString()
        viewModel= ViewModelProvider(this).get(ContactPageViewModel::class.java)

        binding.recyclerView.layoutManager= LinearLayoutManager(this)
        binding.recyclerView.setHasFixedSize(true)
        val recyclerAdapter= ContactPagesRecyclerAdapter(this, arrContactPage, auth)
        binding.recyclerView.adapter = recyclerAdapter


        viewModel.fetchMyContactPages(userId, this)
        viewModel.contactPages.observe(this, Observer {
            arrContactPage.clear()
            arrContactPage.addAll(it)
            recyclerAdapter.notifyDataSetChanged()
        })

        binding.createContactPageButton.setOnClickListener {
            val intent = Intent(this, CreateContactPageActivity::class.java)
            startActivity(intent)
        }

    }
}