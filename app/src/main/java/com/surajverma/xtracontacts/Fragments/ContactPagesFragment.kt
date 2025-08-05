package com.surajverma.xtracontacts

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.surajverma.xtracontacts.ContactPage.ContactPageDetailsModel
import com.surajverma.xtracontacts.ContactPage.ContactPagesRecyclerAdapter
import com.surajverma.xtracontacts.ContactPage.CreateContactPageActivity
import com.surajverma.xtracontacts.databinding.FragmentContactPagesBinding

class ContactPagesFragment : Fragment() {

    private var _binding: FragmentContactPagesBinding? = null
    private val binding get() = _binding!!

    private lateinit var arrContactPages: ArrayList<ContactPageDetailsModel>
    private lateinit var contactPagesAdapter: ContactPagesRecyclerAdapter
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactPagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        setupRecyclerView()
        observeContactPages()

        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(context, CreateContactPageActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.setHasFixedSize(true)

        arrContactPages = ArrayList<ContactPageDetailsModel>()

        val mainActivity = activity as MainActivity
        val contactPageViewModel = mainActivity.getContactPageViewModel()

        contactPagesAdapter = ContactPagesRecyclerAdapter(
            requireContext(),
            arrContactPages,
            auth,
            contactPageViewModel
        )
        binding.recyclerView.adapter = contactPagesAdapter
    }

    private fun observeContactPages() {
        val mainActivity = activity as MainActivity
        val contactPageViewModel = mainActivity.getContactPageViewModel()

        binding.progressbar.visibility = View.VISIBLE

        // Observe contact pages data
        contactPageViewModel.contactPages.observe(viewLifecycleOwner, Observer { contactPages ->
            arrContactPages.clear()
            arrContactPages.addAll(contactPages)
            contactPagesAdapter.notifyDataSetChanged()
            binding.progressbar.visibility = View.GONE
        })

        // Fetch contact pages - assuming you have a method to fetch user's contact pages
        contactPageViewModel.fetchMyContactPages(auth.currentUser?.uid.toString(), mainActivity)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}