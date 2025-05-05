package pion.datlt.libads.utils.adsuntils

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import pion.datlt.libads.AdsController
import pion.datlt.libads.callback.PreloadCallback
import pion.datlt.libads.model.ConfigAds
import pion.datlt.libads.model.ConfigNative
import pion.datlt.libads.utils.AdDef
import pion.datlt.libads.utils.AdsConstant
import pion.datlt.libads.utils.StateLoadAd
import java.util.*

fun checkConditionShowAds(context: Context?, spaceNameConfig: String): Boolean {
    context ?: return false
    val config: ConfigAds? = AdsConstant.listConfigAds[spaceNameConfig]
    val isOn = config?.isOn ?: false
    Log.d(
        "CHECKCONDITION",
        "checkConditionShowAds: " +
                "${AdsConstant.isInternetConnected} " +
                "${!AdsConstant.isPremium} " +
                "$isOn " +
                "${isOverTimeDelay(spaceNameConfig = spaceNameConfig)}"
    )

    if (AdsConstant.disableAllConfig) return false
    return (AdsConstant.isInternetConnected
            && !AdsConstant.isPremium
            && isOn
            && isOverTimeDelay(spaceNameConfig = spaceNameConfig))
}

private fun isOverTimeDelay(spaceNameConfig: String): Boolean {
    val config: ConfigAds? = AdsConstant.listConfigAds[spaceNameConfig]
    config ?: return false
    val timeDelay = config.timeDelayShowInter
    timeDelay ?: return true
    val isTimeOut =
        System.currentTimeMillis() - AdsController.lastTimeShowAdsInter > timeDelay * 1000
    Log.d("CHECKTIMEDELAYINTER", "isOverTimeDelay:$spaceNameConfig $isTimeOut")
    return isTimeOut
}

fun setLastTimeShowInter(spaceNameConfig: String) {

    AdsController.lastTimeShowAdsInter = System.currentTimeMillis()
    Log.d(
        "CHECKTIMEDELAYINTER",
        "setLastTimeShowInter:$spaceNameConfig ${AdsController.lastTimeShowAdsInter}"
    )
}

fun Fragment.safePreloadAds(
    spaceNameConfig: String,
    spaceNameAds: String,
    includeHasBeenOpened: Boolean? = null,
    positionCollapsibleBanner: String? = null,
    adChoice: Int? = null,
    isOneTimeCollapsible: Boolean? = null,
    preloadCallback: PreloadCallback? = null,
    widthBannerAdaptiveAds: Int? = null
) {
    val config = AdsConstant.listConfigAds[spaceNameConfig]
    val isOn = config?.isOn ?: false

    val isTypeEnable = checkAdsByType(spaceNameAds)

    if (AdsController.getInstance().checkAdsState(spaceNameAds) == StateLoadAd.SUCCESS) {
        preloadCallback?.onLoadDone()
    } else if (AdsController.getInstance().checkAdsState(spaceNameAds) == StateLoadAd.LOADING) {
        //set new call back
        AdsController.getInstance().setPreloadCallback(spaceNameAds, object : PreloadCallback {
            override fun onLoadDone() {
                preloadCallback?.onLoadDone()
            }

            override fun onLoadFail(error: String) {
                super.onLoadFail(error)
                preloadCallback?.onLoadFail(error)
            }

        })
    } else if (includeHasBeenOpened == true && AdsController.getInstance()
            .checkAdsState(spaceNameAds) == StateLoadAd.HAS_BEEN_OPENED
    ) {
        preloadCallback?.onLoadDone()
    } else if (!isTypeEnable) {
        preloadCallback?.onLoadFail("remote type off")
    } else {
        if (isOn) {
            //tinh toan ad choice
            val newAdChoice: Int? = adChoice
                ?: (config?.getConfigNative(
                    context = context,
                    default = ConfigNative(
                        adChoice = AdsConstant.TOP_LEFT
                    )
                )?.adChoice)



            AdsController.getInstance()
                .preload(
                    spaceName = spaceNameAds,
                    includeHasBeenOpened = includeHasBeenOpened,
                    positionCollapsibleBanner = positionCollapsibleBanner,
                    adChoice = newAdChoice,
                    isOneTimeCollapsible = isOneTimeCollapsible,
                    preloadCallback = object : PreloadCallback {
                        override fun onLoadDone() {
                            preloadCallback?.onLoadDone()
                        }

                        override fun onLoadFail(error: String) {
                            super.onLoadFail(error)
                            preloadCallback?.onLoadFail(error)
                        }

                    },
                    widthBannerAdaptiveAds = widthBannerAdaptiveAds
                )
        } else {
            Log.d(
                "TESTERADSEVENT",
                "load failed ads : ads name $spaceNameAds \n config name $spaceNameConfig \\n id ${
                    AdsController.getInstance().getAdsDetail(spaceNameAds)?.adsId ?: "null"
                }\n error : off by config"
            )
            preloadCallback?.onLoadFail("remote off")
        }
    }

}

