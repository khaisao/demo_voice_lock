package pion.tech.pionbase.home.presetation.bottomSheet

import android.os.Bundle
import com.piontech.core.base.BaseBottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.R
import pion.tech.pionbase.databinding.BottomSheetDemoBinding

@AndroidEntryPoint
class DemoBottomSheet :
    BaseBottomSheetDialogFragment<BottomSheetDemoBinding>(R.layout.bottom_sheet_demo) {
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
    }
}