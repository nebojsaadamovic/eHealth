package com.example.zdravlje.ui.notification

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.model.request.notification.NotificationReadRequest
import com.example.zdravlje.data.model.request.notification.NotificationSearchRequest
import com.example.zdravlje.data.model.response.notification.NotificationResponse
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.notification.NotificationRepository
import com.example.zdravlje.ui.adaptersAndViewHolders.notification.NotificationAdapter
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.healthcare.medicalcarton.MedicalCartonSortDialog
import com.example.zdravlje.ui.login.PinBottomSheetFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentNotificationBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NotificationFragment(
    private var selectedEventTypes: MutableList<Int>? = null,
    private val selectedNotificationTypes: MutableList<Int>? = null,
    var dateFrom: String? = null,
    var dateTo: String? = null,
    val className: String? = null
) : BaseFragment(),  MedicalCartonSortDialog.IMedicalCartonSortOptions,  PinBottomSheetFragment.OnPinListener {

    lateinit var binding: FragmentNotificationBinding
    private lateinit var notificationViewModel: NotificationViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var notificationAdapter: NotificationAdapter
    private var notificationList: List<NotificationResponse>? = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewModels()
        showHideShimmer(true)
        observePin()
        setupUI()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupUI() {
        binding.ivSort.setOnClickListener {
            val medicalCartonSortDialog = MedicalCartonSortDialog(requireContext(), this)
            medicalCartonSortDialog.setCanceledOnTouchOutside(true)
            medicalCartonSortDialog.show()
        }

            binding.ivSettings.setOnClickListener(View.OnClickListener {
                openNotificationSettingsFragment()
            })

            binding.ivFilter.setOnClickListener(View.OnClickListener {
                openNotificationFilterFragment()
            })

            binding.ivBack.setOnClickListener() {
                requireActivity().finish()
            }

            binding.ivReadAllMsg.setOnClickListener {
                lifecycleScope.launch {
                    val response = notificationViewModel.getNotificationsLiveData.value
                    if (response?.isSuccessful == true) {
                        notificationList = response.body() ?: emptyList()

                        val unreadNotIds = notificationList!!
                            .filter { it.notification.notStatus == 0 }
                            .map { it.notification.notId }

                        if (unreadNotIds.isNotEmpty()) {
                            notificationViewModel.setNotificationRead(
                                NotificationReadRequest(pin = getStoredPin(), notIds = unreadNotIds)
                            )

                            val currentTime = getCurrentTime()
                            val formattedDateFrom = dateFrom?.takeIf { it.isNotEmpty() }
                                ?.let { convertToExpectedFormat(it) }
                            val formattedDateTo =
                                dateTo?.takeIf { it.isNotEmpty() }?.let { convertToExpectedFormat(it) }
                                    ?: currentTime

                            val notificationSearchRequest = NotificationSearchRequest(
                                pin = getStoredPin(),
                                sort = "DESC",
                                limit = 1000,
                                offset = 0,
                                expectedArrivalTo = formattedDateTo
                            ).apply {
                                if (formattedDateFrom != null) expectedArrivalFrom = formattedDateFrom
                            }

                            notificationViewModel.getNotificationsLiveData.value = null
                            notificationViewModel.getNotifications(notificationSearchRequest)
                            delay(1000)
                            notificationViewModel.getNotificationsLiveData.observe(
                                viewLifecycleOwner,
                                Observer { response ->
                                    if (response != null && response.isSuccessful) {
                                        response.body()?.let { data ->
                                            if (data.isNotEmpty()) {
                                                Toast.makeText(
                                                    requireContext(),
                                                    getString(R.string.fragment_notification_all_marked_as_read),
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                notificationList = response.body() ?: emptyList()
                                                notificationAdapter.updateData(notificationList!!)
                                            } else {
                                                println("Nema rekorda.")
                                            }
                                        }
                                    } else {
                                        println("Greška prilikom učitavanja novih notifikacija.")
                                    }
                                })
                        }
                    }
                }
            }
        }
//        else {
//            showHideShimmer(false)
//        }
//            binding.wrapper.visibility = View.VISIBLE
//            binding.notificationRecyclerView.visibility = View.GONE
//
//            val mbpinCode =
//                binding.wrapper.findViewById<MaterialButton>(R.id.mbEnterPinCode)
//            mbpinCode.setOnClickListener {
//                val pinBottomSheetFragment = PinBottomSheetFragment()
//                pinBottomSheetFragment.setOnPinListener(this)
//                pinBottomSheetFragment.show(parentFragmentManager, pinBottomSheetFragment.tag)
//            }
//
//            val mtvNetwork = binding.wrapper.findViewById<MaterialTextView>(R.id.mtvNetwork)
//            mtvNetwork.text = "Obaveštenja"
//
//            val imageMsg = binding.wrapper.findViewById<ImageView>(R.id.ivMsg)
//            imageMsg.setImageResource(R.drawable.ic_medical_carton_wrapper)
//
//            val mbLogInButton = binding.wrapper.findViewById<MaterialButton>(R.id.mbLogIn)
//            mbLogInButton.setOnClickListener(View.OnClickListener {
//                val intent = Intent(requireActivity(), LoginActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                startActivity(intent)
//            })
//
//            val ivBackButton = binding.wrapper.findViewById<ImageView>(R.id.ivBack)
//            ivBackButton.setOnClickListener {
//                requireActivity().onBackPressed()
//            }
//        }
//    }

    private fun setAdapter(
        //eventValues: List<Int>? = null,
        notifyValues: List<Int>? = null,
        dateFrom: String? = null,
        dateTo: String? = null
    ) {
        val currentTime = getCurrentTime()
        val formattedDateFrom =
            if (!dateFrom.isNullOrEmpty()) convertToExpectedFormat(dateFrom) else null
        val formattedDateTo =
            if (!dateTo.isNullOrEmpty()) convertToExpectedFormat(dateTo) else currentTime

        if (
//            eventValues != null ||
            notifyValues != null) {
            notificationViewModel.getNotificationsLiveData.value = null

            val notificationSearchRequest = NotificationSearchRequest(
                pin = getStoredPin(),
                sort = "DESC",
                limit = 1000,
                offset = 0,
                expectedArrivalTo = formattedDateTo,
                //eventTypeId = eventValues ?: emptyList(),
                notificationTypeId = notifyValues ?: emptyList()
            )
            if (formattedDateFrom != null) {
                notificationSearchRequest.expectedArrivalFrom = formattedDateFrom
            }

            notificationViewModel.getNotifications(notificationSearchRequest)

        } else {
            notificationViewModel.getNotificationsLiveData.value = null
            notificationViewModel.getNotifications(
                NotificationSearchRequest(
                    pin = getStoredPin(),
                    sort = "DESC",
                    limit = 1000,
                    offset = 0,
                    expectedArrivalTo = currentTime
                )
            )
        }

        notificationViewModel.getNotificationsLiveData.observe(
            viewLifecycleOwner,
            Observer { response ->
                if (response != null && response.isSuccessful) {

                    showHideShimmer(false)
                    response.body()?.let { data ->
                        if (data.isNotEmpty()) {
                            notificationAdapter = NotificationAdapter(
                                data,
                                notificationViewModel,
                                getStoredPin(),
                                requireActivity().supportFragmentManager,
                                requireContext(),
                                lifecycleOwner = this
                            )
                            binding.notificationRecyclerView.apply {
                                layoutManager = LinearLayoutManager(requireContext())
                                adapter = notificationAdapter
                                setHasFixedSize(true)
                            }
//                            val message = "${getString(R.string.fragment_notification_data_for_period)} $formattedDateFrom " +
//                                    "${getString(R.string.fragment_notification_to)} ${
//                                        dateTo ?: DateUtils.convertYYYYDDMMHHssToDotDDMMYYYY(
//                                            formattedDateTo
//                                        )
//                                    }"
//
//                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

//                            Toast.makeText(
//                                context,
//                                "${getString(R.string.fragment_notification_loaded)} ${data.size} ${
//                                    getString(R.string.fragment_notification_records)
//                                }",
//                                Toast.LENGTH_SHORT
//                            ).show()
                        } else {
                            binding.clWrapperData.visibility = View.VISIBLE
                        }
                    }
                } else {
                    Log.d("Failed", "Failed to load notifications")
                }
            }
        )
    }

    private fun setUpViewModels() {
        val notificationRepository = NotificationRepository()
        dataStoreRepository = DataStoreRepository(requireActivity())
        val notificationViewModelFactory =
            NotificationViewModelFactory(notificationRepository, dataStoreRepository)
        notificationViewModel = ViewModelProvider(requireActivity(), notificationViewModelFactory)
            .get(NotificationViewModel::class.java)
    }

    private fun observePin() {
        if (isPinSet()) {
            binding.ivFilter.visibility = View.VISIBLE
            binding.ivSettings.visibility = View.VISIBLE
            binding.ivReadAllMsg.visibility = View.VISIBLE
        } else {
            binding.ivFilter.visibility = View.GONE
            binding.ivSettings.visibility = View.GONE
            binding.ivReadAllMsg.visibility = View.GONE
            binding.ivSort.visibility = View.VISIBLE

        }

        if (className == "NotificationFilterFragment") {
            setAdapterFilter(
                selectedEventTypes,
                selectedNotificationTypes
            )
        } else {
            setAdapter(
                selectedNotificationTypes, dateFrom, dateTo
            )
        }
    }

    private fun openNotificationSettingsFragment() {
        val notificationSettings = NotificationSettingsFragment()
        val activity = requireActivity()

        if (activity is NotificationActivity) {
            println("nnnnnnnnnnnnnnnn")
            activity.startNewFragmentFromNotification(notificationSettings)
        } else {
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container_view, notificationSettings)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun openNotificationFilterFragment() {
        val notificationFilterFragment = NotificationFilterFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.notification_fragment_container_view, notificationFilterFragment)
            .addToBackStack(null).commit()
    }

    fun getCurrentTime(): String {
        val currentDate = Date()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return sdf.format(currentDate)
    }

    private fun openFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.notification_fragment_container_view, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun convertToExpectedFormat(inputDate: String): String {
        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentTime = Calendar.getInstance()
        val currentTimeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentFormattedTime = currentTimeFormat.format(currentTime.time)

        val date = inputFormat.parse(inputDate)
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = outputFormat.format(date)
        return "$formattedDate $currentFormattedTime"
    }

    private fun showHideShimmer(boolean: Boolean) {
        if (boolean == true) {
            binding.shimmer.visibility = View.VISIBLE
        } else {
            binding.shimmer.visibility = View.GONE
        }
    }

    private fun setAdapterFilter(
        eventValues: List<Int>? = null,
        notifyValues: List<Int>? = null,
//        dateTo: String? = null
    ) {
        val currentTime = getCurrentTime()
        if (notifyValues != null) {
            if (notifyValues.isNotEmpty()) {
                notificationViewModel.getNotificationsLiveData.value = null

                val notificationSearchRequest = NotificationSearchRequest(
                    pin = getStoredPin(),
                    sort = "DESC",
                    limit = 1000,
                    offset = 0,
                    expectedArrivalTo = currentTime,
                    //eventTypeId = eventValues ?: emptyList(),
                    notificationTypeId = notifyValues ?:  emptyList(),
                )

                notificationViewModel.getNotifications(notificationSearchRequest)

            } else {
                notificationViewModel.getNotificationsLiveData.value = null
                notificationViewModel.getNotifications(
                    NotificationSearchRequest(
                        pin = getStoredPin(),
                        sort = "DESC",
                        limit = 1000,
                        offset = 0,
                        eventTypeId = eventValues,
                        expectedArrivalTo = currentTime
                    )
                )
            }
        }

        notificationViewModel.getNotificationsLiveData.observe(
            viewLifecycleOwner,
            Observer { response ->
                if (response != null && response.isSuccessful) {
                    showHideShimmer(false)
                    response.body()?.let { data ->
                        if (data.isNotEmpty()) {
                            notificationAdapter = NotificationAdapter(
                                data,
                                notificationViewModel,
                                getStoredPin(),
                                requireActivity().supportFragmentManager,
                                requireContext(),
                                lifecycleOwner = this
                            )
                            binding.notificationRecyclerView.apply {
                                layoutManager = LinearLayoutManager(requireContext())
                                adapter = notificationAdapter
                                setHasFixedSize(true)
                            }

                            val message = "${getString(R.string.fragment_notification_data_for_period)} " +
                                    "${getString(R.string.fragment_notification_to)} $currentTime"

                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                            Toast.makeText(
                                context,
                                "${getString(R.string.fragment_notification_loaded)} ${data.size} ${
                                    getString(R.string.fragment_notification_records)
                                }",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            binding.clWrapperData.visibility = View.VISIBLE
                        }
                    }
                } else {
                    Log.d("Failed", "Failed to load notifications")
                }
            }
        )
    }

    override fun onPinChange() {
        binding.wrapper.visibility = View.GONE
    }

    override fun onPinDismiss() {
        binding.wrapper.visibility = View.VISIBLE
    }


    override fun onShowSortDescendingDialog() {
        notificationViewModel.getNotifications(
            NotificationSearchRequest(
                limit = 1000,
                notificationTypeId = emptyList(),
                expectedArrivalTo = getCurrentTime(),
                offset = 0,
                pin = getStoredPin(),
                sort = "DESC"
            )
        )
    }

    override fun onShowSortAscendingDialog() {
        notificationViewModel.getNotifications(
            NotificationSearchRequest(
                limit = 1000,
                notificationTypeId = emptyList(),
                expectedArrivalTo = getCurrentTime(),
                offset = 0,
                pin = getStoredPin(),
                sort = "ASC"
            )
        )
    }
}