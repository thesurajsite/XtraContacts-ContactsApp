package com.surajverma.xtracontacts

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.surajverma.xtracontacts.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth
    private lateinit var authViewModel: AuthViewModel
    private lateinit var contactViewModel: ContactViewModel
    lateinit var arrContact: ArrayList<ContactsModel>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        auth=FirebaseAuth.getInstance()
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        contactViewModel= ViewModelProvider(this).get(ContactViewModel::class.java)

        // Check Login
        authViewModel.checkLogin(this)

        // RecyclerView
        binding.recyclerView.layoutManager= LinearLayoutManager(this)
        binding.recyclerView.setHasFixedSize(true)
        arrContact= ArrayList<ContactsModel>()
        val recyclerAdapter= RecyclerContactAdapter(this, arrContact)
        binding.recyclerView.adapter = recyclerAdapter

        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(this, AddContactActivity::class.java)
            startActivity(intent)
        }

        // Observe Livedata
        binding.Progressbar.visibility = View.VISIBLE
        contactViewModel.contacts.observe(this, Observer {
            arrContact.clear()
            arrContact.addAll(it)
            recyclerAdapter.notifyDataSetChanged()
            binding.Progressbar.visibility = View.GONE

        })

        contactViewModel.fetchAllContacts(this)

    }
}