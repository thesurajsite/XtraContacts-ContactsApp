package com.surajverma.xtracontacts.ContactPage

import android.app.Activity
import android.content.Intent
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
import com.surajverma.xtracontacts.ContactsModel

class ContactPageViewModel: ViewModel() {

    lateinit var auth: FirebaseAuth
    private val db: FirebaseFirestore by lazy { Firebase.firestore}

    private val _contactPages = MutableLiveData<List<ContactPageDetailsModel>>()
    val contactPages: LiveData<List<ContactPageDetailsModel>> get() = _contactPages

    fun createContactsPage(pageName: String, ownerId: String, activity: Activity){
        val pageId = db.collection("CONTACT_PAGE").document().id
        val pageDetails = ContactPageDetailsModel(pageName, pageId, ownerId)

        db.collection("CONTACT_PAGE").document(pageId).collection("PAGE_DETAILS").document("PAGE_DETAILS").set(pageDetails)
            .addOnSuccessListener {
                Toast.makeText(activity, "Page Created", Toast.LENGTH_SHORT).show()
                val intent =  Intent(activity, ContactPageActivity::class.java)
                activity.startActivity(intent)
                activity.finish()
            }
            .addOnFailureListener {
                Toast.makeText(activity, "Some error occured creating page", Toast.LENGTH_SHORT).show()

            }

        // Add Page to: My Contact Pages
        db.collection("MY_CONTACT_PAGES").document(ownerId).collection("CONTACT_PAGES").document(pageId).set(pageDetails)
            .addOnSuccessListener {
                // Nothing
            }
            .addOnFailureListener {
                // Nothing
            }

    }


    fun fetchMyContactPages(userId: String, activity: Activity){

        db.collection("MY_CONTACT_PAGES").document(userId).collection("CONTACT_PAGES").get()
            .addOnSuccessListener {

                val contactPageList = mutableListOf<ContactPageDetailsModel>()

                for (document in it.documents) {
                    val pageName = document.getString("pageName") ?: ""
                    val pageId = document.getString("pageId") ?: ""
                    val ownerId = document.getString("ownerId") ?: ""

                    contactPageList.add(ContactPageDetailsModel(pageName, pageId, ownerId))

                }

                _contactPages.value=contactPageList

            }
            .addOnFailureListener {
                Toast.makeText(activity, "Some error occured fetching pages", Toast.LENGTH_SHORT).show()
            }
    }



}



data class ContactPageDetailsModel(
    val pageName: String,
    val pageId: String,
    val ownerId: String
)
