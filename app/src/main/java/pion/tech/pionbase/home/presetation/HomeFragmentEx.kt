package pion.tech.pionbase.home.presetation

import pion.tech.pionbase.R
import pion.tech.pionbase.util.setPreventDoubleClickScaleView
import pion.tech.pionbase.home.presetation.bottomSheet.DemoBottomSheet

fun HomeFragment.initView() {
}

fun HomeFragment.plusEvent() {

}

fun HomeFragment.onBackEvent() {
    onSystemBack {
        backEvent()
    }
}

fun HomeFragment.backEvent() {
}

fun HomeFragment.settingEvent() {
    binding.btnRegister.setPreventDoubleClickScaleView {
        navigator.navigateTo(R.id.action_homeFragment_to_registerFragment)
    }
}
