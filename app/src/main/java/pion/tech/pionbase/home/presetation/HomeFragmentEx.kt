package pion.tech.pionbase.home.presetation

import com.k2fsa.sherpa.onnx.SpeakerRecognition
import pion.tech.pionbase.R
import pion.tech.pionbase.util.setPreventDoubleClickScaleView
import pion.tech.pionbase.home.presetation.bottomSheet.DemoBottomSheet
import pion.tech.pionbase.util.setPreventDoubleClick
import timber.log.Timber

fun HomeFragment.initView() {
    viewModel.getAllSpeaker()
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

    binding.btnTest.setPreventDoubleClickScaleView {
        navigator.navigateTo(R.id.action_homeFragment_to_testSpeakerFragment)
    }
}

private const val TAG = "HomeFragmentEx"
fun HomeFragment.clearDataEvent(){
    binding.btnClearData.setPreventDoubleClick {
        viewModel.deleteAllSpeaker()
        viewModel.getAllSpeaker()

    }
}