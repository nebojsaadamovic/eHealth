package com.example.zdravlje.ui.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.model.request.PinRequest
import com.example.zdravlje.data.model.response.profile.AccountSynchronizeResponse
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.ProfileRepository
import com.example.zdravlje.ui.adaptersAndViewHolders.profile.SyncStatusAdapter
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.utilities.DateUtils
import kotlinx.coroutines.launch
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentProfileSinkBinding
import java.util.Date

class ProfileSinkFragment : BaseFragment() {


    lateinit var binding: FragmentProfileSinkBinding
    private val TAG = ProfileInitialFragment::class.java.simpleName
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var adapter: SyncStatusAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileSinkBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        subscribeToObservers()
        seutpUi()
    }

    private fun seutpUi() {
        adapter = SyncStatusAdapter(emptyList())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())


        val pinRequest = PinRequest(getStoredPin())
        profileViewModel.getSynchronizeStatus(pinRequest)


        binding.mbSink.setOnClickListener(View.OnClickListener {
            val pinRequest = PinRequest(getStoredPin())
            profileViewModel.isSynchronizeStatus(pinRequest)
        })
    }

    private fun setViewModels() {
        val profileRepository = ProfileRepository()
        dataStoreRepository = DataStoreRepository(requireActivity())
        val profileViewModelFactory =
            ProfileViewModelFactory(profileRepository, dataStoreRepository)
        profileViewModel = ViewModelProvider(
            requireActivity(),
            profileViewModelFactory
        ).get(ProfileViewModel::class.java)
    }

    @SuppressLint("SuspiciousIndentation")
    private fun subscribeToObservers() {
        profileViewModel.getSynchronizeStatusLiveData.observe(viewLifecycleOwner) { synchronizeStatus ->
            configure(synchronizeStatus)
            adapter.updateData(synchronizeStatus)
        }
        profileViewModel.isSynchronizeStatusLiveData.observe(viewLifecycleOwner) { synchronizeStatus ->
            Toast.makeText(requireActivity(), getString(R.string.fragment_sink_profile_synchronization_started), Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }
        binding.ivBack.setOnClickListener(View.OnClickListener {
            requireActivity().onBackPressed()
        })
    }

    fun configure(responses: List<AccountSynchronizeResponse>) {
        val allDates = responses.mapNotNull {
            DateUtils.stringToDate(it.useSyncCompleted ?: "/", "yyyy-MM-dd HH:mm")
        }

        val latestDate = allDates.maxOrNull()
        latestDate?.let {
            binding.lastTimeValueLabel.text = DateUtils.dateToString(it, "dd.MM.yyyy.")
        }

        var worstStatusLevel = 0
        val now = Date()

        for (date in allDates) {
            val hours = (now.time - date.time) / (1000 * 60 * 60) // milliseconds to hours
            when {
                hours > 24 -> {
                    worstStatusLevel = 2
                    break
                }

                hours > 2 -> {
                    worstStatusLevel = maxOf(worstStatusLevel, 1)
                }
            }
        }

        when (worstStatusLevel) {
            0 -> {
                binding.descriptionValueLabel.text = getString(R.string.fragment_sink_profile_last_2_hours)
                binding.descriptionValueLabel.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.medicineColor
                    )
                )
                binding.statusColorImageView.setCardBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.medicineColor
                    )
                )
            }

            1 -> {
                binding.descriptionValueLabel.text = getString(R.string.fragment_sink_profile_last_24_hours)
                binding.descriptionValueLabel.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.yellow_FFA901
                    )
                )
                binding.statusColorImageView.setCardBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.yellow_FFA901
                    )
                )
            }

            else -> {
                binding.descriptionValueLabel.text =  getString(R.string.fragment_sink_profile_more_than_24_hours)
                binding.descriptionValueLabel.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.red_warning_E75656
                    )
                )
                binding.statusColorImageView.setCardBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.red_warning_E75656
                    )
                )
            }
        }

    }
}