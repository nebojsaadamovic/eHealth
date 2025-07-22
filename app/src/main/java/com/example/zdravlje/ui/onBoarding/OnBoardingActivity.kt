package com.example.zdravlje.ui.onBoarding

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.ui.base.BaseActivity
import com.example.zdravlje.ui.login.LoginActivity
import com.example.zdravlje.ui.onBoarding.adapters.OnBoardingAdapter
import kotlinx.coroutines.launch
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.ActivityOnBoardingBinding

class OnBoardingActivity : BaseActivity(), OnBoardingAdapter.OnValueListener {

    lateinit var binding: ActivityOnBoardingBinding
    private val TAG = OnBoardingActivity::class.java.simpleName
    lateinit var titles_source: java.util.ArrayList<String>
    lateinit var icons_source: java.util.ArrayList<Drawable>
    lateinit var descriptions_source: java.util.ArrayList<String>
    lateinit var dots_source: java.util.ArrayList<Drawable>
    private lateinit var onBoardingAdapter: OnBoardingAdapter
    private lateinit var dataStoreRepository: DataStoreRepository
    val notificationValue = "notification"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        dataStoreRepository = DataStoreRepository(this)
        setContentView(binding.root)
        fillSourceArray()
        setAdapter()
        goToNextItemInRecyclerView()
    }

    private fun fillSourceArray() {
        titles_source = ArrayList<String>()
        titles_source.add(getString(R.string.onboarding_welcome_message))
        titles_source.add(getString(R.string.onboarding_parameters))
        titles_source.add(getString(R.string.onboarding_healthcare))
        titles_source.add(getString(R.string.onboarding_network))
        titles_source.add(getString(R.string.onboarding_profile_sharing))
        titles_source.add(getString(R.string.onboarding_notifications))
        icons_source = ArrayList<Drawable>()
        icons_source.add(resources.getDrawable(R.drawable.icon_card_1))
        icons_source.add(resources.getDrawable(R.drawable.icon_card_2))
        icons_source.add(resources.getDrawable(R.drawable.icon_card_3))
        icons_source.add(resources.getDrawable(R.drawable.icon_card_4))
        icons_source.add(resources.getDrawable(R.drawable.icon_card_5))
        icons_source.add(resources.getDrawable(R.drawable.icon_card_6))
        descriptions_source = ArrayList<String>()
        descriptions_source.add(getString(R.string.onboarding_ezdravlje_description))
        descriptions_source.add(getString(R.string.onboarding_biometric_data_sync))
        descriptions_source.add(getString(R.string.onboarding_stay_up_to_date))
        descriptions_source.add(getString(R.string.onboarding_contact_doctors_and_access_dependents_data))
        descriptions_source.add(getString(R.string.onboarding_sharing_profile_desc))
        descriptions_source.add(getString(R.string.onboarding_notifications_desc))
        // descriptions_source.add(getString(R.string.onboarding_version_info).trimIndent())
        dots_source = ArrayList<Drawable>()
        dots_source.add(resources.getDrawable(R.drawable.icon_dot_1))
        dots_source.add(resources.getDrawable(R.drawable.icon_dot_2))
        dots_source.add(resources.getDrawable(R.drawable.icon_dot_3))
        dots_source.add(resources.getDrawable(R.drawable.icon_dot_4))
        dots_source.add(resources.getDrawable(R.drawable.icon_dot_5))
        dots_source.add(resources.getDrawable(R.drawable.icon_dot_6))
    }

    private fun setAdapter() {
        onBoardingAdapter =
            OnBoardingAdapter(titles_source, icons_source, descriptions_source, dots_source)
        onBoardingAdapter.setOnValueListener(this)
        val linearLayoutManager =
            object : LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) {
                override fun canScrollHorizontally() = false
            }
        binding.recyclerViewCard.layoutManager = linearLayoutManager
        binding.recyclerViewCard.adapter = onBoardingAdapter

    }

    private fun goToNextItemInRecyclerView() {
        binding.mbNext.setOnClickListener {
            // Povećanje trenutne pozicije RecyclerView-a
            val layoutManager = binding.recyclerViewCard.layoutManager as LinearLayoutManager
            val currentPosition = layoutManager.findFirstVisibleItemPosition()
            layoutManager.scrollToPositionWithOffset(currentPosition + 1, 0)
            val lastPosition = layoutManager.itemCount - 1
            if (currentPosition == lastPosition) {
                // Ako je poslednja pozicija, otvori novu aktivnost
                openNextActivity()
            }
        }

        binding.mbScipe.setOnClickListener() {
            openNextActivity()
        }
    }

    private fun openNextActivity() {
        lifecycleScope.launch {
            // Postavljanje isOnBoardingPass na true
            dataStoreRepository.saveOnBoardingPass(true)

            // Otvorite LoginActivity
            val intent = Intent(this@OnBoardingActivity, LoginActivity::class.java)
            startActivity(intent)

            // Završite trenutnu aktivnost ako želite
            finish()
        }
    }

    override fun onValueListener(notification: String) {
        if (notification == notificationValue) {
            binding.mbNext.text = getString(R.string.network_login_btn)
            binding.mbScipe.visibility = View.GONE
        }
    }
}