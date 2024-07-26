package com.kotik.shppapplication.utils

import android.graphics.drawable.Drawable

class MyImg(private val imageDrawable: Drawable?,
            private val passImgW: Float,
            private val passImgH: Float
) {

    fun getImageDrawable(): Drawable? {
        return imageDrawable
    }
    fun getPassImgW(): Float {
        return passImgW
    }
    fun getPassImgH(): Float {
        return passImgH
    }
}