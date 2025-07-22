package com.example.zdravlje.ui.calendar.personalActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.zdravlje.utilities.DateUtils
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentCalendarPersonalActivityDateBinding
import java.text.SimpleDateFormat
import java.util.Locale

class PersonalActivityDateFragment(
    private var activityName: String,
    private var event: com.example.zdravlje.data.model.response.dashboard.future.Event? = null,
    private var className: String? = null,
    private var dateFrom: String? = null,
) : Fragment() {

    lateinit var binding: FragmentCalendarPersonalActivityDateBinding
    var isTimeToSelected = false
    var isTimeFromSelected = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarPersonalActivityDateBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    fun setupUI() {
        if (className=="PersonalDataActivity_EditArray"){
            binding.tvCalendar.text = "Izmeni ličnu aktivnost"
        }

        if (event != null) {
            binding.mtvDateFromValue.isEnabled = false
            binding.mtvDateFromValue.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color_8A8A8D))
            isTimeFromSelected = true
            binding.mtvDateFromValue.text = event!!.eventStartTime?.let { DateUtils.convertYYYYMMDDHHssToHorLineDDMMYYYY(it) }

            binding.mbNext.setOnClickListener {
                val dateFrom = binding.mtvDateFromValue.text
                val name = binding.tvActivityNameValue.text
                val dateTo = binding.mtvDateToValue.text
                val personalActivityDateDetailsFragment = PersonalActivityDateDetailsFragment(
                    name = name.toString(), dateFrom = dateFrom.toString(), dateTo= dateTo.toString(), className = className, event = event
                )

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.activity_personal_fragment_container, personalActivityDateDetailsFragment)
                    .addToBackStack(null).commit()
            }

        }else{
            if (!dateFrom.isNullOrEmpty()) {
                binding.mtvDateFromValue.text = dateFrom
                isTimeFromSelected = true
                binding.mtvDateFromValue.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.text_color_8A8A8D)
                )
            }

            binding.mtvDateFromValue.setOnClickListener {
                DateUtils.showDatePickerDialog(requireContext(), binding.mtvDateFromValue) {
                    binding.mtvDateFromValue.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.text_color_8A8A8D
                        )
                    )

                    isTimeFromSelected = true

                    if (isTimeFromSelected && isTimeToSelected) {
                        val dateFromStr = binding.mtvDateFromValue.text.toString()
                        val dateToStr = binding.mtvDateToValue.text.toString()

                        val dateFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())

                        try {
                            val dateFrom = dateFormat.parse(dateFromStr)
                            val dateTo = dateFormat.parse(dateToStr)

                            if (dateFrom != null && dateTo != null && dateFrom.after(dateTo)) {
                                Toast.makeText(
                                    requireActivity(),
                                    getString(R.string.fragment_period_from_to_date_error),
                                    Toast.LENGTH_SHORT
                                ).show()

                                binding.mtvDateFromValue.setTextColor(
                                    ContextCompat.getColor(requireContext(), R.color.base_blue_color)
                                )
                                binding.mtvDateToValue.setTextColor(
                                    ContextCompat.getColor(requireContext(), R.color.base_blue_color)
                                )
                                binding.mbNext.alpha = 0.2f
                                binding.mbNext.isEnabled = false
                                return@showDatePickerDialog
                            }

                            binding.mtvDateToValue.setTextColor(
                                ContextCompat.getColor(requireContext(), R.color.text_color_8A8A8D)
                            )
                            binding.mtvDateFromValue.setTextColor(
                                ContextCompat.getColor(requireContext(), R.color.text_color_8A8A8D)
                            )
                            binding.mbNext.alpha = 1.0f
                            binding.mbNext.isEnabled = true

                        } catch (e: Exception) {
                            Toast.makeText(
                                requireActivity(),
                                getString(R.string.fragment_period_from_to_date_error),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

            binding.mbNext.setOnClickListener {
                val dateFrom = binding.mtvDateFromValue.text
                val name = binding.tvActivityNameValue.text
                val dateTo = binding.mtvDateToValue.text
                val personalActivityDateDetailsFragment = PersonalActivityDateDetailsFragment(
                    name.toString(), dateFrom.toString(), dateTo.toString(), className = className
                )

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.calendar_options_container_view, personalActivityDateDetailsFragment)
                    .addToBackStack(null).commit()
            }
        }

        binding.tvActivityNameValue.text = activityName

        binding.mtvDateToValue.setOnClickListener {
            DateUtils.showDatePickerDialog(requireContext(), binding.mtvDateToValue) {
                binding.mtvDateToValue.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.text_color_8A8A8D)
                )

                isTimeToSelected = true

                if (isTimeFromSelected && isTimeToSelected) {
                    val dateFromStr = binding.mtvDateFromValue.text.toString()
                    val dateToStr = binding.mtvDateToValue.text.toString()

                    val dateFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())

                    try {
                        val dateFrom = dateFormat.parse(dateFromStr)
                        val dateTo = dateFormat.parse(dateToStr)

                        if (dateFrom != null && dateTo != null && dateFrom.after(dateTo)) {
                            Toast.makeText(
                                requireActivity(),
                                getString(R.string.fragment_period_from_to_date_error),
                                Toast.LENGTH_SHORT
                            ).show()

                            binding.mtvDateFromValue.setTextColor(
                                ContextCompat.getColor(requireContext(), R.color.base_blue_color)
                            )
                            binding.mtvDateToValue.setTextColor(
                                ContextCompat.getColor(requireContext(), R.color.base_blue_color)
                            )
                            binding.mbNext.alpha = 0.2f
                            binding.mbNext.isEnabled = false
                            return@showDatePickerDialog
                        }

                        binding.mtvDateToValue.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.text_color_8A8A8D)
                        )
                        binding.mtvDateFromValue.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.text_color_8A8A8D)
                        )
                        binding.mbNext.alpha = 1.0f
                        binding.mbNext.isEnabled = true

                    } catch (e: Exception) {
                        Toast.makeText(
                            requireActivity(),
                            getString(R.string.fragment_period_from_to_date_error),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        binding.ivBack.setOnClickListener() {
            requireActivity().finish()
        }
    }
}