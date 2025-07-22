package com.example.zdravlje.ui.adaptersAndViewHolders.healthcare

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.response.healthcare.RecommendationResponse
import com.example.zdravlje.ui.healthcare.prevention.commonsthemes.PreventionInfoDetailsFragment
import com.google.android.material.textview.MaterialTextView
import com.nites.ezdravljeapp.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecommendationsAdapter(
    private var data: List<RecommendationResponse>,
    private val fragmentManager: FragmentManager
) : RecyclerView.Adapter<RecommendationsAdapter.RecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recommendation, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = data[position]
        holder.tvDesc.text = item.recTitle
        holder.tvDate.text = convertTimestampToDate(item.recDate)
        holder.ivDesc.setOnClickListener() {
            openFragment(PreventionInfoDetailsFragment(null, null, item.recContent))
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDesc: MaterialTextView = itemView.findViewById(R.id.mtvDesc)
        val tvDate: MaterialTextView = itemView.findViewById(R.id.mtvDate)
        val ivDesc: ImageView = itemView.findViewById(R.id.ivDesc)
    }

    fun convertTimestampToDate(timestamp: Long?): String {
        if (timestamp == null) return "N/A"
        val date = Date(timestamp * 1000)
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }

    private fun openFragment(nameFragment: Fragment) {
        fragmentManager.beginTransaction()
            .replace(R.id.home_fragment_container_view, nameFragment)
            .addToBackStack(null).commit()
    }
}