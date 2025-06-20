package com.surajverma.xtracontacts.screens

import android.content.Intent
import android.os.Vibrator
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.surajverma.xtracontacts.ContactPage.AllContactsActivity
import com.surajverma.xtracontacts.ContactPage.ContactPageDetailsModel
import com.surajverma.xtracontacts.ContactPage.ContactPageViewModel
import com.surajverma.xtracontacts.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactPagesScreen(
    viewModel: ContactPageViewModel,
    auth: FirebaseAuth,
    onCreateContactPageClick: () -> Unit,
    onDiscoverClick: () -> Unit,
    vibrator: Vibrator
) {
    val contactPages by viewModel.contactPages.observeAsState(emptyList())

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.contact_pages),
                            contentDescription = "Contact Pages Logo",
                            modifier = Modifier
                                .width(200.dp)
                                .height(50.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                },
                actions = {
                    // Earth Animation Button
                    Card(
                        modifier = Modifier
                            .size(45.dp)
                            .clickable { onDiscoverClick() },
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🌍",
                                fontSize = 20.sp,
                                modifier = Modifier.clickable { onDiscoverClick() }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateContactPageClick,
                containerColor = Color.White,
                contentColor = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp, end = 16.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Contact Page",
                    tint = Color.Black
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(paddingValues)
        ) {
            if (contactPages.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 10.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(contactPages) { contactPage ->
                        ContactPageItem(
                            contactPage = contactPage,
                            auth = auth,
                            viewModel = viewModel,
                            vibrator = vibrator
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactPageItem(
    contactPage: ContactPageDetailsModel,
    auth: FirebaseAuth,
    viewModel: ContactPageViewModel,
    vibrator: Vibrator
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    vibrator.vibrate(50)
                    // Navigate to AllContactsActivity
                    val intent = Intent(context, AllContactsActivity::class.java).apply {
                        putExtra("pageName", contactPage.pageName)
                        putExtra("pageId", contactPage.pageId)
                        putExtra("ownerId", contactPage.ownerId)
                    }
                    context.startActivity(intent)
                },
                onLongClick = {
                    vibrator.vibrate(50)
                    showDialog = true
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = contactPage.pageName ?: "Unknown",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            // Admin crown icon - show only if current user is owner
            if (contactPage.ownerId == auth.currentUser?.uid) {
                Icon(
                    painter = painterResource(id = R.drawable.admin),
                    contentDescription = "Admin",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(24.dp)
                )
            }
        }
    }

    // Long press dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.contact_page),
                        contentDescription = "Contact Page",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Contact Page")
                }
            },
            text = {
                Text("Edit or Delete this Contact Page")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        // Edit functionality - currently shows toast
                        Toast.makeText(context, "Feature Not Available", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Edit Page")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        // Delete contact page
                        contactPage.pageId?.let { pageId ->
                            contactPage.ownerId?.let { ownerId ->
                                viewModel.deleteContactPage(pageId, ownerId, context as android.app.Activity)
                            }
                        }
                    }
                ) {
                    Text("Delete Page", color = Color.Red)
                }
            }
        )
    }
}
