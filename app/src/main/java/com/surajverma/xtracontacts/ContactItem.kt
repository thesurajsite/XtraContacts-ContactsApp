package com.surajverma.xtracontacts

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Vibrator
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ContactItem(
    contact: ContactsModel,
    vibrator: Vibrator,
    isContactPage: Boolean
) {
    var isExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                vibrator.vibrate(50)
                isExpanded = !isExpanded
            },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Contact Image
                Card(
                    modifier = Modifier.size(50.dp),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = Color.Black)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.contact_image),
                        contentDescription = "Contact Image",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Contact Info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = contact.name ?: "",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Text(
                        text = contact.number ?: "",
                        fontSize = 15.sp,
                        color = Color.Black
                    )
                }

                // Share Button (visible when expanded)
                if (isExpanded) {
                    IconButton(
                        onClick = {
                            vibrator.vibrate(50)
                            shareContact(context, contact)
                        }
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share",
                            modifier = Modifier.size(25.dp)
                        )
                    }
                }
            }

            // Expanded action buttons
            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Call Button
                    if (!contact.number.isNullOrEmpty()) {
                        item {
                            ActionButton(
                                icon = Icons.Default.Call,
                                contentDescription = "Call",
                                onClick = {
                                    vibrator.vibrate(50)
                                    callContact(context, contact)
                                }
                            )
                        }
                    }

                    // WhatsApp Button
                    if (!contact.number.isNullOrEmpty()) {
                        item {
                            ActionButton(
                                icon = Icons.Default.Message, // Better icon for WhatsApp
                                contentDescription = "WhatsApp",
                                onClick = {
                                    vibrator.vibrate(50)
                                    openWhatsApp(context, contact)
                                }
                            )
                        }
                    }

                    // Email Button
                    if (!contact.email.isNullOrEmpty()) {
                        item {
                            ActionButton(
                                icon = Icons.Default.Email,
                                contentDescription = "Email",
                                onClick = {
                                    vibrator.vibrate(50)
                                    openEmail(context, contact)
                                }
                            )
                        }
                    }

                    // Instagram Button
                    if (!contact.instagram.isNullOrEmpty()) {
                        item {
                            ActionButton(
                                icon = Icons.Default.CameraAlt, // Better placeholder for Instagram
                                contentDescription = "Instagram",
                                onClick = {
                                    vibrator.vibrate(50)
                                    openInstagram(context, contact)
                                }
                            )
                        }
                    }

                    // X Button
                    if (!contact.x.isNullOrEmpty()) {
                        item {
                            ActionButton(
                                icon = Icons.Default.Share, // Placeholder for X/Twitter
                                contentDescription = "X (Twitter)",
                                onClick = {
                                    vibrator.vibrate(50)
                                    openX(context, contact)
                                }
                            )
                        }
                    }

                    // LinkedIn Button
                    if (!contact.linkedin.isNullOrEmpty()) {
                        item {
                            ActionButton(
                                icon = Icons.Default.Work, // Better placeholder for LinkedIn
                                contentDescription = "LinkedIn",
                                onClick = {
                                    vibrator.vibrate(50)
                                    openLinkedIn(context, contact)
                                }
                            )
                        }
                    }

                    // Edit Button
                    item {
                        ActionButton(
                            icon = Icons.Default.Edit,
                            contentDescription = "Edit",
                            onClick = {
                                vibrator.vibrate(50)
                                editContact(context, contact, isContactPage)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActionButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.size(40.dp),
        shape = CircleShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

// Helper functions for contact actions
private fun callContact(context: Context, contact: ContactsModel) {
    if (!contact.number.isNullOrEmpty()) {
        Toast.makeText(context, "Calling ${contact.name}", Toast.LENGTH_SHORT).show()
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.parse("tel:${contact.number}")
        context.startActivity(callIntent)
    }
}

private fun openWhatsApp(context: Context, contact: ContactsModel) {
    val userNumberWithSpaces = contact.number ?: ""
    val userNumberWithoutSpaces = userNumberWithSpaces.filterNot { it.isWhitespace() || it == '+' }
    val length = userNumberWithoutSpaces.length

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

private fun openEmail(context: Context, contact: ContactsModel) {
    val emailID = contact.email
    if (!emailID.isNullOrEmpty()) {
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

private fun openInstagram(context: Context, contact: ContactsModel) {
    var instaID = contact.instagram
    if (!instaID.isNullOrEmpty()) {
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

private fun openX(context: Context, contact: ContactsModel) {
    var xID = contact.x
    if (!xID.isNullOrEmpty()) {
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

private fun openLinkedIn(context: Context, contact: ContactsModel) {
    var linkedinID = contact.linkedin
    if (!linkedinID.isNullOrEmpty()) {
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

private fun editContact(context: Context, contact: ContactsModel, isContactPage: Boolean) {
    val intent = Intent(context, UpdateContactActivity::class.java)
    intent.putExtra("id", contact.id)
    intent.putExtra("name", contact.name)
    intent.putExtra("number", contact.number)
    intent.putExtra("email", contact.email)
    intent.putExtra("instagram", contact.instagram)
    intent.putExtra("x", contact.x)
    intent.putExtra("linkedin", contact.linkedin)
    intent.putExtra("pageName", contact.pageName)
    intent.putExtra("pageId", contact.pageId)
    intent.putExtra("ownerId", contact.ownerId)
    intent.putExtra("isContactPage", isContactPage)
    context.startActivity(intent)
}

private fun shareContact(context: Context, contact: ContactsModel) {
    val name = contact.name
    val number = contact.number
    val email = contact.email
    val instagram = contact.instagram
    val x = contact.x
    val linkedin = contact.linkedin

    var shareText = "$name \n"
    if (!number.isNullOrEmpty()) {
        shareText += "Number: $number \n"
    }
    if (!email.isNullOrEmpty()) {
        shareText += "Email: $email \n"
    }
    if (!instagram.isNullOrEmpty()) {
        shareText += "Instagram: instagram.com/$instagram \n"
    }
    if (!x.isNullOrEmpty()) {
        shareText += "X: x.com/$x \n"
    }
    if (!linkedin.isNullOrEmpty()) {
        shareText += "LinkedIn: linkedin.com/in/$linkedin"
    }

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share via"))
}