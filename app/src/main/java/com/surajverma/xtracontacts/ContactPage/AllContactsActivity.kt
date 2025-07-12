package com.surajverma.xtracontacts.ContactPage

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.surajverma.xtracontacts.ContactsModel
import com.surajverma.xtracontacts.R
import com.surajverma.xtracontacts.RecyclerContactAdapter
import com.surajverma.xtracontacts.databinding.ActivityAllContactsBinding

class AllContactsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllContactsBinding
    private lateinit var arrContacts: ArrayList<ContactsModel>
    private lateinit var viewModel: ContactPageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllContactsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid.toString()
        val pageName = intent.getStringExtra("pageName") ?:""
        val pageId = intent.getStringExtra("pageId")?:""
        val ownerId = intent.getStringExtra("ownerId")?:""
        binding.pageNameTextView.text = pageName

        viewModel = ContactPageViewModel()
        arrContacts = ArrayList()

        binding.recyclerView.layoutManager= LinearLayoutManager(this)
        binding.recyclerView.setHasFixedSize(true)
        val recyclerAdapter= RecyclerContactAdapter(this, arrContacts, true)
        binding.recyclerView.adapter = recyclerAdapter


        viewModel.fetchPageContacts(pageId!!, this)
        viewModel.contacts.observe(this, Observer {
            arrContacts.clear()
            arrContacts.addAll(it)
            recyclerAdapter.notifyDataSetChanged()
        })

        if(ownerId == userId){
            binding.floatingActionButton.visibility = View.VISIBLE
        }else{
            binding.floatingActionButton.visibility = View.GONE
        }

        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(this, CreateContactForContactPageActivity::class.java)
            intent.putExtra("pageName", pageName)
            intent.putExtra("pageID", pageId)
            intent.putExtra("ownerId", ownerId)
            startActivity(intent)
        }

        binding.copyCardView.setOnClickListener {
            val newPageId = "https://"+pageId+".xtra"
            copyPageId(this, newPageId)
        }

    }

    fun copyPageId(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clipboard?.setPrimaryClip(ClipData.newPlainText("Copy Page ID", text))
    }

    override fun onResume() {
        super.onResume()

        val pageId = intent.getStringExtra("pageId") ?: return
        viewModel.fetchPageContacts(pageId, this)
    }


}