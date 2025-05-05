package pion.tech.pionbase.setting.presentation.dialog

import android.os.Bundle
import com.piontech.core.base.BaseDialogFragment
import pion.tech.pionbase.R
import pion.tech.pionbase.databinding.DialogAdvertisementBinding
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

class AdvertisementDialog : BaseDialogFragment<DialogAdvertisementBinding>(R.layout.dialog_advertisement) {

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