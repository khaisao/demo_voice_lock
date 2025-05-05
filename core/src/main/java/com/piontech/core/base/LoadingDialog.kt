package com.piontech.core.base

import com.piontech.core.R
import com.piontech.core.databinding.DialogLoadingBinding

class LoadingDialog : BaseDialogFragment<DialogLoadingBinding>(R.layout.dialog_loading) {
    companion object {
        @Volatile
        private var instance: LoadingDialog? = null

        fun getInstance(): LoadingDialog {
            return instance ?: synchronized(this) {
                instance ?: LoadingDialog().also { instance = it }
            }
        }
    }
}