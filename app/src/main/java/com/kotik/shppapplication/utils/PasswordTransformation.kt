package com.kotik.shppapplication.utils

import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.TransformationMethod
import android.text.style.ImageSpan
import android.view.View

class PasswordTransformation(imgForPassword: MyImg) : TransformationMethod {

    private val drawableImg: Drawable? = imgForPassword.getImageDrawable()
    private val imageWidth = imgForPassword.getPassImgW().toInt()
    private val imageHeight = imgForPassword.getPassImgH().toInt()

    init {
        log("init")
        drawableImg?.setBounds(0, 0, imageWidth, imageHeight)
    }

    override fun getTransformation(source: CharSequence?, view: View?): CharSequence {
        log("getTransformation")
        return source?.let { transformLastChar(it) } ?: ""
    }

    private fun transformLastChar(text: CharSequence): CharSequence {
        log("# transformLastChar - start")
        val editable = Editable.Factory.getInstance().newEditable(text)
        val lastCharIndex = editable.length - 1

        if (lastCharIndex >= 0) {
            val lastChar = editable[lastCharIndex].toString()
            val transformedText = replaceWithIcon(lastChar)
            editable.replace(lastCharIndex, lastCharIndex + 1, transformedText)
        }
        log("# transformLastChar - end")
        return editable
    }

    private fun replaceWithIcon(text: CharSequence): CharSequence {
        log(text.toString())
        val spannableStringBuilder = SpannableStringBuilder()

        if (drawableImg != null) {

            val imageSpan = ImageSpan(drawableImg, ImageSpan.ALIGN_BOTTOM)
            spannableStringBuilder.append(text, imageSpan, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        return spannableStringBuilder
    }

    override fun onFocusChanged(
        view: View?,
        sourceText: CharSequence?,
        focused: Boolean,
        direction: Int,
        previouslyFocusedRect: Rect?
    ) {
        // Handle focus change event if needed
    }
}