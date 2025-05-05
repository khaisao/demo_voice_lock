package pion.datlt.libads.admob.ads

import android.app.Activity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.ads.AdActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnPaidEventListener
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
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

class AdmobRewardInterstitialAds : AdmobAds() {

    private var stateLoadAd: StateLoadAd = StateLoadAd.NONE
    private var rewardedAd: RewardedInterstitialAd? = null
    private var isTimeOut: Boolean = false

    private var eventLifecycle: Lifecycle.Event = Lifecycle.Event.ON_RESUME
    private var error = ""

    private var mActivity: Activity? = null
    private var mAdsChild: AdsChild? = null

    private var mDestinationToShowAds : Int? = null

    private var mPreloadCallback : PreloadCallback? = null
    private var mAdCallback : AdCallback? = null

    //bundle
    private var adSourceId = ""
    private var adSourceName = ""
    private var adUnitId = ""

    private val handler = Handler(Looper.getMainLooper())

    private var mLifecycle: Lifecycle? = null

    private val countDownShowAds = object : CountDownTimer(4000L , 4000L) {
        override fun onTick(p0: Long) {

        }

        override fun onFinish() {
            rewardedAd = null
            stateLoadAd = StateLoadAd.SHOW_FAILED
            error = "timeout show ads"
            if (eventLifecycle == Lifecycle.Event.ON_RESUME) {
                mAdCallback?.onAdFailToLoad(error)
                mLifecycle?.removeObserver(lifecycleObserver)
                Log.d("TESTERADSEVENT", "show failed reward interstitial : ads name ${mAdsChild?.spaceName} id ${mAdsChild?.adsId} error : timeout show ads")

            }
        }

    }

    private val timeoutCallback = Runnable {
        if (
            stateLoadAd != StateLoadAd.SUCCESS
            && stateLoadAd != StateLoadAd.LOAD_FAILED
            && stateLoadAd != StateLoadAd.SHOW_FAILED
        ){
            //none hoac loading
            isTimeOut = true
            if (eventLifecycle == Lifecycle.Event.ON_RESUME){
                mAdCallback?.onAdFailToLoad("TimeOut")
                mLifecycle?.removeObserver(lifecycleObserver)
                Log.d("TESTERADSEVENT", "show failed reward interstitial : ads name ${mAdsChild?.spaceName} id ${mAdsChild?.adsId} error : TimeOut")

            }
        }
    }

