package com.example.zdravlje.ui.home.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.zdravlje.utilities.DateUtils
import com.nites.ezdravljeapp.R

class HealthFragment : Fragment() {

    private lateinit var textViewSteps: TextView
    private lateinit var textViewCalories: TextView
    private lateinit var textViewDistance: TextView
    private lateinit var textViewDescDate: TextView

    private lateinit var stepsProgressBar: ProgressBar
    private lateinit var caloriesProgressBar: ProgressBar
    private lateinit var distanceProgressBar: ProgressBar

    private val fitnessViewModel: FitnessViewModel by viewModels()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_health, container, false)
        return rootView
    }

    override fun onViewCreated(rootView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(rootView, savedInstanceState)

        textViewSteps = rootView.findViewById(R.id.steps)
        textViewCalories = rootView.findViewById(R.id.burned_calories)
        textViewDistance = rootView.findViewById(R.id.distance)
        stepsProgressBar = rootView.findViewById(R.id.stepsProgressBar)
        caloriesProgressBar = rootView.findViewById(R.id.caloriesProgressBar)
        distanceProgressBar = rootView.findViewById(R.id.distanceProgressBar)
        textViewDescDate = rootView.findViewById(R.id.tvDescDate)

        stepsProgressBar.max = fitnessViewModel.loadObjectiveSteps(rootView.context)

        textViewDescDate.text = DateUtils.getDateWithWeekName(DateUtils.getCurrentDateTime())

        fitnessViewModel.getDailyFitnessData(rootView.context)
            .observe(viewLifecycleOwner, Observer { DailyFitness ->
                textViewSteps.text = DailyFitness.stepCount.toString()
                textViewCalories.text = DailyFitness.caloriesBurned.toString()
                textViewDistance.text = String.format("%.2f", DailyFitness.distance)
                stepsProgressBar.progress = DailyFitness.stepCount
                caloriesProgressBar.progress = DailyFitness.caloriesBurned
                distanceProgressBar.progress = DailyFitness.distance.toInt()

            })
    }


}