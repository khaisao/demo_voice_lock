package pion.datlt.libads.admob.ads

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnPaidEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pion.datlt.libads.AdsController
import pion.datlt.libads.callback.AdCallback
import pion.datlt.libads.callback.PreloadCallback
import pion.datlt.libads.model.AdsChild
import pion.datlt.libads.utils.AdDef
import pion.datlt.libads.utils.AdsConstant
import pion.datlt.libads.utils.CommonUtils
import pion.datlt.libads.utils.StateLoadAd
import java.util.*

class AdmobBannerAdaptiveAds : AdmobAds() {

    private var adView: AdView? = null
    private var mAdCallback: AdCallback? = null
    private var error = ""
    private var stateLoadAd: StateLoadAd = StateLoadAd.NONE
    private var mPreloadCallback: PreloadCallback? = null

    private var mDestinationToShowAds: Int? = null

    private var adSize: AdSize? = null


    var adSourceId = ""
    var adSourceName = ""
    var adUnitId = ""

    override fun loadAndShow(
        activity: Activity,
        adsChild: AdsChild,
        destinationToShowAds: Int?,
        adCallback: AdCallback?,
        lifecycle: Lifecycle?,
        timeout: Long?,
        layoutToAttachAds: ViewGroup?,
        viewAdsInflateFromXml: View?,
        adChoice: Int?,
        positionCollapsibleBanner: String?,
        isOneTimeCollapsible: Boolean?,
        widthBannerAdaptiveAds: Int?,
        timeShowNativeCollapsibleAfterClose: Int?
    ) {
        mAdCallback = adCallback
        mDestinationToShowAds = destinationToShowAds

        if (stateLoadAd == StateLoadAd.LOADING) {
            //khong load cai moi nua
            //doi cai cu load xong roi show
        } else {
            //load cai moi
            load(
                activity = activity,
                adsChild = adsChild,
                isPreload = false,
                widthBannerAdaptiveAds = widthBannerAdaptiveAds,
                loadCallback = object : PreloadCallback {
                    override fun onLoadDone() {
                        show(
                            activity = activity,
                            adsChild = adsChild,
                            destinationToShowAds = destinationToShowAds,
                            layoutToAttachAds = layoutToAttachAds,
                            viewAdsInflateFromXml = viewAdsInflateFromXml,
                            lifecycle = lifecycle,
                            adCallback = adCallback,
                            timeShowNativeCollapsibleAfterClose = timeShowNativeCollapsibleAfterClose
                        )
                    }

                    override fun onLoadFail(error: String) {
                        super.onLoadFail(error)
                        adCallback?.onAdFailToLoad(error)
                    }
                }
            )
        }
    }

    override fun preload(
        activity: Activity,
        adsChild: AdsChild,
        positionCollapsibleBanner: String?,
        adChoice: Int?,
        isOneTimeCollapsible: Boolean?,
        widthBannerAdaptiveAds: Int?
    ) {
        load(
            activity = activity,
            adsChild = adsChild,
            widthBannerAdaptiveAds = widthBannerAdaptiveAds,
            isPreload = true
        )
    }

    private fun load(
        activity: Activity,
        adsChild: AdsChild,
        isPreload: Boolean,
        loadCallback: PreloadCallback? = null,
        widthBannerAdaptiveAds: Int? = null
    ) {
        Log.d(
            "TESTERADSEVENT",
            "start load banner adaptive : ads name ${adsChild.spaceName} id ${adsChild.adsId}"
        )

        CoroutineScope(Dispatchers.IO).launch {
            stateLoadAd = StateLoadAd.LOADING
            val idAds =
                if (AdsConstant.isDebug) AdsConstant.ID_ADMOB_BANNER_ADAPTIVE_TEST else adsChild.adsId


            adView = AdView(activity.applicationContext)
            adView?.setBackgroundColor(Color.WHITE)
            adView?.adUnitId = idAds
            adSize = getAdsize(activity, widthBannerAdaptiveAds)
            adView?.setAdSize(adSize!!)

            adView?.adListener = object : AdListener() {

                override fun onAdOpened() {
                    super.onAdOpened()
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    mAdCallback?.onAdClick()
                    Log.d(
                        "TESTERADSEVENT",
                        "click banner adaptive : ads name ${adsChild.spaceName} id ${adsChild.adsId}"
                    )

                }

                override fun onAdClosed() {
                    super.onAdClosed()
                    mAdCallback?.onAdClose()
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    super.onAdFailedToLoad(adError)
                    Log.d("TESTERADSEVENT", "load failed banner adaptive : ads name ${adsChild.spaceName} id ${adsChild.adsId} error : $error")
                    error = adError.message
                    stateLoadAd = StateLoadAd.LOAD_FAILED
                    mAdCallback?.onAdFailToLoad(error)
                    loadCallback?.onLoadFail(error)
                    if (isPreload) {
                        mPreloadCallback?.onLoadFail(error)
                    }
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    Log.d("TESTERADSEVENT", "load success banner adaptive : ads name ${adsChild.spaceName} id ${adsChild.adsId}")
                    timeLoader = Date().time
                    adView?.let {
                        it.responseInfo?.adapterResponses?.forEach { responseInfo ->
                            if (responseInfo.adSourceId.isNotEmpty()) {
                                adSourceId = responseInfo.adSourceId
                            }
                            if (responseInfo.adSourceName.isNotEmpty()) {
                                adSourceName = responseInfo.adSourceName
                            }
                        }
                        adUnitId = it.adUnitId
                    }

                    stateLoadAd = StateLoadAd.SUCCESS
                    loadCallback?.onLoadDone()
                    if (isPreload) {
                        mPreloadCallback?.onLoadDone()
                    }
                }
            }

            adView?.onPaidEventListener = OnPaidEventListener { adValue ->
                val bundle = Bundle().apply {
                    putString("ad_unit_id", adUnitId)
                    putInt("precision_type", adValue.precisionType)
                    putLong("revenue_micros", adValue.valueMicros)
                    putString("ad_source_id", adSourceId)
                    putString("ad_source_name", adSourceName)
                    putString("ad_type", AdDef.ADS_TYPE_ADMOB.BANNER_ADAPTIVE)
                    putString("currency_code", adValue.currencyCode)
                }
                mAdCallback?.onPaidEvent(bundle)
            }

            withContext(Dispatchers.Main) {
                adView?.loadAd(
                    AdRequest.Builder().build()
                )
            }
        }
    }

