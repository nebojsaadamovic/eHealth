package com.example.zdravlje.ui.adaptersAndViewHolders.healthcare

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.healthcare.ScreeningExaminationsData
import com.example.zdravlje.ui.healthcare.DownloadPdfFileFragment
import com.example.zdravlje.ui.healthcare.screeningexaminations.AmnesticQuestionnaireFragment
import com.nites.ezdravljeapp.R

class ScreeningExaminationsAdapter(
    private var screeningExaminationsArr: Array<ScreeningExaminationsData>,
    private var parentFragmentManager: FragmentManager
) : RecyclerView.Adapter<ScreeningExaminationsAdapter.RecyclerViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ScreeningExaminationsAdapter.RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_screening_examinations, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ScreeningExaminationsAdapter.RecyclerViewHolder,
        position: Int
    ) {
        val item = screeningExaminationsArr[position]
        holder.textViewDateItem.text = item.date
        holder.textViewTimeItem.text = item.time
        holder.textViewlocationItem.text = item.location
        holder.textViewStatusItem.text = item.status
        val seeQuestionnaire =
            holder.itemView.context.getString(R.string.adapter_screening_examination_see_questionnaire)
        holder.textViewAmnestyQuestionnaireItem.text =
            item.anamnesticQuestionnaire.takeIf { it.isNotEmpty() } ?: seeQuestionnaire
        val seeInvitation =
            holder.itemView.context.getString(R.string.adapter_screening_examination_see_invitation)
        holder.textViewInvitationItem.text =
            item.invitation.takeIf { it.isNotEmpty() } ?: seeInvitation
        val seeReport =
            holder.itemView.context.getString(R.string.adapter_screening_examination_see_report)
        holder.textViewReportItem.text = item.report.takeIf { it.isNotEmpty() } ?: seeReport
        val choose =
            holder.itemView.context.getString(R.string.adapter_screening_examination_choose)
        holder.textViewActionItem.text = item.action.takeIf { it.isNotEmpty() } ?: choose
        holder.textViewInvitationItem.setOnClickListener {
            val titleFromResources =
                holder.itemView.context.getString(R.string.adapter_screening_examination_screening_invitation)
            val downloadPdfFileFragment =
                DownloadPdfFileFragment.newInstance("", titleFromResources)
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.home_fragment_container_view, downloadPdfFileFragment)
                addToBackStack(null)
                commit()
            }
        }

        holder.textViewAmnestyQuestionnaireItem.setOnClickListener() {
            val amnesticQuestionnaireFragment = AmnesticQuestionnaireFragment()
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.home_fragment_container_view, amnesticQuestionnaireFragment)
                addToBackStack(null)
                commit()
            }
        }

        holder.textViewReportItem.setOnClickListener {
            val titleFromResources =
                holder.itemView.context.getString(R.string.adapter_screening_examination_examination_report)
            val downloadPdfFileFragment =
                DownloadPdfFileFragment.newInstance("", titleFromResources)
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.home_fragment_container_view, downloadPdfFileFragment)
                addToBackStack(null)
                commit()
            }
        }
    }

    override fun getItemCount(): Int {
        return screeningExaminationsArr.size
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewDateItem: TextView = itemView.findViewById(R.id.mtvDateItemValue)
        val textViewTimeItem: TextView = itemView.findViewById(R.id.mtvTimeItemValue)
        val textViewlocationItem: TextView = itemView.findViewById(R.id.mtvLocationItemValue)
        val textViewStatusItem: TextView = itemView.findViewById(R.id.mtvStatusItemValue)
        val textViewAmnestyQuestionnaireItem: TextView =
            itemView.findViewById(R.id.mtvAmnestyQuestionnaireItemValue)
        val textViewInvitationItem: TextView = itemView.findViewById(R.id.mtvInvitationItemValue)
        val textViewReportItem: TextView = itemView.findViewById(R.id.mtvReportItemValue)
        val textViewActionItem: TextView = itemView.findViewById(R.id.mtvActionItemValue)
    }
}