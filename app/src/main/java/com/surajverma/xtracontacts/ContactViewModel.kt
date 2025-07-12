package com.surajverma.xtracontacts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ContactViewModel: ViewModel() {

    lateinit var auth: FirebaseAuth
    private val db: FirebaseFirestore by lazy { Firebase.firestore}
    private val _contacts = MutableLiveData<List<ContactsModel>>()
    val contacts: LiveData<List<ContactsModel>> get() = _contacts

    fun createContact(contactDetails: ContactsModel, activity: Activity, onResult:(Boolean)->Unit){
        auth=FirebaseAuth.getInstance()
        val USER_ID= auth.currentUser?.uid.toString()
        val CONTACT_ID = db.collection("CONTACTS").document(USER_ID).collection("USER_CONTACTS").document().id
        contactDetails.id= CONTACT_ID   //  Assign ID to each Contact

        db.collection("CONTACTS").document(USER_ID).collection("USER_CONTACTS").document(CONTACT_ID).set(contactDetails)
            .addOnSuccessListener {
                Toast.makeText(activity, "Contact Created", Toast.LENGTH_SHORT).show()
                onResult(true)

            }
            .addOnFailureListener {
                Toast.makeText(activity, "Some error occured creating contact", Toast.LENGTH_SHORT).show()
                onResult(false)
            }
    }

    fun fetchAllContacts(activity: Activity){
        auth=FirebaseAuth.getInstance()
        val USER_ID= auth.currentUser?.uid.toString()

        val reference = db.collection("CONTACTS").document(USER_ID).collection("USER_CONTACTS")
        reference.orderBy("name", Query.Direction.ASCENDING).get()
            .addOnSuccessListener {
                //Toast.makeText(activity, "Contacts Fetched", Toast.LENGTH_SHORT).show()

                val contactList = mutableListOf<ContactsModel>()

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
                Toast.makeText(activity, "Some error occured fetching contact", Toast.LENGTH_SHORT).show()
            }

    }

    fun updateContact(contactDetails:ContactsModel, activity: Activity, onResult:(Boolean)->Unit){
        auth=FirebaseAuth.getInstance()
        val USER_ID= auth.currentUser?.uid.toString()
        val CONTACT_ID = contactDetails.id!!  //  Assign ID to each Contact

        db.collection("CONTACTS").document(USER_ID).collection("USER_CONTACTS").document(CONTACT_ID).set(contactDetails)
            .addOnSuccessListener {
                Toast.makeText(activity, "Contact Edited", Toast.LENGTH_SHORT).show()
                onResult(true)
            }
            .addOnFailureListener {
                Toast.makeText(activity, "Some error occured while updating", Toast.LENGTH_SHORT).show()
                onResult(false)
            }

    }

    fun deleteContact(contactID: String, activity: Activity, onResult: (Boolean)-> Unit){
        auth=FirebaseAuth.getInstance()
        val USER_ID= auth.currentUser?.uid.toString()
        val CONTACT_ID = contactID  //  Assign ID to each Contact

        db.collection("CONTACTS").document(USER_ID).collection("USER_CONTACTS").document(CONTACT_ID).delete()
            .addOnSuccessListener {
                Toast.makeText(activity, "Contact Deleted", Toast.LENGTH_SHORT).show()
                onResult(true)

            }
            .addOnFailureListener {
                Toast.makeText(activity, "Some error occured Deleting contact", Toast.LENGTH_SHORT).show()
                onResult(false)
            }

    }

}