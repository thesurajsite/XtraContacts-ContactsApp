package com.surajverma.xtracontacts.DiscoverPages

import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.surajverma.xtracontacts.AddContactActivity
import com.surajverma.xtracontacts.ContactPage.ContactPageViewModel
import com.surajverma.xtracontacts.R
import com.surajverma.xtracontacts.RecyclerContactAdapter.ViewHolder

class DiscoverPageRecyclerAdapter(
    private val context: Context,
    private val arrDiscoverPageList: ArrayList<DiscoverPageDataClass>,
    private val viewModel: DiscoverPagesViewModel,
    private val contactPageViewModel: ContactPageViewModel,
    private val userId: String
): RecyclerView.Adapter<DiscoverPageRecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pageName = itemView.findViewById<TextView>(R.id.pageNameTextView)
        val addPageButton = itemView.findViewById<CardView>(R.id.addPageButton)
        val pageCard = itemView.findViewById<CardView>(R.id.cardView)
        val vibrator = itemView.context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.discover_page_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.pageName.text = arrDiscoverPageList[position].pageName
        val pageId = arrDiscoverPageList[position].pageId

        holder.addPageButton.setOnClickListener {
            holder.vibrator.vibrate(100)
            contactPageViewModel.addContactPage(pageId.toString(), userId, context as Activity) { onSuccess->
                if(onSuccess) {
                    // Show Dialog
                    val dialog = Dialog(context)
                    dialog.setContentView(R.layout.page_added_dialog)
                    dialog.setCancelable(true)

                    val okButton = dialog.findViewById<Button>(R.id.okButton)
                    okButton.setOnClickListener {
                        dialog.dismiss()
                    }

                    dialog.show()
                }
            }
        }

        holder.pageCard.setOnLongClickListener {
            val pageDetails = DiscoverPageDataClass(
                arrDiscoverPageList[position].pageName,
                arrDiscoverPageList[position].pageId,
                arrDiscoverPageList[position].ownerId
            )
            viewModel.RemovePage(pageDetails, context){
                viewModel.fetchDiscoverPages(context)
            }

            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return arrDiscoverPageList.size
    }

    fun copyPageId(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clipboard?.setPrimaryClip(ClipData.newPlainText("Copy Page ID", text))
    }
}