    override fun show(
        activity: Activity,
        adsChild: AdsChild,
        destinationToShowAds: Int?,
        adCallback: AdCallback?,
        lifecycle: Lifecycle?,
        layoutToAttachAds: ViewGroup?,
        viewAdsInflateFromXml: View?,
        timeShowNativeCollapsibleAfterClose: Int?
    ) {
        mAdCallback = adCallback
        mDestinationToShowAds = destinationToShowAds

        layoutToAttachAds?.let { viewG ->
            val lp = viewG.layoutParams
            lp.width = adSize?.getWidthInPixels(viewG.context) ?: 0
            lp.height = adSize?.getHeightInPixels(viewG.context) ?: 0
            viewG.layoutParams = lp
        }

        if (adView != null && layoutToAttachAds != null) {
            if (mDestinationToShowAds != null && mDestinationToShowAds != AdsController.currentDestinationId) {
                adCallback?.onAdFailToLoad("show in wrong destination")
                Log.d("TESTERADSEVENT", "show failed banner adaptive : ads name ${adsChild.spaceName} id ${adsChild.adsId} error : show in wrong destination")
            }else if (!wasLoadTimeLessThanNHoursAgo()) {
                stateLoadAd = StateLoadAd.SHOW_FAILED
                adCallback?.onAdFailToLoad("ads expired")
                Log.d(
                    "TESTERADSEVENT",
                    "show failed banner adaptive : ads name ${adsChild.spaceName} id ${adsChild.adsId} error : ads expired"
                )
            } else {
                layoutToAttachAds.removeAllViews()
                if (adView!!.parent != null) {
                    (adView!!.parent as ViewGroup).removeView(adView)
                }
                layoutToAttachAds.addView(adView)
                stateLoadAd = StateLoadAd.HAS_BEEN_OPENED
                CommonUtils.showToastDebug(activity, "Admob banner adaptive id: ${adsChild.adsId}")
                mAdCallback?.onAdShow()
                Log.d("TESTERADSEVENT", "show success banner adaptive : ads name ${adsChild.spaceName} id ${adsChild.adsId}")
            }
        } else {
            adCallback?.onAdFailToLoad("layout null")
            Log.d("TESTERADSEVENT", "show failed banner adaptive : ads name ${adsChild.spaceName} id ${adsChild.adsId} error : layout null")
        }
    }

    override fun setPreloadCallback(preloadCallback: PreloadCallback?) {
        mPreloadCallback = preloadCallback
    }

    override fun removePreloadCallback() {
        mPreloadCallback = null
    }

    override fun getStateLoadAd(): StateLoadAd {
        return stateLoadAd
    }


    private fun getAdsize(activity: Activity, widthBannerAdaptiveAds: Int?): AdSize {
        val display = activity.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val density = outMetrics.density
        val adWidth = if (widthBannerAdaptiveAds != null) {
            (widthBannerAdaptiveAds / density).toInt()
        } else {
            val widthPixels = outMetrics.widthPixels.toFloat()
            (widthPixels / density).toInt()
        }
        Log.d("CHECKWIDTHBANNER", "getAdsize: $adWidth $widthBannerAdaptiveAds")
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
            activity,
            adWidth
        )
    }
}