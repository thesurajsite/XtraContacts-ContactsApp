package com.surajverma.xtracontacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.app.Activity
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.surajverma.xtracontacts.databinding.FragmentMyContactsBinding

class MyContactsFragment : Fragment() {

    private var _binding: FragmentMyContactsBinding? = null
    private val binding get() = _binding!!

    private lateinit var arrContact: ArrayList<ContactsModel>
    private lateinit var recyclerAdapter: RecyclerContactAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeContacts()

        binding.addButton.playAnimation()
        binding.addButton.setOnClickListener {
            val intent = Intent(context, AddContactActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.setHasFixedSize(true)
        arrContact = ArrayList<ContactsModel>()
        recyclerAdapter = RecyclerContactAdapter(requireContext(), arrContact, false)
        binding.recyclerView.adapter = recyclerAdapter
    }

    private fun observeContacts() {
        val mainActivity = activity as MainActivity
        val contactViewModel = mainActivity.getContactViewModel()

        binding.progressbar.visibility = View.VISIBLE

        contactViewModel.contacts.observe(viewLifecycleOwner, Observer { contacts ->
            arrContact.clear()
            arrContact.addAll(contacts)
            recyclerAdapter.notifyDataSetChanged()
            binding.progressbar.visibility = View.GONE
        })

        // Fetch contacts
        contactViewModel.fetchAllContacts(mainActivity)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}