package com.example.zdravlje.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.zdravlje.data.repository.DashboardRepository
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.ui.base.BaseActivity
import com.example.zdravlje.ui.calendar.CalendarFragment
import com.example.zdravlje.ui.chat.ChatInboxFragment
import com.example.zdravlje.ui.fitparameters.FitParametersFragment
import com.example.zdravlje.ui.healthcare.HealthcareFragment
import com.example.zdravlje.ui.healthcare.medicalcarton.MedicalCartonFragment
import com.example.zdravlje.ui.healthcare.prescribedlistmedicaments.PrescribedListMedicamentsFragment
import com.example.zdravlje.ui.healthcare.prescribedlistmedicaments.medicamentoptions.MedicamentDetailsFragment
import com.example.zdravlje.ui.healthcare.prescribedlistmedicaments.medicamentoptions.RecipeDeatilsFragment
import com.example.zdravlje.ui.profile.settings.ProfileSettings
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.ActivityHomeBinding

class HomeActivity : BaseActivity() {

    lateinit var binding: ActivityHomeBinding
    private var lastClickTime: Long = 0

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var dataStoreRepository: DataStoreRepository

    lateinit var fab: FloatingActionButton // Definišite fab kao FloatingActionButton

    val firebaseToken =
        "cu3D9d7MSCutEyzY5El0b0:APA91bENr2Mnxtge2wDXE_kKTpqSLPcKiavA95XApA4_FabCeJMpBEwBJSTjwd3cSXorjDNSUaQNBtOy99K5pSWKR7Rjre-qYBC4lt66idb_mrF17sJCW3hEKLbDMcF9y2ny3ACJcKDG"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)

        startAVCSdk(getStoredToken(), "sr",getStoredFcmToken(), "2")

        setContentView(binding.root)
        this.window.navigationBarColor = ContextCompat.getColor(this, R.color.white)
        fab = binding.fab
        setViewModels()
        setupUi()
    }

    private fun setViewModels() {
        val dashboardRepository = DashboardRepository()
        dataStoreRepository = DataStoreRepository(this)
        val dashboardViewModelFactory =
            DashboardViewModelFactory(dashboardRepository, dataStoreRepository)

        dashboardViewModel = ViewModelProvider(
            this,
            dashboardViewModelFactory
        ).get(DashboardViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        setContentView(binding.root)
        this.window.navigationBarColor = ContextCompat.getColor(this, R.color.white_bg)

    }

    private fun setupUi() {
        checkNotificationPermission()
        if (intent.getBooleanExtra("open_calendar", false)) {
            setCurrentFragment(CalendarFragment())
        }

        //Selektovan Home ekran
        val menuItem = binding.bottomNavigationView.menu.getItem(2)
        menuItem.isChecked = true

        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            //Log.i("tts", it.itemId.toString())
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime < 100) {
                return@setOnNavigationItemSelectedListener false
            }
            //lastClickedItemId = it.itemId
            lastClickTime = currentTime


            when (it.itemId) {
                R.id.menu_item_web -> {
                    defautlHomeBtn()
                    setCurrentFragment(ChatInboxFragment())
                }

                R.id.menu_item_health -> {
                    defautlHomeBtn()
                    setCurrentFragment(HealthcareFragment())
                }

                R.id.menu_item_notification -> {
                    defautlHomeBtn()
                    setCurrentFragment(ProfileSettings())
                }

                R.id.menu_item_life_way -> {
                    defautlHomeBtn()
                    setCurrentFragment(FitParametersFragment())
                }

                R.id.menu_item_calendar -> {
                    defautlHomeBtn()
                    setCurrentFragment(CalendarFragment())
                }
            }
            true
        }

        fab.setImageResource(R.drawable.ic_home_white) // Početna ikona
        fab.setOnClickListener {
            fab.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
            dashboardViewModel.dateParem.value = null
            val menuItem = binding.bottomNavigationView.menu.getItem(2)
            menuItem.isChecked = true
            setCurrentFragment(HomeFragment())
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Notification permission granted
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.POST_NOTIFICATIONS
                )
            ) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)

            } else {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }    }

    fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.home_fragment_container_view, fragment)
            addToBackStack(null)
            commit()
        }
    }

    override fun onBackPressed() {
        val v = supportFragmentManager.findFragmentById(R.id.home_fragment_container_view)
        if (v?.javaClass?.simpleName == "HomeFragment") {
            moveTaskToBack(true)
            return
        }

        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.home_fragment_container_view)

        if (currentFragment is PrescribedListMedicamentsFragment
            || currentFragment is MedicalCartonFragment
        ) {
            setCurrentFragment(HealthcareFragment())
            return

        } else if (currentFragment is RecipeDeatilsFragment
            || currentFragment is MedicamentDetailsFragment
        ) {
            setCurrentFragment(PrescribedListMedicamentsFragment())
            return

        } else if (currentFragment !is ChatInboxFragment &&
            currentFragment !is HealthcareFragment &&
            currentFragment !is FitParametersFragment &&
            currentFragment !is CalendarFragment
        ) {
            super.onBackPressed()
            return
        }

        val selectedItem = binding.bottomNavigationView.selectedItemId
        when (selectedItem) {
            R.id.menu_item_web, R.id.menu_item_health, R.id.menu_item_notification,
            R.id.menu_item_life_way, R.id.menu_item_calendar -> setCurrentFragment(HomeFragment())

            else -> super.onBackPressed()
        }
    }

    private fun defautlHomeBtn() {
        fab.setImageResource(R.drawable.ic_home_white)
        fab.clearColorFilter()
        fab.imageTintList = null
    }
}