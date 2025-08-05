package com.surajverma.xtracontacts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.google.firebase.auth.FirebaseAuth
import com.surajverma.xtracontacts.Authentication.AuthViewModel
import com.surajverma.xtracontacts.BulkContacts.BulkContacts
import com.surajverma.xtracontacts.ContactPage.ContactPageDetailsModel
import com.surajverma.xtracontacts.ContactPage.ContactPageViewModel
import com.surajverma.xtracontacts.DiscoverPages.DiscoverPageActivity
import com.surajverma.xtracontacts.Updater.AppUpdater
import com.surajverma.xtracontacts.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth
    val user = FirebaseAuth.getInstance().currentUser
    private lateinit var authViewModel: AuthViewModel
    private lateinit var contactViewModel: ContactViewModel
    private lateinit var vibrator: Vibrator
    private lateinit var appUpdater: AppUpdater
    private lateinit var contactPageViewModel: ContactPageViewModel

    private lateinit var appUpdateManager: AppUpdateManager
    private val updateType = AppUpdateType.FLEXIBLE
    val UPDATE_CODE = 1233

    // Fragments
    private lateinit var myContactsFragment: MyContactsFragment
    private lateinit var contactPagesFragment: ContactPagesFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // App Updater
        appUpdater = AppUpdater(this, lifecycleScope, AppUpdateType.FLEXIBLE, UPDATE_CODE)
        appUpdater.registerListener()
        appUpdater.checkForAppUpdate()

        auth = FirebaseAuth.getInstance()
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        contactViewModel = ViewModelProvider(this).get(ContactViewModel::class.java)
        contactPageViewModel = ViewModelProvider(this)[ContactPageViewModel::class.java]
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        ///////////////////// Bulk Contact ///////////////////////////
//        val bulkContacts = BulkContacts()
//        bulkContacts.AddBulkContacts(this, contactPageViewModel)
//        Toast.makeText(this, "${user?.uid}", Toast.LENGTH_SHORT).show()
        //////////////////////////////////////////////////////////////

        // Check Login
        authViewModel.checkLogin(this)

        // Initialize fragments
        myContactsFragment = MyContactsFragment()
        contactPagesFragment = ContactPagesFragment()

        // Set default fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, myContactsFragment)
                .commit()
        }

        binding.earthAnimation.playAnimation()
        binding.earthAnimation.setOnClickListener {
            val intent = Intent(this, DiscoverPageActivity::class.java)
            startActivity(intent)
        }

        binding.profileCardView.setOnClickListener {
            vibrator.vibrate(50)
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        if (user != null) {
            val profileImageUrl = user.photoUrl?.toString()
            if (!profileImageUrl.isNullOrEmpty()) {
                binding.profileImage.load(profileImageUrl) {
                    placeholder(R.drawable.person)
                    error(R.drawable.person)
                }
            }
        }

        // Bottom Navigation
        binding.bottomNavigation.setSelectedItemId(R.id.MyContacts)
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.MyContacts -> {
                    replaceFragment(myContactsFragment)
                    true
                }
                R.id.ContactPages -> {
                    replaceFragment(contactPagesFragment)
                    true
                }
                else -> false
            }
        }

        // Automatically update animation visibility when fragment changes
        supportFragmentManager.registerFragmentLifecycleCallbacks(
            object : FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                    super.onFragmentResumed(fm, f)
                    updateAnimationVisibility()
                }
            }, true
        )
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun updateAnimationVisibility() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (currentFragment is ContactPagesFragment && currentFragment.isVisible) {
            binding.discoverPagesCardView.visibility = View.VISIBLE
        } else {
            binding.discoverPagesCardView.visibility = View.GONE
        }
    }


    // Getter methods for fragments to access ViewModels and other components
    fun getContactViewModel(): ContactViewModel = contactViewModel
    fun getContactPageViewModel(): ContactPageViewModel = contactPageViewModel
    fun getAuthViewModel(): AuthViewModel = authViewModel
    fun getVibrator(): Vibrator = vibrator

    override fun onResume() {
        super.onResume()
        contactViewModel.fetchAllContacts(this)
        appUpdater.resumeUpdateIfNeeded()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (appUpdater.isUpdateRequest(requestCode)) {
            if (resultCode != RESULT_OK) {
                println("Something went wrong while updating...")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        appUpdater.unregisterListener()
    }
}