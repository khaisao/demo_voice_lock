package pion.tech.pionbase.onboard.presentation

import android.view.View
import com.piontech.core.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.R
import pion.tech.pionbase.main.presentation.CommonViewModel
import pion.tech.pionbase.databinding.FragmentOnboardBinding
import pion.tech.pionbase.onboard.presentation.adapter.OnboardAdapter

@AndroidEntryPoint
class OnboardFragment : BaseFragment<FragmentOnboardBinding, OnboardViewModel, CommonViewModel>(
    FragmentOnboardBinding::inflate,
    OnboardViewModel::class.java,
    CommonViewModel::class.java,
), OnboardAdapter.Listener {
    var adapter: OnboardAdapter? = null
    override fun init(view: View) {
        initView()
        onBackEvent()
    }

    override fun subscribeObserver(view: View) {

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDoneOnboard() {
        navigator.navigateTo(R.id.action_onboardFragment_to_homeFragment)
    }
}