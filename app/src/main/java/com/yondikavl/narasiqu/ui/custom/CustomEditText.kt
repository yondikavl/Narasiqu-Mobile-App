package com.yondikavl.narasiqu.ui.custom

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText

class CustomEditText : TextInputEditText {
    constructor(context: Context) : super(context) {
        //
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        //
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        //
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (s.toString().length < 8) {
            setError("Password minimal 8 karakter", null)
        } else {
            error = null
        }
    }
}