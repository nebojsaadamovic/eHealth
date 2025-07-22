package com.example.zdravlje.ui.adaptersAndViewHolders.healthcare

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.response.healthcare.QAResponse
import com.example.zdravlje.ui.healthcare.prevention.questionsanswers.QuestionsDetailsFragment
import com.nites.ezdravljeapp.R
import java.text.SimpleDateFormat
import java.util.Locale

data class ListQAAdapter(
    private var allQAArray: Array<QAResponse>,
    private val fragmentManager: FragmentManager,
) : RecyclerView.Adapter<ListQAAdapter.RecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_all_qa, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = allQAArray[position]
        holder.textViewQA.text = item.topic
        holder.textViewDate.text = item.citengGenerated?.let { formatDateToDayMonthYear(it) }
        holder.ivArrowQA.setOnClickListener {
            openFragment(QuestionsDetailsFragment(allQAArray))
        }
    }

    override fun getItemCount(): Int {
        return allQAArray.size
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewQA : TextView = itemView.findViewById(R.id.mtvQA)
        val textViewDate : TextView = itemView.findViewById(R.id.mtvDate)
        val ivArrowQA : ImageView = itemView.findViewById(R.id.ivArrowQA)
    }

    fun updateData(newData: Array<QAResponse>) {
        allQAArray = newData
        notifyDataSetChanged()
    }

    private fun openFragment(nameFragment: Fragment) {
        val fragment = nameFragment
        fragmentManager.beginTransaction()
            .replace(R.id.home_fragment_container_view, fragment)
            .addToBackStack(null).commit()
    }

    fun formatDateToDayMonthYear(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        return outputFormat.format(date)
    }
}