fun Context.safePreloadAds(
    spaceNameConfig: String,
    spaceNameAds: String,
    includeHasBeenOpened: Boolean? = null,
    positionCollapsibleBanner: String? = null,
    adChoice: Int? = null,
    isOneTimeCollapsible: Boolean? = null,
    preloadCallback: PreloadCallback? = null,
    widthBannerAdaptiveAds: Int? = null
) {
    val context = this
    val config = AdsConstant.listConfigAds[spaceNameConfig]
    val isOn = config?.isOn ?: false

    val isTypeEnable = checkAdsByType(spaceNameAds)

    if (AdsController.getInstance().checkAdsState(spaceNameAds) == StateLoadAd.SUCCESS) {
        preloadCallback?.onLoadDone()
    } else if (AdsController.getInstance().checkAdsState(spaceNameAds) == StateLoadAd.LOADING) {
        //set new call back
        AdsController.getInstance().setPreloadCallback(spaceNameAds, object : PreloadCallback {
            override fun onLoadDone() {
                preloadCallback?.onLoadDone()
            }

            override fun onLoadFail(error: String) {
                super.onLoadFail(error)
                preloadCallback?.onLoadFail(error)
            }

        })
    } else if (includeHasBeenOpened == true && AdsController.getInstance()
            .checkAdsState(spaceNameAds) == StateLoadAd.HAS_BEEN_OPENED
    ) {
        preloadCallback?.onLoadDone()
    } else if (!isTypeEnable) {
        preloadCallback?.onLoadFail("remote type off")
    } else {
        if (isOn) {
            //tinh toan ad choice
            val newAdChoice: Int? = adChoice
                ?: (config?.getConfigNative(
                    context = context,
                    default = ConfigNative(
                        adChoice = AdsConstant.TOP_LEFT
                    )
                )?.adChoice)



            AdsController.getInstance()
                .preload(
                    spaceName = spaceNameAds,
                    includeHasBeenOpened = includeHasBeenOpened,
                    positionCollapsibleBanner = positionCollapsibleBanner,
                    adChoice = newAdChoice,
                    isOneTimeCollapsible = isOneTimeCollapsible,
                    preloadCallback = object : PreloadCallback {
                        override fun onLoadDone() {
                            preloadCallback?.onLoadDone()
                        }

                        override fun onLoadFail(error: String) {
                            super.onLoadFail(error)
                            preloadCallback?.onLoadFail(error)
                        }

                    },
                    widthBannerAdaptiveAds = widthBannerAdaptiveAds
                )
        } else {
            Log.d(
                "TESTERADSEVENT",
                "load failed ads : ads name $spaceNameAds \n config name $spaceNameConfig \\n id ${
                    AdsController.getInstance().getAdsDetail(spaceNameAds)?.adsId ?: "null"
                }\n error : off by config"
            )
            preloadCallback?.onLoadFail("remote off")
        }
    }

}

fun Fragment.safePreloadAds(
    listSpaceNameConfig: List<String>,
    spaceNameAds: String,
    includeHasBeenOpened: Boolean? = null,
    positionCollapsibleBanner: String? = null,
    adChoice: Int? = null,
    isOneTimeCollapsible: Boolean? = null,
    preloadCallback: PreloadCallback? = null,
    widthBannerAdaptiveAds: Int? = null
) {
    for (configName in listSpaceNameConfig) {
        if (AdsConstant.listConfigAds[configName]?.isOn == true) {
            safePreloadAds(
                spaceNameConfig = configName,
                spaceNameAds = spaceNameAds,
                includeHasBeenOpened = includeHasBeenOpened,
                positionCollapsibleBanner = positionCollapsibleBanner,
                adChoice = adChoice,
                isOneTimeCollapsible = isOneTimeCollapsible,
                preloadCallback = preloadCallback,
                widthBannerAdaptiveAds = widthBannerAdaptiveAds
            )
            break
        }
    }
}

fun checkAdsByType(spaceNameAds: String): Boolean {
    if (AdsConstant.disableAllConfig) {
        return false
    }
    val adsDetail = AdsController.getInstance().getAdsDetail(spaceNameAds)
    return when (adsDetail?.adsType) {
        //admob
        AdDef.ADS_TYPE_ADMOB.OPEN_APP -> {
            return AdsConstant.isOpenAppOn
        }

        AdDef.ADS_TYPE_ADMOB.INTERSTITIAL -> {
            return AdsConstant.isInterstitialOn
        }

        AdDef.ADS_TYPE_ADMOB.NATIVE -> {
            return AdsConstant.isNativeOn
        }

        AdDef.ADS_TYPE_ADMOB.NATIVE_FULL_SCREEN -> {
            return AdsConstant.isNativeFullScreenOn
        }

        AdDef.ADS_TYPE_ADMOB.BANNER -> {
            return AdsConstant.isBannerOn
        }

        AdDef.ADS_TYPE_ADMOB.BANNER_ADAPTIVE -> {
            return AdsConstant.isBannerAdaptiveOn
        }

        AdDef.ADS_TYPE_ADMOB.BANNER_LARGE -> {
            return AdsConstant.isBannerLargeOn
        }

        AdDef.ADS_TYPE_ADMOB.BANNER_INLINE -> {
            return AdsConstant.isBannerInlineOn
        }

        AdDef.ADS_TYPE_ADMOB.BANNER_COLLAPSIBLE -> {
            return AdsConstant.isBannerCollapsibleOn
        }

        AdDef.ADS_TYPE_ADMOB.REWARD_VIDEO -> {
            return AdsConstant.isRewardVideoOn
        }

        AdDef.ADS_TYPE_ADMOB.REWARD_INTERSTITIAL -> {
            return AdsConstant.isRewardInterOn
        }

        else -> {
            false
        }
    }
}

fun checkIsPreloadAfterShow(spaceNameConfig: String): Boolean {
    return AdsConstant.listConfigAds[spaceNameConfig]?.isPreloadAfterShow ?: false
}