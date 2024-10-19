package com.surajverma.xtracontacts

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.google.firebase.auth.FirebaseAuth
import com.surajverma.xtracontacts.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth
    private lateinit var authViewModel: AuthViewModel
    private lateinit var contactViewModel: ContactViewModel
    lateinit var arrContact: ArrayList<ContactsModel>

    private lateinit var appUpdateManager: AppUpdateManager
    private val updateType= AppUpdateType.FLEXIBLE
    val UPDATE_CODE=1233


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        appUpdateManager= AppUpdateManagerFactory.create(applicationContext)
        if(updateType==AppUpdateType.FLEXIBLE){
            appUpdateManager.registerListener(installStateUpdateListener)
        }

        checkForAppUpdate()

        auth=FirebaseAuth.getInstance()
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        contactViewModel= ViewModelProvider(this).get(ContactViewModel::class.java)

        // Check Login
        authViewModel.checkLogin(this)

        // RecyclerView
        binding.recyclerView.layoutManager= LinearLayoutManager(this)
        binding.recyclerView.setHasFixedSize(true)
        arrContact= ArrayList<ContactsModel>()
        val recyclerAdapter= RecyclerContactAdapter(this, arrContact)
        binding.recyclerView.adapter = recyclerAdapter

        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(this, AddContactActivity::class.java)
            startActivity(intent)
        }

        // Observe Livedata
        binding.Progressbar.visibility = View.VISIBLE
        contactViewModel.contacts.observe(this, Observer {
            arrContact.clear()
            arrContact.addAll(it)
            recyclerAdapter.notifyDataSetChanged()
            binding.Progressbar.visibility = View.GONE

        })

        // FETCH CONTACTS
        contactViewModel.fetchAllContacts(this)


    }

    private val installStateUpdateListener= InstallStateUpdatedListener{ state->
        if(state.installStatus()== InstallStatus.DOWNLOADED){
            Toast.makeText(applicationContext, "Update Successful, Restarting in 5 Seconds", Toast.LENGTH_LONG).show()
        }
        lifecycleScope.launch {
            delay(5.seconds)
            appUpdateManager.completeUpdate()
        }
    }

    private fun checkForAppUpdate(){
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info->
            val isUpdateAvailable=info.updateAvailability()== UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateAllowed=when(updateType){
                AppUpdateType.FLEXIBLE->info.isFlexibleUpdateAllowed
                AppUpdateType.IMMEDIATE->info.isImmediateUpdateAllowed
                else-> false
            }

            if(isUpdateAvailable && isUpdateAllowed){
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
        if(updateType==AppUpdateType.IMMEDIATE){
            appUpdateManager.appUpdateInfo.addOnSuccessListener { info->
                if(info.updateAvailability()==UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS){
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
        if(resultCode==UPDATE_CODE){
            if(resultCode!= RESULT_OK){
                println("Somerthing went wrong while updating...")
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if(updateType==AppUpdateType.FLEXIBLE){
            appUpdateManager.unregisterListener(installStateUpdateListener)
        }

    }
}