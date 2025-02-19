package com.surajverma.xtracontacts.ContactPage

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.surajverma.xtracontacts.ContactsModel
import com.surajverma.xtracontacts.R

class ContactPageViewModel: ViewModel() {

    var auth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore by lazy { Firebase.firestore}

    private val _contactPages = MutableLiveData<List<ContactPageDetailsModel>>()
    val contactPages: LiveData<List<ContactPageDetailsModel>> get() = _contactPages

    private val _contacts = MutableLiveData<List<ContactsModel>>()
    val contacts: LiveData<List<ContactsModel>> get() = _contacts

    fun createContactsPage(pageName: String, ownerId: String, activity: Activity){
//        val pageDetails = mapOf(
//            "pageName" to pageName,
//            "pageId" to pageId,
//            "ownerId" to ownerId
//        )

        val pageId = db.collection("CONTACT_PAGE").document().id
        val pageDetails = ContactPageDetailsModel(pageName, pageId, ownerId)

        db.collection("CONTACT_PAGE").document(pageId).set(pageDetails)
            .addOnSuccessListener {
                Toast.makeText(activity, "Page Created", Toast.LENGTH_SHORT).show()
                val intent = Intent(activity, ContactPageActivity::class.java)
                activity.startActivity(intent)
                activity.finish()
            }
            .addOnFailureListener {
                Toast.makeText(activity, "Some error occurred creating page", Toast.LENGTH_SHORT).show()
            }

        // Add Page to: My Contact Pages
        //val pageIdMap = mapOf("pageId" to pageId)
        db.collection("MY_CONTACT_PAGES").document(ownerId) .set(mapOf("ContactPages" to FieldValue.arrayUnion(pageId)), SetOptions.merge())
            .addOnSuccessListener {
                // Nothing
            }
            .addOnFailureListener {
                // Nothing
            }


    }


