package com.surajverma.xtracontacts

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class RecyclerContactAdapter(
    private val context: Context,
    private val arrContacts: ArrayList<ContactsModel>
): RecyclerView.Adapter<RecyclerContactAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtName: TextView = itemView.findViewById(R.id.txtName)
        var txtNumber: TextView = itemView.findViewById(R.id.txtNumber)
        var cardView: CardView = itemView.findViewById(R.id.cardView)
        var linear: LinearLayout = itemView.findViewById(R.id.linear)
        var editButton: ImageView = itemView.findViewById(R.id.editButton)
        var callButton: ImageView = itemView.findViewById(R.id.callButton)
        var whatsappButton: ImageView = itemView.findViewById(R.id.whatsappButton)
        var emailButton : ImageView =  itemView.findViewById(R.id.emailButton)
        var instagramButton: ImageView = itemView.findViewById(R.id.instagramButton)
        var xButton: ImageView = itemView.findViewById(R.id.XButton)
        var linkedinButton: ImageView = itemView.findViewById(R.id.linkedinButton)
        val vibrator = itemView.context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.contact_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return arrContacts.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtName.text = arrContacts[position].name
        holder.txtNumber.text = arrContacts[position].number

        iconVisibilityControls(holder, position)

        val arr = IntArray(1) { 0 }
        holder.cardView.setOnClickListener {
            holder.vibrator.vibrate(50)
            arr[0] = if (arr[0] == 0) {
                holder.linear.visibility = View.VISIBLE
                1
            } else {
                holder.linear.visibility = View.GONE
                0
            }
        }

        holder.editButton.setOnClickListener {
            holder.vibrator.vibrate(50)
            val intent = Intent(context, UpdateContactActivity::class.java)
            intent.putExtra("id", arrContacts[position].id)
            intent.putExtra("name", arrContacts[position].name)
            intent.putExtra("number", arrContacts[position].number)
            intent.putExtra("email", arrContacts[position].email)
            intent.putExtra("instagram", arrContacts[position].instagram)
            intent.putExtra("x", arrContacts[position].x)
            intent.putExtra("linkedin", arrContacts[position].linkedin)
            context.startActivity(intent)
        }

        holder.callButton.setOnClickListener {
            holder.vibrator.vibrate(50)
            if (arrContacts[position].number!!.isNotEmpty()) {
                Toast.makeText(context, "Calling ${arrContacts[position].name}", Toast.LENGTH_SHORT).show()
                val callintent = Intent(Intent.ACTION_DIAL)
                callintent.data = Uri.parse("tel:${arrContacts[position].number}")
                context.startActivity(callintent)
            }
        }

        holder.whatsappButton.setOnClickListener {
            holder.vibrator.vibrate(50)
            val userNumberWithSpaces = arrContacts[position].number!! // The Phone Number May have spaces
            val userNumberWithoutSpaces = userNumberWithSpaces.filterNot { it.isWhitespace() || it == '+' } // To store Number Without Spaces
            val length = userNumberWithoutSpaces.length // length of filtered phone number

            if (length >= 10) {
                val whatsappNumber = "91${userNumberWithoutSpaces.substring(length - 10, length)}"
                if (whatsappNumber.length == 12) {
                    Toast.makeText(context, "Opening Whatsapp", Toast.LENGTH_SHORT).show()
                    val whatsappIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=$whatsappNumber"))
                    context.startActivity(whatsappIntent)
                } else {
                    Toast.makeText(context, "Invalid WhatsApp Number", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Invalid WhatsApp Number", Toast.LENGTH_SHORT).show()
            }
        }

        holder.emailButton.setOnClickListener {
            holder.vibrator.vibrate(50)
            val emailID = arrContacts[position].email
            if (emailID!!.isNotEmpty()) {
                Toast.makeText(context, "Opening Email app for $emailID", Toast.LENGTH_SHORT).show()

                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:$emailID")
                }

                try {
                    context.startActivity(emailIntent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, "No Email app found", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Invalid Email ID", Toast.LENGTH_SHORT).show()
            }
        }


        holder.instagramButton.setOnClickListener {
            holder.vibrator.vibrate(50)
            var instaID = arrContacts[position].instagram
            if (instaID!!.isNotEmpty()) {
                if (instaID[0] == '@') {
                    instaID = instaID.substring(1)
                }

                Toast.makeText(context, "Opening $instaID", Toast.LENGTH_SHORT).show()

                val uri = Uri.parse("https://www.instagram.com/$instaID")
                val appInstagram = Intent(Intent.ACTION_VIEW, uri).apply {
                    setPackage("com.instagram.android")
                }

                try {
                    context.startActivity(appInstagram)
                } catch (e: ActivityNotFoundException) {
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                }
            } else {
                Toast.makeText(context, "Invalid Instagram ID", Toast.LENGTH_SHORT).show()
            }
        }

        holder.xButton.setOnClickListener {
            holder.vibrator.vibrate(50)
            var xID = arrContacts[position].x
            if (xID!!.isNotEmpty()) {
                if (xID[0] == '@') {
                    xID = xID.substring(1)
                }

                Toast.makeText(context, "Opening $xID", Toast.LENGTH_SHORT).show()

                val uri = Uri.parse("https://www.twitter.com/$xID")
                val appX = Intent(Intent.ACTION_VIEW, uri).apply {
                    setPackage("com.twitter.android")
                }

                try {
                    context.startActivity(appX)
                } catch (e: ActivityNotFoundException) {
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                }
            } else {
                Toast.makeText(context, "Invalid X ID", Toast.LENGTH_SHORT).show()
            }
        }

        holder.linkedinButton.setOnClickListener {
            holder.vibrator.vibrate(50)
            var linkedinID = arrContacts[position].linkedin
            if (linkedinID!!.isNotEmpty()) {
                if (linkedinID[0] == '@') {
                    linkedinID = linkedinID.substring(1)
                }

                Toast.makeText(context, "Opening $linkedinID", Toast.LENGTH_SHORT).show()

                val uri = Uri.parse("https://www.linkedin.com/in/$linkedinID")
                val appLinkedin = Intent(Intent.ACTION_VIEW, uri).apply {
                    setPackage("com.linkedin.android.home")
                }

                try {
                    context.startActivity(appLinkedin)
                } catch (e: ActivityNotFoundException) {
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                }
            } else {
                Toast.makeText(context, "Invalid LinkedIn ID", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun iconVisibilityControls(holder: ViewHolder, position: Int) {

        if (arrContacts[position].number!!.isEmpty()) {
            holder.callButton.visibility = View.GONE
            holder.whatsappButton.visibility = View.GONE
        } else {
            holder.callButton.visibility = View.VISIBLE
            holder.whatsappButton.visibility = View.VISIBLE
        }

        if (arrContacts[position].email!!.isEmpty()) {
            holder.emailButton.visibility = View.GONE
        } else {
            holder.emailButton.visibility = View.VISIBLE
        }

        if (arrContacts[position].instagram!!.isEmpty()) {
            holder.instagramButton.visibility = View.GONE
        } else {
            holder.instagramButton.visibility = View.VISIBLE
        }

        if (arrContacts[position].x!!.isEmpty()) {
            holder.xButton.visibility = View.GONE
        } else {
            holder.xButton.visibility = View.VISIBLE
        }

        if (arrContacts[position].linkedin!!.isEmpty()) {
            holder.linkedinButton.visibility = View.GONE
        } else {
            holder.linkedinButton.visibility = View.VISIBLE
        }

    }
}