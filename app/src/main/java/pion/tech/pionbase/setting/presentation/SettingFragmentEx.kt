package pion.tech.pionbase.setting.presentation

import android.annotation.SuppressLint
import android.content.Intent
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.libiap.IAPConnector
import pion.datlt.libads.AdsController
import pion.datlt.libads.utils.isNeedToShowConsent
import pion.datlt.libads.utils.resetConsent
import pion.datlt.libads.utils.showPolicyForm
import pion.tech.pionbase.BuildConfig
import pion.tech.pionbase.setting.presentation.dialog.AdvertisementDialog
import pion.tech.pionbase.setting.presentation.dialog.DeveloperDialog
import pion.tech.pionbase.util.Constant
import pion.tech.pionbase.util.gone
import pion.tech.pionbase.util.setPreventDoubleClickScaleView
import pion.tech.pionbase.util.show
import androidx.core.net.toUri
import pion.tech.pionbase.R

fun SettingFragment.backEvent() {
    onSystemBack {
        onBackPressed()
    }
    binding.ivBack.setPreventDoubleClickScaleView {
        onBackPressed()
    }
}

fun SettingFragment.onBackPressed() {
    findNavController().popBackStack()
}

@SuppressLint("SetTextI18n")
fun SettingFragment.bindView() {
    val remoteResult = if (Constant.isRemoteConfigSuccess) {
        "R"
    } else {
        "D"
    }

    binding.txvVersion.text =
        buildString {
            append("Application version: v")
            append(" ")
            append(remoteResult)
            append(" ")
            append(BuildConfig.VERSION_CODE)
            append(" ")
            append(BuildConfig.VERSION_NAME)
        }
}

fun SettingFragment.languageEvent() {
    binding.btnLanguage.setPreventDoubleClickScaleView {
        navigator.navigateTo(R.id.action_settingFragment_to_languageFragment)
    }
}

fun SettingFragment.developerEvent() {
    binding.btnDeveloper.setPreventDoubleClickScaleView {
        DeveloperDialog().show(childFragmentManager)
    }
}

fun SettingFragment.advertisementEvent() {
    binding.btnAdvertisement.setPreventDoubleClickScaleView {
        AdvertisementDialog().show(childFragmentManager)
    }
}

fun SettingFragment.policyEvent() {
    binding.btnPolicy.setPreventDoubleClickScaleView {
        runCatching {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                "https://sites.google.com/piontech.co/voicelockscreen".toUri()
            )
            startActivity(browserIntent)
        }
    }
}

fun SettingFragment.resetIapEvent() {
    if (BuildConfig.DEBUG) {
        binding.btnResetIap.isVisible = true
        binding.btnResetIap.setPreventDoubleClickScaleView {
            activity?.let { IAPConnector.resetIap(it) }
        }
    } else {
        binding.btnResetIap.isVisible = false
    }
}

fun SettingFragment.gdprEvent() {
    binding.btnGdpr.setPreventDoubleClickScaleView {
        runCatching {
            AdsController.getInstance().showPolicyForm(
                onShow = {
                    //do nothing
                },
                onError = {
                    //do nothing
                }
            )
        }
    }

    if (AdsController.getInstance().isNeedToShowConsent() || BuildConfig.DEBUG) {
        binding.btnGdpr.show()
    } else {
        binding.btnGdpr.gone()
    }


}

fun SettingFragment.resetGDPR() {
    if (BuildConfig.DEBUG) {
        binding.btnResetGdpr.setPreventDoubleClickScaleView {
            runCatching {
                AdsController.getInstance().resetConsent()
            }
        }
    } else {
        binding.btnResetGdpr.gone()
    }
}