package com.surajverma.xtracontacts.ContactPage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.surajverma.xtracontacts.ContactsModel
import com.surajverma.xtracontacts.R
import com.surajverma.xtracontacts.RecyclerContactAdapter
import com.surajverma.xtracontacts.RecyclerContactAdapter.ViewHolder

class ContactPagesRecyclerAdapter(
    private val context: Context,
    private val arrContactPages: ArrayList<ContactPageDetailsModel>,
    private val auth: FirebaseAuth
): RecyclerView.Adapter<ContactPagesRecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pageName = itemView.findViewById<TextView>(R.id.pageName)
        val adminImageView = itemView.findViewById<ImageView>(R.id.adminImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.contact_page_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactPagesRecyclerAdapter.ViewHolder, position: Int) {

        holder.pageName.text = arrContactPages[position].pageName

        if(arrContactPages[position].ownerId == auth.currentUser?.uid)
            holder.adminImageView.visibility = View.VISIBLE
        else holder.adminImageView.visibility = View.GONE


    }

    override fun getItemCount(): Int {
        return arrContactPages.size
    }



}