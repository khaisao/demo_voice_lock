package pion.tech.pionbase.onboard.presentation.viewpager

import pion.tech.pionbase.databinding.PagerOnboard2Binding
import pion.tech.pionbase.onboard.presentation.OnboardFragment
import pion.tech.pionbase.onboard.presentation.adapter.OnboardAdapter

class OnboardViewHolder2(
    val binding: PagerOnboard2Binding,
    val onboardAdapter: OnboardAdapter,
    val fragment: OnboardFragment?
) :
    OnboardAdapter.OnboardViewHolder(binding.root) {

    override fun bind() {
    }


}