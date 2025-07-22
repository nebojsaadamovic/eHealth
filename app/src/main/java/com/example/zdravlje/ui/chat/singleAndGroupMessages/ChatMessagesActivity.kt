package com.example.zdravlje.ui.chat.singleAndGroupMessages

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.app.democall.composesample.data.chat.list.ChatListResponseItem
import com.example.zdravlje.ui.base.BaseActivity
import com.google.gson.Gson
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.ActivityMessagesBinding

class ChatMessagesActivity() : BaseActivity() {

    lateinit var binding: ActivityMessagesBinding
    private var chatId: String? = null

    private var chatType: Int = -1 // Dodato polje za chatType
    lateinit var chatListResponseItem: ChatListResponseItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val chatListResponseItemJson = intent.getStringExtra("chat_list_response_item_json")
        if (chatListResponseItemJson != null) {
            // Deserializacija JSON stringa u ChatListResponseItem objekat
            val gson = Gson()
            chatListResponseItem = gson.fromJson(chatListResponseItemJson, ChatListResponseItem::class.java)
        }

        chatType = intent.getIntExtra("chat_type", -1)
        chatId = intent.getStringExtra("your_chat_id")
        binding = ActivityMessagesBinding.inflate(layoutInflater)

        setContentView(binding.root)
        this.window.navigationBarColor = ContextCompat.getColor(this, R.color.white_bg)
        this.window.statusBarColor = ContextCompat.getColor(this, R.color.white_bg)
    }

    fun startNewFragmentFromMessages(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            .replace(R.id.chatOptionFcv, fragment)
            .addToBackStack(null)
            .commit()
    }


    fun  getChat(): ChatListResponseItem {
        return chatListResponseItem
    }

    fun  setChat(chatResponseItem:ChatListResponseItem){
         chatListResponseItem=chatResponseItem
    }

    fun getChatType(): Int {
        return chatType
    }
}