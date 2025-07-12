package com.surajverma.xtracontacts


data class ContactsModel(
    var id: String?="",
    var name: String?="",
    var number: String?="",
    var email: String?="",
    var instagram: String?="",
    var x: String?="",
    var linkedin: String?="",
    var pageName: String?="", // for contact page only
    var pageId: String?="",  // for contact page only
    var ownerId: String?=""  // for contact page only
)