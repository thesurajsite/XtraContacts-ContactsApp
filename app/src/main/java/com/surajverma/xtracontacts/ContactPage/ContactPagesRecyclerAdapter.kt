package com.surajverma.xtracontacts.ContactPage

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.surajverma.xtracontacts.ContactsModel
import com.surajverma.xtracontacts.R
import com.surajverma.xtracontacts.RecyclerContactAdapter
import com.surajverma.xtracontacts.RecyclerContactAdapter.ViewHolder


class ContactPagesRecyclerAdapter(
    private val context: Context,
    private val arrContactPages: ArrayList<ContactPageDetailsModel>,
    private val auth: FirebaseAuth,
    private val viewModel: ContactPageViewModel
): RecyclerView.Adapter<ContactPagesRecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pageName = itemView.findViewById<TextView>(R.id.pageName)
        val adminImageView = itemView.findViewById<ImageView>(R.id.adminImageView)
        val contactCardLayout = itemView.findViewById<CardView>(R.id.contactCardLayout)
        val vibrator = itemView.context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.contact_page_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactPagesRecyclerAdapter.ViewHolder, position: Int) {

        val pageName = arrContactPages[position].pageName
        val pageId = arrContactPages[position].pageId
        val ownerId = arrContactPages[position].ownerId

        holder.pageName.text = arrContactPages[position].pageName

        if(arrContactPages[position].ownerId == auth.currentUser?.uid)
            holder.adminImageView.visibility = View.VISIBLE
        else holder.adminImageView.visibility = View.GONE

        holder.contactCardLayout.setOnClickListener {
            holder.vibrator.vibrate(50)
            val intent = Intent(context, AllContactsActivity::class.java)
            intent.putExtra("pageName", pageName)
            intent.putExtra("pageId", pageId)
            intent.putExtra("ownerId", ownerId)
            context.startActivity(intent)
        }

        holder.contactCardLayout.setOnLongClickListener {
            holder.vibrator.vibrate(50)

            val builder = AlertDialog.Builder(context)
                .setTitle("Contact Page")
                .setIcon(R.drawable.contact_page)
                .setMessage("Edit or Delete this Contact Page")
                .setPositiveButton(
                    "Edit Page"
                ) { dialogInterface, i ->
                    try {
                        Toast.makeText(context, "Feature Not Available", Toast.LENGTH_SHORT).show()

                    } catch (e: Exception) {
                        Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show()
                        Log.w("crash-attendance", e)
                    }

                }.setNegativeButton("Delete Page")
                { dialogInterface, i ->
                    viewModel.deleteContactPage(pageId, ownerId, context as Activity)
                }
            builder.show()

            return@setOnLongClickListener true
        }


    }

    override fun getItemCount(): Int {
        return arrContactPages.size
    }



}