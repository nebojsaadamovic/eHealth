package com.example.zdravlje.ui.adaptersAndViewHolders.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.response.profile.AccountSynchronizeResponse
import com.example.zdravlje.utilities.DateUtils
import com.google.android.material.card.MaterialCardView
import com.nites.ezdravljeapp.R
import java.util.Date

class SyncStatusAdapter(
    private var items: List<AccountSynchronizeResponse>
) : RecyclerView.Adapter<SyncStatusAdapter.SyncStatusViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SyncStatusViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sync, parent, false)
        return SyncStatusViewHolder(view)
    }

    override fun onBindViewHolder(holder: SyncStatusViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<AccountSynchronizeResponse>) {
        items = newItems
        notifyDataSetChanged()
    }

    class SyncStatusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mtvSystemValue = itemView.findViewById<TextView>(R.id.mtvSystemValue)
        private val mtvSinkValue = itemView.findViewById<TextView>(R.id.mtvSinkValue)
        private val mtvDateSinkValue = itemView.findViewById<TextView>(R.id.mtvDateSinkValue)
        private val mtvTimeSinkValue = itemView.findViewById<TextView>(R.id.mtvTimeSinkValue)
        private val statusImageView:MaterialCardView = itemView.findViewById<MaterialCardView>(R.id.statusColorImageView)

        fun bind(item: AccountSynchronizeResponse) {

            val completedDate = DateUtils.stringToDate(item.useSyncCompleted ?: "", "yyyy-MM-dd HH:mm") ?: return

            mtvDateSinkValue.text = DateUtils.dateToString(completedDate, "dd.MM.yyyy.")
            mtvTimeSinkValue.text = DateUtils.dateToString(completedDate, "HH:mm")

            when (item.useExternalSystem) {
                "Erecept" -> mtvSystemValue.text = itemView.context.getString(R.string.adapter_sync_status_erecipe_data)
                "Sorsix" -> mtvSystemValue.text = itemView.context.getString(R.string.adapter_sync_status_ecard_data)
                "Vuz" -> mtvSystemValue.text = itemView.context.getString(R.string.adapter_sync_status_ereferral_data)
                "Mobile Sync" -> mtvSystemValue.text = itemView.context.getString(R.string.adapter_sync_status_life_style)
                "RFZO Subinsured" -> mtvSystemValue.text = itemView.context.getString(R.string.adapter_sync_status_underinsured_persons)
                else -> mtvSystemValue.text = item.useExternalSystem
            }

            val now = Date()
            val hours = (now.time - completedDate.time) / (1000 * 3600).toDouble()

            when {
                hours < 2 -> {
                    statusImageView.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.medicineColor))
                    mtvSinkValue.text = "U toku poslednja 2 sata"
                    mtvSinkValue.setTextColor(ContextCompat.getColor(itemView.context, R.color.medicineColor))

                }
                hours < 24 -> {
                    statusImageView.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.yellow_FFA901))
                    mtvSinkValue.text = "U toku poslednja 24 sata"
                    mtvSinkValue.setTextColor(ContextCompat.getColor(itemView.context, R.color.yellow_FFA901))

                }
                else -> {
                    statusImageView.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.red_warning_E75656))
                    mtvSinkValue.text = "Više od 24 sata"
                    mtvSinkValue.setTextColor(ContextCompat.getColor(itemView.context, R.color.red_warning_E75656))
                }
            }

        }
    }

}
