package com.example.zdravlje.ui.onBoarding.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nites.ezdravljeapp.R

class OnBoardingAdapter(
    private val titles: ArrayList<String>,
    private val icons: ArrayList<Drawable>,
    private val descriptions: ArrayList<String>,
    private val dots: ArrayList<Drawable>
) : RecyclerView.Adapter<OnBoardingAdapter.RecyclerViewHolder>() {

    val notificationValue = "notification"
    private var listener: OnValueListener? = null

    interface OnValueListener {
        fun onValueListener(notification: String)
    }

    fun setOnValueListener(listener: OnValueListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_on_boarding_card, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.textViewTitle.text = titles[position]
        holder.textViewTitle.setTextColor(holder.itemView.context.getColor(R.color.onboarding_title_0270BB))
        holder.imageViewIconCard.setImageDrawable(icons[position])
        holder.textViewDescription.text = descriptions[position]
        holder.imageViewIconDot.setImageDrawable(dots[position])

        if (position == titles.size - 1) {
            listener?.onValueListener(notificationValue)
        } else {
            holder.textViewDescription.setTextColor(holder.itemView.context.getColor(R.color.onboarding_title_0270BB))
            holder.textViewTitle.setTextColor(holder.itemView.context.getColor(R.color.onboarding_title_0270BB))
            holder.textViewDescription.setTypeface(null, android.graphics.Typeface.NORMAL)
        }
    }

    override fun getItemCount(): Int {
        return titles.size
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTitle: TextView = itemView.findViewById(R.id.textView_title)
        val imageViewIconCard: ImageView = itemView.findViewById(R.id.imageView_icon_card)
        val textViewDescription: TextView = itemView.findViewById(R.id.textView_descriprion)
        val imageViewIconDot: ImageView = itemView.findViewById(R.id.imageView_icon_dot)
    }
}