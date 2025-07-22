package com.example.zdravlje.ui.adaptersAndViewHolders.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.response.chat.UserContact
import com.example.zdravlje.utilities.loadImage
import com.nites.ezdravljeapp.R

class ProfileUsersAdapter(
    private val users: List<UserContact>,
    private val listener: OnUserItemClickListener
) :
    RecyclerView.Adapter<ProfileUsersAdapter.ProfileUsersViewHolder>() {

    interface OnUserItemClickListener {
        fun onItemClickFirst(item: UserContact)
        fun onItemClickSecond(item: UserContact)
    }

    private var ivPermitResourceId: Int = R.drawable.ic_e_id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileUsersViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profile_user, parent, false)
        return ProfileUsersViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ProfileUsersAdapter.ProfileUsersViewHolder,
        position: Int
    ) {
        val user = users[position]
        holder.bind(user)
    }

    fun updatePermitImageResource(drawableResId: Int) {
        ivPermitResourceId = drawableResId
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = users.size

    inner class ProfileUsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userFullName: TextView = itemView.findViewById(R.id.userFullName)
        private val userImage: ImageView = itemView.findViewById(R.id.ivUserImage)
        private val ivPermit: ImageView =
            itemView.findViewById(R.id.ivPermit)

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
