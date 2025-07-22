package com.example.zdravlje.ui.chat.scheduleCall

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.model.chat.ScheduleCallData
import com.example.zdravlje.data.model.response.chat.Participant
import com.example.zdravlje.data.model.response.dashboard.future.Event
import com.example.zdravlje.ui.adaptersAndViewHolders.chat.schedulecall.ScheduleCallAdapter
import com.example.zdravlje.ui.healthcare.screeningexaminations.EnterEmailAndNameBottomSheetFragment
import com.example.zdravlje.ui.home.dashboard.event_details.ScheduledCallInfoFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentScheduleCallParticipiantBinding

class ScheduleCallParticipiantFragment(
    private var dataList: List<Participant>,
    private val classFragment: String? = null,
    private var event: Event? = null,
    private var selectedParticipants: List<Participant> = emptyList()
) : Fragment(), ScheduleCallAdapter.OnParticipantListener, EnterEmailAndNameBottomSheetFragment.OnValueListener {

    private lateinit var binding: FragmentScheduleCallParticipiantBinding
    lateinit var scheduleCallAdapter: ScheduleCallAdapter
    private var selectedListParticipant: List<Participant> = emptyList()
    private var dataScheduleCallParcelable: ScheduleCallData? = null
    private val fieldTag: String = "ScheduleCallParticipiantFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScheduleCallParticipiantBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        dataScheduleCallParcelable = arguments?.getParcelable("scheduleCallData")
    }

    private fun setupUI() {
        scheduleCallAdapter = ScheduleCallAdapter(dataList, selectedParticipants)
        scheduleCallAdapter.setOnParticipantListener(this)
        binding.recyclerViewParticipiants.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewParticipiants.adapter = scheduleCallAdapter
        val title = getString(R.string.fragment_calendar_appointment_examination_header_title)
        binding.mtvAdd.setOnClickListener {
            if (selectedListParticipant.isNotEmpty()) {
                val bundle = Bundle().apply {
                    putParcelableArrayList(
                        "selectedParticipants",
                        ArrayList(selectedListParticipant)
                    )
                    putParcelable("scheduleCallData", dataScheduleCallParcelable)
                    putString("addedParticipants",getString(R.string.fragment_schedule_call_participant_added_participants))
                    putString("class_name","ScheduleCallParticipiantFragment")
                    putString("header_title", title)
                    putSerializable("event",event)
                }

                if(classFragment=="ScheduledCallInfoFragment"){
                    val nextFragment = ScheduledCallInfoFragment()
                    nextFragment.arguments = bundle
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.home_fragment_container_view, nextFragment)
                        .addToBackStack(null)
                        .commit()
                }else {
                    val nextFragment = ScheduleCallFragment()
                    nextFragment.arguments = bundle
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.chatOptionFcv, nextFragment)
                        .addToBackStack(null)
                        .commit()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Morate odabrati barem jednog učesnika",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.mtvSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                filterParticipant(query)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.ivBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }

        binding.mtvAddAnonymousParticipant.setOnClickListener() {
            val enterValueBottomSheetFragment = EnterEmailAndNameBottomSheetFragment()
            enterValueBottomSheetFragment.setOnValueListener(this, fieldTag)
            enterValueBottomSheetFragment.show(
                requireActivity().supportFragmentManager,
                enterValueBottomSheetFragment.tag
            )
        }
    }

    private fun filterParticipant(query: String) {
        val filteredList = dataList.filter { participant ->
            participant.sgco_name?.contains(query, ignoreCase = true) ?: false
        }

        if (!filteredList.isNullOrEmpty()) {
            binding.clWrapperData.visibility = View.GONE
            val filteredSelectedItems = selectedListParticipant.filter { selected ->
                filteredList.contains(selected)
            }

            selectedListParticipant = filteredSelectedItems
            updateRecyclerView(filteredList)
        } else {
            binding.clWrapperData.visibility = View.VISIBLE
        }
    }


    private fun updateRecyclerView(filteredList: List<Participant>) {
        scheduleCallAdapter.updateFilterData(filteredList)
    }

    override fun OnValueChange(email: String, name: String) {
        val newParticipant = Participant(
            sgco_id = 0,
            sgco_name = name,
            email = email
        )

        dataList = dataList + newParticipant
        updateRecyclerView(dataList)
    }

    override fun onParticipantSelected(selectedList: List<Participant>) {
        selectedListParticipant = selectedList
        Log.d("ScheduleCall", "Odabrani učesnik: $selectedListParticipant")
    }
}