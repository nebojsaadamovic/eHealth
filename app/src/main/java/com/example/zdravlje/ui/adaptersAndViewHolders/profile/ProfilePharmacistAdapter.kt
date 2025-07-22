package com.example.zdravlje.ui.adaptersAndViewHolders.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.response.chat.UserContact
import com.example.zdravlje.ui.adaptersAndViewHolders.profile.ProfileSubUsersAdapter.OnUserItemClickListener
import com.example.zdravlje.utilities.loadImage
import com.nites.ezdravljeapp.R

class ProfilePharmacistAdapter(
    private val users: List<UserContact>,
    private val listener: OnUserItemClickListener
) :
    RecyclerView.Adapter<ProfilePharmacistAdapter.ProfilePharmacistViewHolder>() {

    private var ivPermitResourceId: Int = R.drawable.ic_e_id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfilePharmacistViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_profile_sub_user, parent, false)
        return ProfilePharmacistViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfilePharmacistViewHolder, position: Int) {
        val users = users[position]
        holder.bind(users)
    }

    fun updatePermitImageResource(drawableResId: Int) {
        ivPermitResourceId = drawableResId
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return users.size
    }

    inner class ProfilePharmacistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userFullName: TextView = itemView.findViewById(R.id.userFullName)
        private val userImage: ImageView = itemView.findViewById(R.id.ivUserImage)
        private val ivPermit: ImageView = itemView.findViewById(R.id.ivPermit)

        fun bind(user: UserContact) {
            userFullName.text = user.sgco_name
            if (!user.avt_encoded_value.isNullOrEmpty()) {
                userImage.loadImage(
                    fileStream = user.avt_encoded_value,
                    defaultImageResId = R.drawable.ic_contact_avatar
                )
            } else {
                userImage.setImageResource(R.drawable.ic_contact_avatar)
            }
            ivPermit.setImageResource(ivPermitResourceId)

            ivPermit.setOnClickListener {
                when (ivPermitResourceId) {
                    R.drawable.ic_arrow -> {
                        listener.onItemClickFirst(user)
                    }

                    R.drawable.ic_permits_folder -> {
                        listener.onItemClickSecond(user)
                    }
                }
            }
        }
    }
}
