package com.surajverma.xtracontacts.ContactPage

import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.surajverma.xtracontacts.MainActivity
import com.surajverma.xtracontacts.R
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
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

        binding.recyclerView.layoutManager= LinearLayoutManager(this)
        binding.recyclerView.setHasFixedSize(true)
        val recyclerAdapter= ContactPagesRecyclerAdapter(this, arrContactPage, auth, viewModel)
        binding.recyclerView.adapter = recyclerAdapter


        binding.Progressbar.visibility= View.VISIBLE
        viewModel.fetchMyContactPages(userId, this)
        viewModel.contactPages.observe(this, Observer {
            arrContactPage.clear()
            arrContactPage.addAll(it)
            recyclerAdapter.notifyDataSetChanged()
            binding.Progressbar.visibility= View.GONE
        })

        binding.floatingActionButton.setOnClickListener {
            vibrator.vibrate(50)
            val intent = Intent(this, CreateContactPageActivity::class.java)
            startActivity(intent)
        }

        // Bottom Navigation
        binding.bottomNavigation.setSelectedItemId(R.id.ContactPages)
        binding.bottomNavigation.setOnItemSelectedListener {

            when(it.itemId){
                R.id.MyContacts ->{
                    vibrator.vibrate(50)
                    //sharedPreferenceManager.updateNavigationCode(2)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }

            return@setOnItemSelectedListener true
        }

    }
}