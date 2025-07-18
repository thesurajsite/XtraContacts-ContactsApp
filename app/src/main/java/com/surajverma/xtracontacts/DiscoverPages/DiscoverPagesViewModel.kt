package com.surajverma.xtracontacts.DiscoverPages

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.surajverma.xtracontacts.ContactPage.ContactPageDetailsModel
import com.surajverma.xtracontacts.R

class DiscoverPagesViewModel() : ViewModel() {
    var auth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore by lazy { Firebase.firestore}
    val userID = auth.currentUser?.uid
    val userEmail = FirebaseAuth.getInstance().currentUser?.email.toString()

    private val _contactPages = MutableLiveData<List<DiscoverPageDataClass>>()
    val contactPages: LiveData<List<DiscoverPageDataClass>> get() = _contactPages

    fun AddPage(pageId: String, context: Context, onResult:(Boolean)->Unit){

        val cleanPageId = cleanPageId(pageId)
        db.collection("CONTACT_PAGE").document(cleanPageId).get()
            .addOnSuccessListener { document->
                if(document.exists()){
                    val pageName = document.getString("pageName") ?: ""
                    val ownerId = document.getString("ownerId") ?: ""

                    if(userID==ownerId){
                        val pageDetails = DiscoverPageDataClass(pageName, cleanPageId, ownerId)
                        db.collection("DISCOVER_PAGES").document(cleanPageId).set(pageDetails)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Page Added", Toast.LENGTH_SHORT).show()
                                onResult(true)
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Couldn't Add Page", Toast.LENGTH_SHORT).show()
                                onResult(false)
                            }
                    }
                    else{
                        Toast.makeText(context, "Your are not the owner of this page", Toast.LENGTH_SHORT).show()
                        onResult(false)
                    }

                }
                else{
                    Toast.makeText(context, "Page Doesn't Exist", Toast.LENGTH_SHORT).show()
                    onResult(false)
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Some error occcured", Toast.LENGTH_SHORT).show()
                onResult(false)
            }

    }

    fun RemovePage(pageDetails: DiscoverPageDataClass, context: Context, onResult:(Boolean)->Unit){
        val pageId = pageDetails.pageId
        val ownerId = pageDetails.ownerId
        if(userID==ownerId || userEmail.equals("thesurajsite@gmail.com") || userEmail.equals("xtracontacts@gmail.com")){

            val builder = AlertDialog.Builder(context)
                .setTitle("Remove Page")
                .setIcon(R.drawable.contact_page)
                .setMessage("Do you want to Remove this page from public access?")
                .setPositiveButton(
                    "Remove"
                ) { dialogInterface, i ->
                    try {
                        // Remove the page from Discover Pages
                        db.collection("DISCOVER_PAGES").document(pageId!!).delete()
                            .addOnSuccessListener {
                                Toast.makeText(context, "Page Removed", Toast.LENGTH_SHORT).show()
                                onResult(true)
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Couldn't Remove Page", Toast.LENGTH_SHORT).show()
                                onResult(false)
                            }

                    } catch (e: Exception) {
                        Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                        onResult(false)
                    }

                }.setNegativeButton("Cancel")
                { dialogInterface, i ->
                    // Nothing
                }
            builder.show()

        }
    }

    fun fetchDiscoverPages(context: Context){
        db.collection("DISCOVER_PAGES").orderBy("pageName", Query.Direction.ASCENDING).get()
            .addOnSuccessListener {

                val pageList = mutableListOf<DiscoverPageDataClass>()
                for(document in it.documents){
                    val pageName = document.getString("pageName") ?: ""
                    val pageId = document.getString("pageId") ?: ""
                    val ownerId = document.getString("ownerId") ?: ""

                    pageList.add(DiscoverPageDataClass(pageName, pageId, ownerId))
                }
                _contactPages.value=pageList

            }
            .addOnFailureListener {
                Toast.makeText(context, "Couldn't fetch Pages", Toast.LENGTH_SHORT).show()
            }
    }

    fun cleanPageId(id: String): String {
        return id.removePrefix("https://www.")
            .removePrefix("https://")
            .removeSuffix(".xtra")
    }



}