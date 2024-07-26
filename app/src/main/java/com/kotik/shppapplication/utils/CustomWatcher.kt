package com.kotik.shppapplication.utils

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText

class CustomWatcher(private val textInput: TextInputEditText) : TextWatcher {

    private var oldText = ""

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        log("***before***")}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        log("***on***")
        procTransformation(s)
    }

    override fun afterTextChanged(s: Editable?) {
        log("***after***")
        textInput.setSelection(textInput.text.toString().length)
    }

    private fun procTransformation(s: CharSequence?) {
        if (s.toString() != oldText) {
            oldText = s.toString()
            textInput.setText(oldText)
            val transformedText = textInput.transformationMethod.getTransformation(s, null)
            textInput.text = Editable.Factory.getInstance().newEditable(transformedText)
        }
    }
}

fun TextInputEditText.changeTextToDots() {
    addTextChangedListener(CustomWatcher(this))
}