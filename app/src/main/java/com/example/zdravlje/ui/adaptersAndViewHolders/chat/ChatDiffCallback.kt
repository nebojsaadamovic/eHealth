package com.example.zdravlje.ui.adaptersAndViewHolders.chat

import androidx.recyclerview.widget.DiffUtil
import com.app.democall.composesample.data.chat.list.ChatListResponseItem

class ChatDiffCallback(
    private val oldList: List<ChatListResponseItem>,
    private val newList: List<ChatListResponseItem>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Uporedite jedinstveni identifikator za svaki chat
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Uporedite sadržaj chatova
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}