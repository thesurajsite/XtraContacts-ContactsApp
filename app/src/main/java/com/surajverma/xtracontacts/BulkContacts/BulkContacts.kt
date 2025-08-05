package com.surajverma.xtracontacts.BulkContacts

import android.app.Activity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.surajverma.xtracontacts.ContactPage.ContactPageViewModel
import com.surajverma.xtracontacts.ContactsModel

class BulkContacts {

    fun AddBulkContacts(context: Activity, contactPageViewModel: ContactPageViewModel){

        val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val pageName = "1000+ HR List"
        val pageId = "4bPJiiaa0SEmLug4PE6i"  // enter id without https://

        val contactsList = listOf(
            ContactsModel(name = "Aarti Industries Ltd.: Sarthak Jain", linkedin = "sarthak-jain"),
            ContactsModel(name = "Aarti Industries Ltd.: Vishal Rautray", linkedin = "vishal-rautray"),
            ContactsModel(name = "Aarti Industries Ltd.: Abhishek Toppo", linkedin = "abhishek-toppo"),
            ContactsModel(name = "Daimler: Lipika Chaudhry", linkedin = "lipika-chaudhry"),

        )

        contactPageViewModel.createBulkContacts(userId, pageName, pageId, contactsList, context) { success ->
            if (success) {
                Toast.makeText(context, "Bulk Page Success", Toast.LENGTH_SHORT).show()
            }
        }
    }
}