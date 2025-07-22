package com.example.zdravlje.ui.dialogs

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.app.democall.composesample.data.call.IncomingCallResponse
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.card.MaterialCardView
import com.nites.ezdravljeapp.R

class IncomingCallDialog(
    context: Context,
    private var parent: IncomingCallActions,
    private var incomingCallResponse: IncomingCallResponse

) :
    Dialog(context) {
    private var mpCallerRingtone: MediaPlayer? = null


    interface IncomingCallActions {
        fun onAcceptCall()
        fun onDeclineCall()
    }

    init {

        setCancelable(false) // Prevent closing via the Back button
        setContentView(R.layout.fragment_incoming_call_dialog)
        this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val layoutParams = window!!.attributes
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        window!!.attributes = layoutParams

        onInit()
    }

    @SuppressLint("SetTextI18n")
    fun onInit() {
        initRingtone()
        mpCallerRingtone?.start()


        findViewById<MaterialCardView>(R.id.btnAccept)?.setOnClickListener {
            parent.onAcceptCall()
            mpCallerRingtone?.stop()
            dismiss()
        }

        findViewById<MaterialCardView>(R.id.btnDecline)?.setOnClickListener {
            parent.onDeclineCall()
            mpCallerRingtone?.stop()
            dismiss()
        }

        findViewById<TextView>(R.id.userName).text=incomingCallResponse.user.firstName+" "+incomingCallResponse.user.lastName

        if (incomingCallResponse.user.avatarDB!=null){
            loadImage(incomingCallResponse.user.avatarDB!!,this.context,findViewById<ImageView>(R.id.userAvatar))
        }

        createAnimation( findViewById<ImageButton>(R.id.ibAcceptCallPulsing),1.1f,1000)
        createAnimation( findViewById<ImageButton>(R.id.ibAcceptCallPulsingSecond),1.1f,1000)
    }

    fun createAnimation(button: ImageButton, value:Float, duration:Long){
        val anim = ValueAnimator.ofFloat(1f, value)
        anim.duration = duration
        anim.addUpdateListener { animation ->
            button.setScaleX(animation.animatedValue as Float)
            button.setScaleY(animation.animatedValue as Float)

        }
        anim.repeatCount = ValueAnimator.INFINITE;
        anim.repeatMode = ValueAnimator.REVERSE
        anim.start()
    }

    private fun initRingtone() {
        mpCallerRingtone = MediaPlayer.create(this.context, R.raw.ringback)
        mpCallerRingtone?.setOnCompletionListener { mp ->
            var mp = mp
            mp!!.reset()
            mp.release()
            mp = null
        }
    }

    fun loadImage(image: String, context: Context, imageView: ImageView) {
        val glideRequest = Glide.with(context)
            .load(if (!image.equals("")) image else R.drawable.ic_contact_avatar)
            .transition(DrawableTransitionOptions.withCrossFade())
            .centerCrop()
            .transform(CircleCrop())
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .transition(DrawableTransitionOptions.withCrossFade())

        glideRequest.into(imageView)
    }
}
