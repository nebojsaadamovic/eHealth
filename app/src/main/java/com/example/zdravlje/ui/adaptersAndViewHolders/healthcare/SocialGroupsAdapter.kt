package com.example.zdravlje.ui.adaptersAndViewHolders.healthcare

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.response.healthcare.SocialGroup
import com.example.zdravlje.ui.healthcare.prevention.commonsthemes.PreventionInfoFragment
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.nites.ezdravljeapp.R

class SocialGroupsAdapter(
    private var sgNamesList: List<SocialGroup?>,
    private val fragmentManager: FragmentManager
) : RecyclerView.Adapter<SocialGroupsAdapter.RecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_social_group_arr, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val data = sgNamesList[position]
        val sgmId = data?.socialGroupMessages?.firstOrNull()?.sgmId

        holder.textViewContent.text = data?.sgName.toString()
        holder.textViewContentMCV.setOnClickListener {
            val fragment = PreventionInfoFragment(sgmId)
            fragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container_view, fragment)
                .addToBackStack(null).commit()
        }
    }

    override fun getItemCount(): Int {
        return sgNamesList.size
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewContent: MaterialTextView = itemView.findViewById(R.id.mtvContentSocialGroup)
        val textViewContentMCV: MaterialCardView = itemView.findViewById(R.id.mcvContentSocialGroup)
    }

    fun updateData(newData: List<SocialGroup>) {
        sgNamesList = newData
        notifyDataSetChanged()
    }
}
