package com.example.zdravlje.ui.calendar.personalActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.zdravlje.utilities.CommonUtils
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentPersonalActivityDateDetailsBinding

class PersonalActivityDateDetailsFragment(
    private var name: String,
    private var dateFrom: String,
    private var dateTo: String,
    private var className: String? = null,
    private var event: com.example.zdravlje.data.model.response.dashboard.future.Event? = null
) : Fragment() {

    lateinit var binding: FragmentPersonalActivityDateDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPersonalActivityDateDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    fun setupUI() {
        binding.tvActivityNameValue.text = name
        binding.tvITimeFromValue.text = dateFrom
        binding.tvTimeToValue.text = dateTo

        if (className == "PersonalDataActivity") {
            binding.clActivityTime.setOnClickListener() {
                CommonUtils.openFragmentActivityPersonal(
                    requireActivity(),
                    PersonalActivityTimeActivityFragment(
                        name = name, dateFrom = dateFrom, dateTo = dateFrom, className = className, eventFuture = event)
                )
            }

            binding.clInterval.setOnClickListener(View.OnClickListener {
                CommonUtils.openFragmentActivityPersonal(
                    requireActivity(),
                    PersonalActivityDateIntervalFragment(name, dateFrom, dateTo, className, event)
                )
            })

        } else if (className == "PersonalDataActivity_EditArray") {
            binding.tvCalendar.text = "Izmeni ličnu aktivnost"
            binding.clActivityTime.setOnClickListener() {
                CommonUtils.openFragmentActivityPersonal(
                    requireActivity(),
                    PersonalActivityTimeActivityFragment(name, dateFrom, dateTo, className, event)
                )
            }

            binding.clInterval.setOnClickListener(View.OnClickListener {
                CommonUtils.openFragmentActivityPersonal(
                    requireActivity(),
                    PersonalActivityDateIntervalFragment(name, dateFrom, dateTo, className, event)
                )
            })

        } else {

            binding.clActivityTime.setOnClickListener() {
                CommonUtils.openFragmentCalendar(
                    requireActivity(),
                    PersonalActivityTimeActivityFragment(name, dateFrom, dateTo)
                )
            }

            binding.clInterval.setOnClickListener() {
                CommonUtils.openFragmentCalendar(
                    requireActivity(),
                    PersonalActivityDateIntervalFragment(name, dateFrom, dateTo)
                )
            }
        }

        binding.ivBack.setOnClickListener {
            if (className == "PersonalDataActivity_EditArray") {
                val personalActivityDateFragment = PersonalActivityDateFragment(
                    activityName =name, event = event, className = className
                )
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.activity_personal_fragment_container,
                        personalActivityDateFragment
                    )
                    .addToBackStack(null).commit()

            } else {
                CommonUtils.openFragmentCalendar(
                    requireActivity(),
                    PersonalActivityDateFragment(
                        activityName =name, dateFrom = dateFrom
                    )
                )
            }
        }
    }

}
