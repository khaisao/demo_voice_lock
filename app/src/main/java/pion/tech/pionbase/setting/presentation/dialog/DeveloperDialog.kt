package pion.tech.pionbase.setting.presentation.dialog

import android.os.Bundle
import com.piontech.core.base.BaseDialogFragment
import pion.tech.pionbase.R
import pion.tech.pionbase.databinding.DialogDeveloperBinding
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

class DeveloperDialog : BaseDialogFragment<DialogDeveloperBinding>(R.layout.dialog_developer) {

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setDialogCanCancel()
    }

    override fun addEvent(savedInstanceState: Bundle?) {
        super.addEvent(savedInstanceState)
        binding.ivClose.setPreventDoubleClickScaleView {
            dismiss()
        }
    }
}