    private val lifecycleObserver = object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            eventLifecycle = event
            if (event == Lifecycle.Event.ON_RESUME) {
//                if (isTimeOut){
//                    mLifecycle?.removeObserver(this)
//                    mAdCallback?.onAdFailToLoad("TimeOut")
//                    Log.d("TESTERADSEVENT", "show failed reward interstitial : ads name ${mAdsChild?.spaceName} id ${mAdsChild?.adsId} error : TimeOut")
//
//                }else
                if (stateLoadAd == StateLoadAd.SUCCESS) {
                    if (mActivity != null && mAdsChild != null) {
                        show(
                            activity = mActivity!!,
                            adsChild = mAdsChild!!,
                            destinationToShowAds = mDestinationToShowAds,
                            adCallback = mAdCallback,
                            lifecycle = mLifecycle,
                            layoutToAttachAds = null,
                            viewAdsInflateFromXml = null,
                            timeShowNativeCollapsibleAfterClose = 0
                        )
                    } else {
                        mLifecycle?.removeObserver(this)
                        mAdCallback?.onAdFailToLoad("activity or adsChild must not null")
                        Log.d(
                            "TESTERADSEVENT",
                            "show failed reward interstitial : ads name ${mAdsChild?.spaceName} id ${mAdsChild?.adsId} error : activity or adsChild must not null"
                        )

                    }
                } else {
                    mLifecycle?.removeObserver(this)
                    mAdCallback?.onAdFailToLoad(error)
                    Log.d(
                        "TESTERADSEVENT",
                        "show failed reward interstitial : ads name ${mAdsChild?.spaceName} id ${mAdsChild?.adsId} error : $error"
                    )

                }
            }
        }
    }


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
        mActivity = activity
        mAdsChild = adsChild
        mDestinationToShowAds = destinationToShowAds
        mAdCallback = adCallback
        mLifecycle = lifecycle

        if (stateLoadAd == StateLoadAd.LOADING) {
            //không load cái mới nữa
            //chờ load xong rồi show
        } else if (stateLoadAd == StateLoadAd.SUCCESS) {
            //show luôn
            show(
                activity = activity,
                adsChild = adsChild,
                destinationToShowAds = destinationToShowAds,
                adCallback = adCallback,
                lifecycle = lifecycle,
                layoutToAttachAds = layoutToAttachAds,
                viewAdsInflateFromXml = viewAdsInflateFromXml,
                timeShowNativeCollapsibleAfterClose = timeShowNativeCollapsibleAfterClose
            )
        } else {
            //load quảng cáo mới
            load(
                activity = activity,
                adsChild = adsChild,
                isPreload = false,
                loadCallback = object : PreloadCallback {
                    override fun onLoadDone() {
                        if (isTimeOut) {
                            //khong show nua
                            //reset timeout
                            isTimeOut = false //reset khi load qua thoi gian
                        } else {
                            //show nhu binh thuong
                            show(
                                activity = activity,
                                adsChild = adsChild,
                                destinationToShowAds = destinationToShowAds,
                                adCallback = adCallback,
                                lifecycle = lifecycle,
                                layoutToAttachAds = layoutToAttachAds,
                                viewAdsInflateFromXml = viewAdsInflateFromXml,
                                timeShowNativeCollapsibleAfterClose = timeShowNativeCollapsibleAfterClose
                            )
                        }
                    }

                    override fun onLoadFail(error: String) {
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
            isPreload = true
        )
    }

    private fun load(
        activity: Activity,
        adsChild: AdsChild,
        isPreload: Boolean,
        timeout: Long = AdsConstant.TIME_OUT_DEFAULT,
        loadCallback: PreloadCallback? = null
    ) {

        Log.d(
            "TESTERADSEVENT",
            "start load reward interstitial : ads name ${adsChild.spaceName} id ${adsChild.adsId}"
        )

        CoroutineScope(Dispatchers.IO).launch {
            mActivity = activity
            mAdsChild = adsChild
            isTimeOut = false



            stateLoadAd = StateLoadAd.LOADING
            val id =
                if (AdsConstant.isDebug) AdsConstant.ID_ADMOB_REWARD_INTERSTITIAL_TEST else adsChild.adsId

            if (!isPreload) {
                handler.removeCallbacks(timeoutCallback)
                handler.postDelayed(timeoutCallback, timeout)
            }


            val rewardAdCallback = object : RewardedInterstitialAdLoadCallback() {
                override fun onAdLoaded(ads: RewardedInterstitialAd) {
                    super.onAdLoaded(ads)
                    Log.d(
                        "TESTERADSEVENT",
                        "load success reward interstitial : ads name ${adsChild.spaceName} id ${adsChild.adsId}"
                    )

                    rewardedAd = ads
                    timeLoader = Date().time
                    stateLoadAd = StateLoadAd.SUCCESS
                    loadCallback?.onLoadDone()
                    handler.removeCallbacks(timeoutCallback)
                    if (isPreload) {
                        mPreloadCallback?.onLoadDone()
                    }

                    rewardedAd?.let {
                        it.responseInfo.adapterResponses.forEach { responseInfo ->
                            if (responseInfo.adSourceId.isNotEmpty()) {
                                adSourceId = responseInfo.adSourceId
                            }
                            if (responseInfo.adSourceName.isNotEmpty()) {
                                adSourceName = responseInfo.adSourceName
                            }
                        }
                        adUnitId = it.adUnitId
                    }
                }

                override fun onAdFailedToLoad(loadError: LoadAdError) {
                    super.onAdFailedToLoad(loadError)
                    Log.d(
                        "TESTERADSEVENT",
                        "load failed reward interstitial : ads name ${adsChild.spaceName} id ${adsChild.adsId} error : ${loadError.message}"
                    )
                    error = loadError.message
                    stateLoadAd = StateLoadAd.LOAD_FAILED
                    loadCallback?.onLoadFail(loadError.message)
                    handler.removeCallbacks(timeoutCallback)
                    if (isPreload) {
                        mPreloadCallback?.onLoadFail(loadError.message)
                    }
                }
            }
            val request = AdRequest.Builder().build()
            withContext(Dispatchers.Main) {
                RewardedInterstitialAd.load(
                    activity, id, request, rewardAdCallback
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
        mActivity = activity
        mAdsChild = adsChild
        mDestinationToShowAds = destinationToShowAds
        mAdCallback = adCallback
        mLifecycle = lifecycle

        rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {

            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                Log.d(
                    "TESTERADSEVENT",
                    "close reward interstitial : ads name ${adsChild.spaceName} id ${adsChild.adsId}"
                )

                countDownShowAds.cancel()
                rewardedAd = null
                adCallback?.onAdClose()
                lifecycle?.removeObserver(lifecycleObserver)
            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
                //goi khi quang cao duoc show len
                Log.d(
                    "TESTERADSEVENT",
                    "show success reward interstitial : ads name ${adsChild.spaceName} id ${adsChild.adsId}"
                )

                countDownShowAds.cancel()
                rewardedAd = null
                stateLoadAd = StateLoadAd.HAS_BEEN_OPENED
                lifecycle?.removeObserver(lifecycleObserver)
                CommonUtils.showToastDebug(activity, "Admob Reward id: ${adsChild.adsId}")
                adCallback?.onAdShow()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                super.onAdFailedToShowFullScreenContent(adError)
                Log.d(
                    "TESTERADSEVENT",
                    "show failed reward interstitial : ads name ${adsChild.spaceName} id ${adsChild.adsId} error : $error"
                )

                countDownShowAds.cancel()
                rewardedAd = null
                stateLoadAd = StateLoadAd.SHOW_FAILED
                error = adError.message
                if (eventLifecycle == Lifecycle.Event.ON_RESUME) {
                    adCallback?.onAdFailToLoad(adError.message)
                    lifecycle?.removeObserver(lifecycleObserver)
                }
            }

            override fun onAdClicked() {
                super.onAdClicked()
                adCallback?.onAdClick()
            }


            override fun onAdImpression() {
                super.onAdImpression()
            }

        }

        rewardedAd?.onPaidEventListener = OnPaidEventListener { adValue ->
            val bundle = Bundle().apply {
                putString("ad_unit_id", adUnitId)
                putInt("precision_type", adValue.precisionType)
                putLong("revenue_micros", adValue.valueMicros)
                putString("ad_source_id", adSourceId)
                putString("ad_source_name", adSourceName)
                putString("ad_type", AdDef.ADS_TYPE_ADMOB.REWARD_INTERSTITIAL)
                putString("currency_code", adValue.currencyCode)
            }
            adCallback?.onPaidEvent(bundle)
        }

        lifecycle?.let {
            if (lifecycle.currentState != Lifecycle.State.RESUMED) {
                lifecycle.removeObserver(lifecycleObserver)
                lifecycle.addObserver(lifecycleObserver)
            }
        }

        if (eventLifecycle == Lifecycle.Event.ON_RESUME && !isTimeOut) {
            if (mDestinationToShowAds != null && mDestinationToShowAds != AdsController.currentDestinationId) {
                adCallback?.onAdFailToLoad("show in wrong destination")
                Log.d(
                    "TESTERADSEVENT",
                    "show failed reward interstitial : ads name ${adsChild.spaceName} id ${adsChild.adsId} error : show in wrong destination"
                )

            } else if (!wasLoadTimeLessThanNHoursAgo()) {
                stateLoadAd = StateLoadAd.SHOW_FAILED
                adCallback?.onAdFailToLoad("ads expired")
                Log.d(
                    "TESTERADSEVENT",
                    "show failed reward interstitial : ads name ${adsChild.spaceName} id ${adsChild.adsId} error : ads expired"
                )

            } else {
                rewardedAd?.show(activity) {
                    adCallback?.onGotReward()
                    Log.d(
                        "TESTERADSEVENT",
                        "got reward interstitial : ads name ${adsChild.spaceName} id ${adsChild.adsId}"
                    )
                }
                //bat dau dem nguoc
                countDownShowAds.start()
            }
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
}