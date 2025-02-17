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

    private val _contacts = MutableLiveData<List<ContactsModel>>()
    val contacts: LiveData<List<ContactsModel>> get() = _contacts

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
        db.collection("MY_CONTACT_PAGES").document(userId).collection("CONTACT_PAGES")
            .orderBy("pageName", Query.Direction.ASCENDING).get()
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

    fun createContacts(contactDetails: ContactsModel,pageName: String, pageId: String, ownerId: String, userId: String, activity: Activity){

        if(ownerId == userId){

            val contactId = db.collection("CONTACT_PAGE").document(pageId).collection("PAGE_CONTACTS").document().id
            db.collection("CONTACT_PAGE").document(pageId).collection("PAGE_CONTACTS").document(contactId).set(contactDetails)
                .addOnSuccessListener {
                    Toast.makeText(activity, "Contact Created", Toast.LENGTH_SHORT).show()
                    val intent =  Intent(activity, AllContactsActivity::class.java)
                    intent.putExtra("pageName", pageName)
                    intent.putExtra("pageID", pageId)
                    intent.putExtra("ownerId", ownerId)
                    activity.startActivity(intent)
                    activity.finish()
                }
                .addOnFailureListener {
                    Toast.makeText(activity, "Some error Occured", Toast.LENGTH_SHORT).show()
                }
        }

    }

    fun fetchPageContacts(pageId: String, activity: Activity){

        val contactList = mutableListOf<ContactsModel>()

        db.collection("CONTACT_PAGE").document(pageId).collection("PAGE_CONTACTS")
            .orderBy("name", Query.Direction.ASCENDING).get()
            .addOnSuccessListener {
                //Toast.makeText(activity, "Contacts Fetched", Toast.LENGTH_SHORT).show()

                for (document in it.documents) {
                    val id = document.getString("id") ?: ""
                    val name = document.getString("name") ?: ""
                    val number = document.getString("number") ?: ""
                    val email = document.getString("email") ?: ""
                    val instagram = document.getString("instagram") ?: ""
                    val x = document.getString("x") ?: ""
                    val linkedin = document.getString("linkedin") ?: ""

                    contactList.add(ContactsModel(id, name, number, email, instagram, x, linkedin))

                }

                _contacts.value=contactList

            }
            .addOnFailureListener {
                Toast.makeText(activity, "Some Error Occured", Toast.LENGTH_SHORT).show()
            }
    }

    fun addContactPage(pageId: String, userId: String ,activity: Activity){
        db.collection("CONTACT_PAGE").document(pageId).collection("PAGE_DETAILS").document("PAGE_DETAILS").get()
            .addOnSuccessListener { document->

                if (document.exists()) {
                    Toast.makeText(activity, "Page Found", Toast.LENGTH_SHORT).show()

                    val pageName = document.getString("pageName") ?: ""
                    val ownerId = document.getString("ownerId") ?: ""
                    val pageId = document.getString("pageId") ?: ""

                    val contactDetails = ContactPageDetailsModel(pageName, pageId, ownerId)

                    // Add Page to My Contact Pages
                    db.collection("MY_CONTACT_PAGES").document(userId).collection("CONTACT_PAGES").document(pageId).set(contactDetails)
                        .addOnSuccessListener {
                            Toast.makeText(activity, "Page Added", Toast.LENGTH_SHORT).show()
                            val intent = Intent(activity, ContactPageActivity::class.java)
                            activity.startActivity(intent)
                            activity.finish()

                        }
                        .addOnFailureListener {
                            Toast.makeText(activity, "Page Not Added", Toast.LENGTH_SHORT).show()
                        }


                }else{
                    Toast.makeText(activity, "Page Not Found", Toast.LENGTH_SHORT).show()
                }

            }
            .addOnFailureListener {
                Toast.makeText(activity, "Some Error Occured finding Page", Toast.LENGTH_SHORT).show()

            }

    }



}



data class ContactPageDetailsModel(
    val pageName: String,
    val pageId: String,
    val ownerId: String
)
