package com.example.zdravlje.utilities

import android.graphics.BitmapFactory
import android.text.Selection
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Base64
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.fitness.data.DataPoint
import com.nites.ezdravljeapp.R
import java.text.DateFormat
import java.util.concurrent.TimeUnit


//Create Links for part of text
fun TextView.makeLinks(
    textSize: Int,
    fontFamily: Int?,
    isUnderline: Boolean,
    color: Int = resources.getColor(R.color.white),
    vararg links: Pair<String, View.OnClickListener>
) {
    val spannableString = SpannableString(this.text)
    var startIndexOfLink = -1
    for (link in links) {
        val clickableSpan = object : ClickableSpan() {
            override fun updateDrawState(textPaint: TextPaint) {
                // use this to change the link color
                val spSize = textSize
                val scaledSizeInPixels = spSize * resources.displayMetrics.scaledDensity
                textPaint.color = color
                textPaint.linkColor = resources.getColor(R.color.transparent)
                textPaint.bgColor = resources.getColor(R.color.transparent)

                textPaint.typeface = fontFamily?.let { ResourcesCompat.getFont(context, it) }
                textPaint.textSize = scaledSizeInPixels
                // toggle below value to enable/disable
                // the underline shown below the clickable text
                textPaint.isUnderlineText = isUnderline
            }

            override fun onClick(view: View) {
                Selection.setSelection((view as TextView).text as Spannable, 0)
                view.invalidate()
                link.second.onClick(view)
            }
        }
        startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)
        spannableString.setSpan(
            clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    this.movementMethod =
        LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}

fun ImageView.loadImage(fileStream: String, defaultImageResId: Int) {
    if (fileStream.isNotEmpty()) {
        val newImageData = Base64.decode(fileStream, Base64.DEFAULT)

        val bitmap = BitmapFactory.decodeByteArray(newImageData, 0, newImageData.size)
        this.setImageBitmap(bitmap ?: BitmapFactory.decodeResource(resources, defaultImageResId))
    } else {
        this.setImageResource(defaultImageResId)
    }
}


fun DataPoint.getStartTimeString(): String = DateFormat.getTimeInstance()
    .format(this.getStartTime(TimeUnit.MILLISECONDS))

fun DataPoint.getEndTimeString(): String = DateFormat.getTimeInstance()
    .format(this.getEndTime(TimeUnit.MILLISECONDS))