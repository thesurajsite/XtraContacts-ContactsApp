package com.surajverma.xtracontacts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pages
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.google.firebase.auth.FirebaseAuth
import com.surajverma.xtracontacts.ContactPage.ContactPageViewModel
import com.surajverma.xtracontacts.ContactPage.CreateContactPageActivity
import com.surajverma.xtracontacts.DiscoverPages.DiscoverPageActivity
import com.surajverma.xtracontacts.screens.ContactPagesScreen
import com.surajverma.xtracontacts.screens.MyContactsScreen
import com.surajverma.xtracontacts.ui.theme.XtraContactsTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class MainActivity : ComponentActivity() {

    lateinit var auth: FirebaseAuth
    private lateinit var authViewModel: AuthViewModel
    private lateinit var contactViewModel: ContactViewModel
    private lateinit var contactPageViewModel: ContactPageViewModel
    private lateinit var vibrator: Vibrator
    private lateinit var appUpdateManager: AppUpdateManager
    private val updateType = AppUpdateType.FLEXIBLE
    val UPDATE_CODE = 1233

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize components
        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        if (updateType == AppUpdateType.FLEXIBLE) {
            appUpdateManager.registerListener(installStateUpdateListener)
        }

        checkForAppUpdate()

        auth = FirebaseAuth.getInstance()
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        contactViewModel = ViewModelProvider(this).get(ContactViewModel::class.java)
        contactPageViewModel = ViewModelProvider(this).get(ContactPageViewModel::class.java)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // Check Login
        authViewModel.checkLogin(this)

        // Fetch contacts
        contactViewModel.fetchAllContacts(this)

        // Fetch contact pages
        val userId = auth.currentUser?.uid.toString()
        contactPageViewModel.fetchMyContactPages(userId, this)

        setContent {
            XtraContactsTheme {
                MainAppContent(
                    contactViewModel = contactViewModel,
                    contactPageViewModel = contactPageViewModel,
                    auth = auth,
                    vibrator = vibrator,
                    onAddContactClick = {
                        vibrator.vibrate(50)
                        val intent = Intent(this, AddContactActivity::class.java)
                        startActivity(intent)
                    },
                    onProfileClick = {
                        vibrator.vibrate(50)
                        val intent = Intent(this, ProfileActivity::class.java)
                        startActivity(intent)
                    },
                    onCreateContactPageClick = {
                        vibrator.vibrate(50)
                        val intent = Intent(this, CreateContactPageActivity::class.java)
                        startActivity(intent)
                    },
                    onDiscoverClick = {
                        vibrator.vibrate(50)
                        startActivity(Intent(this, DiscoverPageActivity::class.java))
                    }
                )
            }
        }
    }

    private val installStateUpdateListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            Toast.makeText(applicationContext, "Update Successful, Restarting in 5 Seconds", Toast.LENGTH_LONG).show()
        }
        lifecycleScope.launch {
            delay(5.seconds)
            appUpdateManager.completeUpdate()
        }
    }

    private fun checkForAppUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            val isUpdateAvailable = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateAllowed = when (updateType) {
                AppUpdateType.FLEXIBLE -> info.isFlexibleUpdateAllowed
                AppUpdateType.IMMEDIATE -> info.isImmediateUpdateAllowed
                else -> false
            }

            if (isUpdateAvailable && isUpdateAllowed) {
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    updateType,
                    this,
                    UPDATE_CODE
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (updateType == AppUpdateType.IMMEDIATE) {
            appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
                if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    appUpdateManager.startUpdateFlowForResult(
                        info,
                        updateType,
                        this,
                        UPDATE_CODE
                    )
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPDATE_CODE) {
            if (resultCode != RESULT_OK) {
                println("Something went wrong while updating...")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (updateType == AppUpdateType.FLEXIBLE) {
            appUpdateManager.unregisterListener(installStateUpdateListener)
        }
    }
}

@Composable
fun MainAppContent(
    contactViewModel: ContactViewModel,
    contactPageViewModel: ContactPageViewModel,
    auth: FirebaseAuth,
    vibrator: Vibrator,
    onAddContactClick: () -> Unit,
    onProfileClick: () -> Unit,
    onCreateContactPageClick: () -> Unit,
    onDiscoverClick: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "My Contacts") },
                    label = { Text("My Contacts") },
                    selected = currentDestination?.route == "contacts",
                    onClick = {
                        vibrator.vibrate(50)
                        navController.navigate("contacts") {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Pages, contentDescription = "Contact Pages") },
                    label = { Text("Contact Pages") },
                    selected = currentDestination?.route == "contact_pages",
                    onClick = {
                        vibrator.vibrate(50)
                        navController.navigate("contact_pages") {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "contacts",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("contacts") {
                MyContactsScreen(
                    contactViewModel = contactViewModel,
                    onAddContactClick = onAddContactClick,
                    onProfileClick = onProfileClick,
                    vibrator = vibrator
                )
            }
            composable("contact_pages") {
                ContactPagesScreen(
                    viewModel = contactPageViewModel,
                    auth = auth,
                    onCreateContactPageClick = onCreateContactPageClick,
                    onDiscoverClick = onDiscoverClick,
                    vibrator = vibrator
                )
            }
        }
    }
}