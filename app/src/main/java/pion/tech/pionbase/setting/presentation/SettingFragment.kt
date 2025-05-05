package pion.tech.pionbase.setting.presentation

import android.view.View
import com.piontech.core.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.main.presentation.CommonViewModel
import pion.tech.pionbase.databinding.FragmentSettingBinding

@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding, SettingViewModel, CommonViewModel>(
    FragmentSettingBinding::inflate,
    SettingViewModel::class.java,
    CommonViewModel::class.java,
) {
    override fun init(view: View) {
        backEvent()
        bindView()
        languageEvent()
        developerEvent()
        advertisementEvent()
        policyEvent()
        resetIapEvent()
        gdprEvent()
        resetGDPR()
    }

    override fun subscribeObserver(view: View) {

    }
}