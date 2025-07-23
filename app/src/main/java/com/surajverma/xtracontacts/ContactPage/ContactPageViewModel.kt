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
import java.io.Serializable

class ContactPageViewModel: ViewModel() {

    var auth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore by lazy { Firebase.firestore}

    private val _contactPages = MutableLiveData<List<ContactPageDetailsModel>>()
    val contactPages: LiveData<List<ContactPageDetailsModel>> get() = _contactPages

    private val _contacts = MutableLiveData<List<ContactsModel>>()
    val contacts: LiveData<List<ContactsModel>> get() = _contacts

    fun createContactsPage(pageName: String, ownerId: String, activity: Activity, onSuccess: (Boolean)-> Unit){

        val pageId = db.collection("CONTACT_PAGE").document().id
        val pageDetails = ContactPageDetailsModel(pageName, pageId, ownerId)

        db.collection("CONTACT_PAGE").document(pageId).set(pageDetails)
            .addOnSuccessListener {
                Toast.makeText(activity, "Page Created", Toast.LENGTH_SHORT).show()
                onSuccess(true)
            }
            .addOnFailureListener {
                Toast.makeText(activity, "Some error occurred creating page", Toast.LENGTH_SHORT).show()
                onSuccess(false)
            }

        // Add Page to: My Contact Pages
        //val pageIdMap = mapOf("pageId" to pageId)
        db.collection("MY_CONTACT_PAGES").document(ownerId).set(mapOf("ContactPages" to FieldValue.arrayUnion(pageId)), SetOptions.merge())
            .addOnSuccessListener {
                // Nothing
            }
            .addOnFailureListener {
                // Nothing
            }


    }

    fun addContactPage(pageId: String, userId: String ,activity: Activity, onSuccess: (Boolean)-> Unit){
        val cleanPageId = cleanPageId(pageId)  // Removes https:// & .xtra from the pageId
        db.collection("MY_CONTACT_PAGES").document(userId) .set(mapOf("ContactPages" to FieldValue.arrayUnion(cleanPageId)), SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(activity, "Page Added", Toast.LENGTH_SHORT).show()
                onSuccess(true)
            }
            .addOnFailureListener {
                Toast.makeText(activity, "Some Error Occured", Toast.LENGTH_SHORT).show()
                onSuccess(false)
            }

    }

    fun cleanPageId(id: String): String {
        return id.removePrefix("https://www.")
            .removePrefix("https://")
            .removeSuffix(".xtra")
    }