    fun fetchMyContactPages(userId: String, activity: Activity) {
        val userDocRef = db.collection("MY_CONTACT_PAGES").document(userId)

        userDocRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val pageIdList = document.get("ContactPages") as? List<String> ?: emptyList()

                if (pageIdList.isNotEmpty()) {
                    val contactPageList = mutableListOf<ContactPageDetailsModel>()
                    val validPageIds = mutableListOf<String>()
                    var processedCount = 0

                    for (pageId in pageIdList) {
                        db.collection("CONTACT_PAGE").document(pageId).get()
                            .addOnSuccessListener { pageDoc ->
                                processedCount++

                                if (pageDoc.exists()) {
                                    val pageName = pageDoc.getString("pageName") ?: ""
                                    val ownerId = pageDoc.getString("ownerId") ?: ""

                                    contactPageList.add(ContactPageDetailsModel(pageName, pageId, ownerId))
                                    validPageIds.add(pageId) // Keep only existing page IDs
                                }

                                // Update LiveData when all requests are done
                                if (processedCount == pageIdList.size) {
                                    _contactPages.value = contactPageList
                                    if (validPageIds.size < pageIdList.size) {
                                        cleanUpInvalidPageIds(userDocRef, validPageIds)
                                    }
                                }
                            }
                            .addOnFailureListener {
                                processedCount++
                                Log.e("Firestore", "Error fetching page $pageId")

                                if (processedCount == pageIdList.size) {
                                    _contactPages.value = contactPageList
                                    if (validPageIds.size < pageIdList.size) {
                                        cleanUpInvalidPageIds(userDocRef, validPageIds)
                                    }
                                }
                            }
                    }
                } else {
                    Toast.makeText(activity, "No Contact Pages found", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(activity, "You have no Contact Pages", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(activity, "Error fetching pages", Toast.LENGTH_SHORT).show()
        }
    }

    /** Remove deleted page IDs from MY_CONTACT_PAGES */
    private fun cleanUpInvalidPageIds(userDocRef: DocumentReference, validPageIds: List<String>) {
        userDocRef.update("ContactPages", validPageIds)
            .addOnSuccessListener {
                Log.d("Firestore", "Invalid page IDs removed successfully")
            }
            .addOnFailureListener {
                Log.e("Firestore", "Error removing invalid page IDs")
            }
    }




    fun createContacts(contactDetails: ContactsModel,pageName: String, pageId: String, ownerId: String, userId: String, activity: Activity){

        if(ownerId == userId){

            val contactId = db.collection("CONTACT_PAGE").document(pageId).collection("PAGE_CONTACTS").document().id
            contactDetails.id= contactId
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
        // Get Page Details like PageName, OwnerId, PageId
        db.collection("CONTACT_PAGE").document(pageId).collection("PAGE_DETAILS").document("PAGE_DETAILS").get()
            .addOnSuccessListener { document->

                if (document.exists()) {
                    Toast.makeText(activity, "Page Found", Toast.LENGTH_SHORT).show()

                    val pageName = document.getString("pageName") ?: ""
                    val ownerId = document.getString("ownerId") ?: ""
                    val pageId = document.getString("pageId") ?: ""

                    val contactPageDetails = ContactPageDetailsModel(pageName, pageId, ownerId)

                    // Add Page to My Contact Pages
                    db.collection("MY_CONTACT_PAGES").document(userId).collection("CONTACT_PAGES").document(pageId).set(contactPageDetails)
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

    fun deleteContactPage(pageId: String, ownerId: String, activity: Activity){

        val userId = auth.currentUser?.uid.toString()

        val builder = AlertDialog.Builder(activity)
            .setTitle("Delete Contact Page")
            .setIcon(R.drawable.contact_page)
            .setMessage(if (ownerId == userId) "Are you sure you want to delete this page? It will be deleted for EVERYONE" else "Are you sure you want to delete this page? It will be deleted only for you")
            .setPositiveButton(
                "YES"
            ) { dialogInterface, i ->
                try {

                    if(ownerId == userId){
                        deleteContactPageForAll(pageId, activity)
                    }
                    else{
                        deleteContactPageForMe(pageId, userId, activity)
                    }


                } catch (e: Exception) {
                    Toast.makeText(activity, "Something Went Wrong", Toast.LENGTH_SHORT).show()
                    Log.w("crash-attendance", e)
                }

            }.setNegativeButton("NO")
            { dialogInterface, i ->
                Toast.makeText(activity, "Deletion Cancelled", Toast.LENGTH_SHORT).show()
            }
        builder.show()
    }

    fun deleteContactPageForAll(pageId: String, activity: Activity){
        db.collection("CONTACT_PAGE").document(pageId).delete()
            .addOnSuccessListener {
                Toast.makeText(activity, "Page Deleted for Everyone", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(activity, "Couldn't Delete Page", Toast.LENGTH_SHORT).show()
            }

    }

    fun deleteContactPageForMe(userId: String, pageId: String, activity: Activity) {

        //val userDocRef = db.collection("MY_CONTACT_PAGES").document(userId)

        db.collection("MY_CONTACT_PAGES").document(userId).get()
            .addOnSuccessListener { document ->

                if (document.exists()) {
                    val contactPages = document.get("ContactPages") as? List<Map<String, String>>

                    // Filter out the page with the given pageId
                    val updatedPages = contactPages?.filter { it["pageId"] != pageId }

                    // Update Firestore with the new list
                    db.collection("MY_CONTACT_PAGES").document(userId).update("ContactPages", updatedPages)
                        .addOnSuccessListener {
                            Toast.makeText(activity, "Contact Page Deleted", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(activity, "Failed to delete Contact Page", Toast.LENGTH_SHORT).show()
                        }

                } else {
                    Toast.makeText(activity, "No Contact Pages found", Toast.LENGTH_SHORT).show()
                }

            }
            .addOnFailureListener {
                    Toast.makeText(activity, "Error fetching pages", Toast.LENGTH_SHORT).show()
            }
    }

}


data class ContactPageDetailsModel(
    val pageName: String,
    val pageId: String,
    val ownerId: String
)
