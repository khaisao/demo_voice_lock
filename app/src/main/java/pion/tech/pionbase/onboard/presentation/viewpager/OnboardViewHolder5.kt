package pion.tech.pionbase.onboard.presentation.viewpager

import pion.tech.pionbase.databinding.LayoutIapBinding
import pion.tech.pionbase.onboard.presentation.OnboardFragment
import pion.tech.pionbase.onboard.presentation.adapter.OnboardAdapter
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

class OnboardViewHolder5(
    val binding: LayoutIapBinding,
    private val onboardAdapter: OnboardAdapter,
    val fragment: OnboardFragment?
) :
    OnboardAdapter.OnboardViewHolder(binding.root) {

    override fun bind() {
        initView()
    }

    private fun initView() {
        binding.tvNext.setPreventDoubleClickScaleView {
            onboardAdapter.getListener()?.onDoneOnboard()
        }
    }

}