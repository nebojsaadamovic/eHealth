package com.example.zdravlje.ui.adaptersAndViewHolders.healthcare

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.response.healthcare.SocialGroupCategoryResponse
import com.example.zdravlje.ui.healthcare.prevention.commonsthemes.PreventionInfoDetailsFragment
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.nites.ezdravljeapp.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SocialGroupsInfoAdapter(
    private val data: List<SocialGroupCategoryResponse>,
    private val fragmentManager: FragmentManager
) : RecyclerView.Adapter<SocialGroupsInfoAdapter.RecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_social_group_info_arr, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val dataValue = data[position]
        holder.textViewContent.text = dataValue.sgcatNameSh
        holder.textViewDate.text =
            convertTimestampToDate(dataValue.socialGroups?.firstOrNull()?.sgTimeCreated)

        holder.textViewContentMCV.setOnClickListener {
            val fragment = PreventionInfoDetailsFragment(
                data,
                "Commons Themes",
                dataValue.socialGroups?.firstOrNull()?.socialGroupMessages?.firstOrNull()?.sgmMessage
            )

            fragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container_view, fragment)
                .addToBackStack(null).commit()
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewContent: MaterialTextView = itemView.findViewById(R.id.mtvContentInfo)
        val textViewContentMCV: MaterialCardView = itemView.findViewById(R.id.mcvContentInfo)
        val textViewDate: MaterialTextView = itemView.findViewById(R.id.mtvDateValue)
    }

    fun convertTimestampToDate(timestamp: Long?): String {
        if (timestamp == null) return "N/A"
        val date = Date(timestamp * 1000)
        val dateFormat =
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }
}