    fun fetchMyContactPages(userId: String, activity: Activity) {
        val userDocRef = db.collection("MY_CONTACT_PAGES").document(userId)

        val contactPageList = mutableListOf<ContactPageDetailsModel>()
        userDocRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val pageIdList = document.get("ContactPages") as? List<String> ?: emptyList()

                if (pageIdList.isNotEmpty()) {
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
                                    contactPageList.sortBy { it.pageName.lowercase() }
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
                    _contactPages.value = contactPageList

                }
            } else {
                Toast.makeText(activity, "You have no Contact Pages", Toast.LENGTH_SHORT).show()
                _contactPages.value = contactPageList
            }
        }.addOnFailureListener {
            Toast.makeText(activity, "Error fetching pages", Toast.LENGTH_SHORT).show()
            _contactPages.value = contactPageList
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


    fun createContacts(contactDetails: ContactsModel, userId: String, activity: Activity, onResult:(Boolean)->Unit){

        if(contactDetails.ownerId == userId){

            val contactId = db.collection("CONTACT_PAGE").document().id
            contactDetails.id= contactId
            db.collection("CONTACT_PAGE").document(contactDetails.pageId!!).set(mapOf("ContactsList" to FieldValue.arrayUnion(contactDetails)), SetOptions.merge())
                .addOnSuccessListener {
                    Toast.makeText(activity, "Contact Created", Toast.LENGTH_SHORT).show()
                    onResult(true)
                }
                .addOnFailureListener {
                    onResult(false)
                    Toast.makeText(activity, "Some error Occured", Toast.LENGTH_SHORT).show()
                }
        }

    }

    fun fetchPageContacts(pageId: String, activity: Activity) {

        if (pageId.isBlank()) {  // Prevent invalid document reference
            Toast.makeText(activity, "Invalid page reference", Toast.LENGTH_SHORT).show()
            return
        }

        val contactsList = mutableListOf<ContactsModel>()

        db.collection("CONTACT_PAGE").document(pageId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val contacts = document.get("ContactsList") as? List<Map<String, Any>> ?: emptyList()

                    if (contacts.isNotEmpty()) {
                        for (contact in contacts) {
                            val id = contact["id"] as? String ?: ""
                            val name = contact["name"] as? String ?: ""
                            val number = contact["number"] as? String ?: ""
                            val email = contact["email"] as? String ?: ""
                            val instagram = contact["instagram"] as? String ?: ""
                            val x = contact["x"] as? String ?: ""
                            val linkedin = contact["linkedin"] as? String ?: ""
                            val pageName = contact["pageName"] as? String ?: ""
                            val pageId = contact["pageId"] as? String ?: ""
                            val ownerId = contact["ownerId"] as? String ?: ""

                            contactsList.add(ContactsModel(id, name, number, email, instagram, x, linkedin, pageName, pageId, ownerId))
                        }

                        contactsList.sortBy { it.name!!.lowercase() }
                    }
                    _contacts.value = contactsList // update the list even if its empty
                }
            }
            .addOnFailureListener {
                Toast.makeText(activity, "Some Error Occurred", Toast.LENGTH_SHORT).show()
            }
    }

    fun deletePageContact(contactDetails: ContactsModel, activity: Activity, onResult:(Boolean)->Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("CONTACT_PAGE").document(contactDetails.pageId!!)
            .update("ContactsList", FieldValue.arrayRemove(contactDetails))
            .addOnSuccessListener {
                Toast.makeText(activity, "Contact Deleted", Toast.LENGTH_SHORT).show()
                onResult(true)
            }
            .addOnFailureListener {
                Toast.makeText(activity, "Failed to delete contact", Toast.LENGTH_SHORT).show()
                onResult(false)
            }
    }

    fun updateContact(contactDetails: ContactsModel, activity: Activity, onResult:(Boolean)-> Unit) {
        val contactRef = db.collection("CONTACT_PAGE").document(contactDetails.pageId!!)

        contactRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val contactsList = document.get("ContactsList") as? List<Map<String, Any>>
                val existingContact = contactsList?.find { it["id"] == contactDetails.id }

                if (existingContact != null) {
                    // Remove the old contact and add the updated one
                    contactRef.update("ContactsList", FieldValue.arrayRemove(existingContact))
                        .addOnSuccessListener {
                            contactRef.update("ContactsList", FieldValue.arrayUnion(contactDetails))
                                .addOnSuccessListener {
                                    Toast.makeText(activity, "Contact Updated", Toast.LENGTH_SHORT).show()
                                    onResult(true)
                                }
                                .addOnFailureListener {
                                    Toast.makeText(activity, "Error Updating Contact", Toast.LENGTH_SHORT).show()
                                    onResult(false)
                                }
                        }
                        .addOnFailureListener {
                            Toast.makeText(activity, "Some Error Occured", Toast.LENGTH_SHORT).show()
                            onResult(false)
                        }
                } else {
                    Toast.makeText(activity, "Contact Not Found", Toast.LENGTH_SHORT).show()
                    onResult(false)
                }
            } else {
                Toast.makeText(activity, "Page Not Found", Toast.LENGTH_SHORT).show()
                onResult(false)
            }
        }.addOnFailureListener {
            Toast.makeText(activity, "Error Fetching Data", Toast.LENGTH_SHORT).show()
            onResult(false)
        }
    }




    fun deleteContactPage(pageId: String, ownerId: String, activity: Activity, onSuccess: (Boolean) -> Unit){

        val userId = auth.currentUser?.uid.toString()

        val builder = AlertDialog.Builder(activity)
            .setTitle("Delete Contact Page")
            .setIcon(R.drawable.contact_page)
            .setMessage(if (ownerId == userId) "Page will be deleted for EVERYONE" else "Page will be deleted only for YOU")
            .setPositiveButton(
                "YES"
            ) { dialogInterface, i ->
                try {

                    if(ownerId == userId){
                        deleteContactPageForAll(pageId, activity){ isComplete->
                            onSuccess(isComplete)
                        }
                    }
                    else{
                        deleteContactPageForMe(pageId, userId, activity){ isComplete->
                            onSuccess(isComplete)
                        }
                    }


                } catch (e: Exception) {
                    Toast.makeText(activity, "Something Went Wrong", Toast.LENGTH_SHORT).show()
                    Log.w("crash-attendance", e)
                    onSuccess(false)
                }

            }.setNegativeButton("NO")
            { dialogInterface, i ->
                Toast.makeText(activity, "Deletion Cancelled", Toast.LENGTH_SHORT).show()
                onSuccess(false)
            }
        builder.show()
    }

    fun deleteContactPageForAll(pageId: String, activity: Activity, onSuccess: (Boolean) -> Unit){
        db.collection("CONTACT_PAGE").document(pageId).delete()
            .addOnSuccessListener {
                Toast.makeText(activity, "Page Deleted for Everyone", Toast.LENGTH_SHORT).show()
                onSuccess(true)
            }
            .addOnFailureListener {
                Toast.makeText(activity, "Couldn't Delete Page", Toast.LENGTH_SHORT).show()
                onSuccess(false)
            }

    }

    fun deleteContactPageForMe(pageId: String, userId: String, activity: Activity, onSuccess: (Boolean) -> Unit) {
        db.collection("MY_CONTACT_PAGES").document(userId)
            .update("ContactPages", FieldValue.arrayRemove(pageId))
            .addOnSuccessListener {
                Toast.makeText(activity, "Page Deleted", Toast.LENGTH_SHORT).show()
                onSuccess(true)
            }
            .addOnFailureListener {
                Toast.makeText(activity, "Some Error Occurred", Toast.LENGTH_SHORT).show()
                onSuccess(false)
            }
    }


}


data class ContactPageDetailsModel(
    val pageName: String,
    val pageId: String,
    val ownerId: String
) : Serializable
