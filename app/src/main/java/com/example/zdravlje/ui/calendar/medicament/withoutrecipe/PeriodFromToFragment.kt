package com.example.zdravlje.ui.calendar.medicament.withoutrecipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.zdravlje.data.model.response.dashboard.event_payload.MedicineEventPayload
import com.example.zdravlje.data.model.response.dashboard.future.Event
import com.example.zdravlje.data.model.response.healthcare.PatmedNotes
import com.example.zdravlje.data.model.response.healthcare.PrescribedListMedicamentResponse
import com.example.zdravlje.ui.calendar.BottomSheetInsertValue
import com.example.zdravlje.utilities.DateUtils
import com.google.gson.Gson
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentPeriodFromToBinding
import java.text.SimpleDateFormat
import java.util.Locale

class PeriodFromToFragment : Fragment(), BottomSheetInsertValue.OnValueListener {

    lateinit var binding: FragmentPeriodFromToBinding
    private var medicineItem: PrescribedListMedicamentResponse?=null
    private var medicineEventPayload: MedicineEventPayload? = null
    private var event: Event? = null
    private var title: String? = null
    private var headerTitle: String? = null
    private var isTimeToSelected = false
    private var isTimeFromSelected = false
    private var dateFrom: String = ""
    private var dateTo: String = ""
    private var dosage: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPeriodFromToBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            medicineItem = it.getParcelable("medicine_item")
            title = it.getString("title")
            headerTitle = it.getString("header_title")
            medicineEventPayload =
                it.getSerializable("medicine_event_payload") as? MedicineEventPayload
            event = it.getSerializable("event") as? Event
        }
        setupUI()
    }

    private fun setupUI() {
        if (title == "medicamenteFromERecipe") {
            binding.mcvDosage.visibility = View.GONE
            binding.vLine.visibility = View.GONE
            binding.mtvDosageERecipe.visibility = View.VISIBLE
            binding.mtvDosageERecipeValue.visibility = View.VISIBLE
            binding.mtvDosageERecipeValue.setTextColor(ContextCompat.getColor(requireContext(), R.color.base_blue_color))
            val patmedNotes = Gson().fromJson(medicineItem?.patmedNotes, PatmedNotes::class.java)
            if (patmedNotes!=null){
                val dosage = patmedNotes?.dosage
                binding.mtvDosageERecipeValue.text = dosage
            }else{
                binding.mtvDosageERecipeValue.text = getString(R.string.select)
            }

            binding.mtvDosageERecipeValue.setOnClickListener(){
                val dosageTitle = getString(R.string.fragment_period_from_to_title_bottom_sheet)
                val drugDoseSubtitle = getString(R.string.fragment_period_from_to_subtitle_bottom_sheet)
                val bottomSheetInsertValue = BottomSheetInsertValue().apply {
                    arguments = Bundle().apply {
                        putString("title", dosageTitle)
                        putString("subtitle", drugDoseSubtitle)
                    }
                }
                bottomSheetInsertValue.setOnValueListener(this)
                bottomSheetInsertValue.show(parentFragmentManager, bottomSheetInsertValue.tag)
            }

        } else if (title == "medicamenteWithOutRecipe") {
            binding.vLine.visibility = View.VISIBLE
            binding.mcvDosage.visibility = View.VISIBLE
            binding.mtvDosageERecipe.visibility = View.GONE
            binding.mtvDosageERecipeValue.visibility = View.GONE

            binding.tvDosageChoose.setOnClickListener {
                val dosageTitle = getString(R.string.fragment_period_from_to_title_bottom_sheet)
                val drugDoseSubtitle = getString(R.string.fragment_period_from_to_subtitle_bottom_sheet)
                val bottomSheetInsertValue = BottomSheetInsertValue().apply {
                    arguments = Bundle().apply {
                        putString("title", dosageTitle)
                        putString("subtitle", drugDoseSubtitle)
                    }
                }
                bottomSheetInsertValue.setOnValueListener(this)
                bottomSheetInsertValue.show(parentFragmentManager, bottomSheetInsertValue.tag)
            }

        } else if (title == "drugConsumptionActivity") {
            headerTitle?.let { binding.tvHeaderTitle.text = it}
            binding.mcvDosage.visibility = View.GONE
            binding.vLine.visibility = View.GONE
            binding.mtvDosageERecipe.visibility = View.VISIBLE
            binding.mtvDosageERecipeValue.visibility = View.VISIBLE
            binding.mtvDosageERecipeValue.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.base_blue_color
                )
            )

            binding.mtvDosageERecipeValue.setOnClickListener(){
                val dosageTitle = getString(R.string.fragment_period_from_to_title_bottom_sheet)
                val drugDoseSubtitle = getString(R.string.fragment_period_from_to_subtitle_bottom_sheet)
                val bottomSheetInsertValue = BottomSheetInsertValue().apply {
                    arguments = Bundle().apply {
                        putString("title", dosageTitle)
                        putString("subtitle", drugDoseSubtitle)
                    }
                }
                bottomSheetInsertValue.setOnValueListener(this)
                bottomSheetInsertValue.show(parentFragmentManager, bottomSheetInsertValue.tag)
            }

            dosage = medicineEventPayload?.mandatory?.doziranje.toString()
            binding.tvMedicamentValue.text = event?.eventTitle
            binding.mtvDosageERecipeValue.text = dosage
            binding.mtvDateFromValue.text = event?.eventStartTime?.let { DateUtils.getFormattedDateLineUp(it) }
            dateFrom = binding.mtvDateFromValue.text.toString()
            isTimeFromSelected = true
            binding.mtvDateFromValue.isEnabled = false
            binding.mtvDateFromValue.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.text_color_8A8A8D
                )
            )
            binding.ivBack.setOnClickListener() {
                requireActivity().onBackPressed()
            }

            println("kkkkkkkkkkkkkkkwwwwwwwwwwwww")
            binding.mbNext.setOnClickListener() {
                val timeTakingMedicamentFragment =
                    TimeTakingMedicamentFragment(null, dateFrom, dateTo, dosage, event, medicineEventPayload, headerTitle = headerTitle)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.drug_fragment_container, timeTakingMedicamentFragment)
                    .addToBackStack(null).commit()
            }
        }

        if (title != "drugConsumptionActivity") {
            binding.tvMedicamentValue.text = medicineItem?.medicamentProtectedName

            binding.mbNext.setOnClickListener() {
                val timeTakingMedicamentFragment =
                    TimeTakingMedicamentFragment(medicineItem, dateFrom, dateTo, dosage, null)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.calendar_options_container_view, timeTakingMedicamentFragment)
                    .addToBackStack(null).commit()
            }

            binding.ivBack.setOnClickListener() {
                requireActivity().onBackPressed()
            }
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
                    dateFrom = binding.mtvDateFromValue.text.toString()
                    dateTo = binding.mtvDateToValue.text.toString()

                    val dateFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())

                    try {
                        val dateFrom = dateFormat.parse(dateFrom)
                        val dateTo = dateFormat.parse(dateTo)

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

        binding.mtvDateToValue.setOnClickListener {
            DateUtils.showDatePickerDialog(requireContext(), binding.mtvDateToValue) {
                binding.mtvDateToValue.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.text_color_8A8A8D)
                )

                isTimeToSelected = true

                if (isTimeFromSelected && isTimeToSelected) {
                    dateFrom = binding.mtvDateFromValue.text.toString()
                    dateTo = binding.mtvDateToValue.text.toString()

                    val dateFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())

                    try {
                        val dateFrom = dateFormat.parse(dateFrom)
                        val dateTo = dateFormat.parse(dateTo)

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
    }

    override fun onValueChange(value: String) {
        binding.tvDosageChoose.text = value
        dosage = value
        binding.mtvDosageERecipeValue.text = value
    }
}