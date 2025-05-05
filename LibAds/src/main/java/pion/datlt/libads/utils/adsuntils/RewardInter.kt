package pion.datlt.libads.utils.adsuntils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import pion.datlt.libads.AdsController
import pion.datlt.libads.callback.AdCallback
import pion.datlt.libads.callback.PreloadCallback
import pion.datlt.libads.utils.DialogLoadAdsUtils
import pion.datlt.libads.utils.StateLoadAd

fun Fragment.loadAndShowRewardInter(
    spaceNameConfig: String,
    spaceName: String,
    timeOut: Long = 7000L,
    destinationToShowAds: Int? = null,
    isScreenType: Boolean = true,
    onRewardDone: ((Boolean) -> Unit),
    onGetReward: (() -> Unit)? = null
) {
    if (checkConditionShowAds(context, spaceNameConfig)) {
        AdsController.isInterIsShowing = true
        var fragmentEvent = Lifecycle.Event.ON_ANY
        val callback = object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

            }

            override fun onActivityStarted(activity: Activity) {

            }

            override fun onActivityResumed(activity: Activity) {

            }

            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStopped(activity: Activity) {
                fragmentEvent = Lifecycle.Event.ON_STOP
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

            }

            override fun onActivityDestroyed(activity: Activity) {

            }
        }
        AdsController.getInstance().activity.application.registerActivityLifecycleCallbacks(callback)
        val lifecycleObserver = object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_STOP) {
                    fragmentEvent = event
                }
            }
        }
        DialogLoadAdsUtils.getInstance().showDialogLoadingAds(activity, isScreenType)

        var isRewardGot = false

        AdsController.getInstance().loadAndShow(
            spaceName = spaceName,
            destinationToShowAds = destinationToShowAds,
            lifecycle = lifecycle,
            timeout = timeOut,
            adCallback = object : AdCallback {
                override fun onAdShow() {
                    DialogLoadAdsUtils.getInstance().hideDialogLoadingAds()
                    AdsController.isInterIsShowing = true
                }

                override fun onAdClose() {
                    AdsController.isInterIsShowing = false
                    AdsController.isBlockOpenAds = fragmentEvent == Lifecycle.Event.ON_STOP
                    AdsController.getInstance().activity.application.unregisterActivityLifecycleCallbacks(
                        callback
                    )
                    lifecycle.removeObserver(lifecycleObserver)
                    onRewardDone.invoke(isRewardGot)
                }

                override fun onAdFailToLoad(messageError: String?) {
                    DialogLoadAdsUtils.getInstance().hideDialogLoadingAds(0)
                    AdsController.isInterIsShowing = false
                    AdsController.isBlockOpenAds = fragmentEvent == Lifecycle.Event.ON_STOP
                    AdsController.getInstance().activity.application.unregisterActivityLifecycleCallbacks(
                        callback
                    )
                    lifecycle.removeObserver(lifecycleObserver)
                    onRewardDone.invoke(false)
                }

                override fun onGotReward() {
                    super.onGotReward()
                    isRewardGot = true
                    onGetReward?.invoke()
                }

                override fun onAdClick() {
                    super.onAdClick()
                    AdsController.isBlockOpenAds = true
                }

            }
        )

    } else {
        onRewardDone.invoke(false)
    }
}

fun Fragment.showLoadedRewardInter(
    spaceNameConfig: String,
    spaceName: String,
    timeOut: Long = 7000L,
    isPreloadAfterShow: Boolean = false,
    destinationToShowAds: Int? = null,
    isShowLoadingView: Boolean = false,
    timeShowLoadingView: Long = 1000L,
    isScreenType: Boolean = true,
    onRewardDone: ((Boolean) -> Unit),
    onGetReward: (() -> Unit)? = null
) {
    if (checkConditionShowAds(context, spaceNameConfig)) {

        AdsController.isInterIsShowing = true
        var fragmentEvent = Lifecycle.Event.ON_ANY
        val callback = object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

            }

            override fun onActivityStarted(activity: Activity) {

            }

            override fun onActivityResumed(activity: Activity) {

            }

            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStopped(activity: Activity) {
                fragmentEvent = Lifecycle.Event.ON_STOP
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

            }

            override fun onActivityDestroyed(activity: Activity) {

            }
        }
        AdsController.getInstance().activity.application.registerActivityLifecycleCallbacks(callback)
        val lifecycleObserver = object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_STOP) {
                    fragmentEvent = event
                }
            }
        }

        val countDownTimer = object : CountDownTimer(timeShowLoadingView, timeShowLoadingView) {
            override fun onTick(p0: Long) {
                //do nothing
            }

            override fun onFinish() {
                var isRewardGot = false

                AdsController.getInstance().showLoadedAds(
                    spaceName = spaceName,
                    destinationToShowAds = destinationToShowAds,
                    lifecycle = lifecycle,
                    timeout = timeOut,
                    adCallback = object : AdCallback {


                        override fun onAdShow() {
                            DialogLoadAdsUtils.getInstance().hideDialogLoadingAds()
                            AdsController.isInterIsShowing = true
                        }

                        override fun onAdClose() {
                            AdsController.isInterIsShowing = false
                            AdsController.isBlockOpenAds = fragmentEvent == Lifecycle.Event.ON_STOP
                            AdsController.getInstance().activity.application.unregisterActivityLifecycleCallbacks(
                                callback
                            )
                            lifecycle.removeObserver(lifecycleObserver)
                            if (isPreloadAfterShow) {
                                safePreloadAds(
                                    spaceNameAds = spaceName,
                                    spaceNameConfig = spaceNameConfig
                                )
                            }
                            onRewardDone.invoke(isRewardGot)
                        }

                        override fun onAdFailToLoad(messageError: String?) {
                            DialogLoadAdsUtils.getInstance().hideDialogLoadingAds(0)
                            AdsController.isInterIsShowing = false
                            AdsController.isBlockOpenAds = fragmentEvent == Lifecycle.Event.ON_STOP
                            AdsController.getInstance().activity.application.unregisterActivityLifecycleCallbacks(
                                callback
                            )
                            lifecycle.removeObserver(lifecycleObserver)
                            if (isPreloadAfterShow) {
                                safePreloadAds(
                                    spaceNameAds = spaceName,
                                    spaceNameConfig = spaceNameConfig
                                )
                            }
                            onRewardDone.invoke(false)
                        }

                        override fun onGotReward() {
                            super.onGotReward()
                            isRewardGot = true
                            onGetReward?.invoke()
                        }

                        override fun onAdClick() {
                            super.onAdClick()
                            AdsController.isBlockOpenAds = true
                        }

                    }

                )
            }

        }

        if (isShowLoadingView) {
            DialogLoadAdsUtils.getInstance().showDialogLoadingAds(activity, isScreenType)
            countDownTimer.start()
        } else {
            countDownTimer.onFinish()
        }


    } else {
        onRewardDone.invoke(false)
    }
}

fun Fragment.show3LoadedRewardInter(
    spaceNameConfig: String,
    spaceName1: String,
    spaceName2: String,
    spaceName3: String,
    timeOut: Long = 7000L,
    isPreloadAfterShow: Boolean = false,
    destinationToShowAds: Int? = null,
    isShowLoadingView: Boolean = false,
    timeShowLoadingView: Long = 1000L,
    isScreenType: Boolean = true,
    onRewardDone: ((Boolean) -> Unit),
    onGetReward: (() -> Unit)? = null
){
    if (checkConditionShowAds(context, spaceNameConfig)) {
        AdsController.isInterIsShowing = true


        val countDownTimer = object : CountDownTimer(timeShowLoadingView, timeShowLoadingView) {
            override fun onTick(p0: Long) {
                //do nothing
            }

            override fun onFinish() {

                //bat dau load tu day
                var stateAds1 = StateLoadAd.LOADING
                var stateAds2 = StateLoadAd.LOADING
                var stateAds3 = StateLoadAd.LOADING


                var isTimeOut = false
                val timeOutRunnable = Runnable {
                    isTimeOut = true
                    if (stateAds1 == StateLoadAd.HAS_BEEN_OPENED || stateAds2 == StateLoadAd.HAS_BEEN_OPENED || stateAds3 == StateLoadAd.HAS_BEEN_OPENED){
                        AdsController.isInterIsShowing = false
                    }

                    if (stateAds1 == StateLoadAd.SUCCESS){
                        //show 1
                        showLoadedRewardInter(
                            spaceNameConfig = spaceNameConfig,
                            spaceName = spaceName1,
                            isPreloadAfterShow = isPreloadAfterShow,
                            destinationToShowAds = destinationToShowAds,
                            isShowLoadingView = false,
                            onRewardDone = onRewardDone,
                            onGetReward = onGetReward)
                    }else if (stateAds2 == StateLoadAd.SUCCESS){
                        //show 2
                        showLoadedRewardInter(
                            spaceNameConfig = spaceNameConfig,
                            spaceName = spaceName2,
                            isPreloadAfterShow = isPreloadAfterShow,
                            destinationToShowAds = destinationToShowAds,
                            isShowLoadingView = false,
                            onRewardDone = onRewardDone,
                            onGetReward = onGetReward)
                    }else if (stateAds3 == StateLoadAd.SUCCESS){
                        //show 3
                        showLoadedRewardInter(
                            spaceNameConfig = spaceNameConfig,
                            spaceName = spaceName3,
                            isPreloadAfterShow = isPreloadAfterShow,
                            destinationToShowAds = destinationToShowAds,
                            isShowLoadingView = false,
                            onRewardDone = onRewardDone,
                            onGetReward = onGetReward)
                    }else{
                        AdsController.isInterIsShowing = false
                    }
                }

                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed(timeOutRunnable , timeOut)

                fun checkShowAds(){
                    if (isTimeOut) return
                    if (stateAds1 == StateLoadAd.HAS_BEEN_OPENED || stateAds2 == StateLoadAd.HAS_BEEN_OPENED || stateAds3 == StateLoadAd.HAS_BEEN_OPENED)return

                    if (stateAds1 == StateLoadAd.SUCCESS){
                        //show 1
                        stateAds1 = StateLoadAd.HAS_BEEN_OPENED
                        handler.removeCallbacks(timeOutRunnable)
                        showLoadedRewardInter(
                            spaceNameConfig = spaceNameConfig,
                            spaceName = spaceName1,
                            isPreloadAfterShow = isPreloadAfterShow,
                            destinationToShowAds = destinationToShowAds,
                            isShowLoadingView = false,
                            onRewardDone = onRewardDone,
                            onGetReward = onGetReward)
                    }else if (stateAds2 == StateLoadAd.SUCCESS && stateAds1 == StateLoadAd.LOAD_FAILED){
                        //show 2
                        stateAds2 = StateLoadAd.HAS_BEEN_OPENED
                        handler.removeCallbacks(timeOutRunnable)
                        showLoadedRewardInter(
                            spaceNameConfig = spaceNameConfig,
                            spaceName = spaceName2,
                            isPreloadAfterShow = isPreloadAfterShow,
                            destinationToShowAds = destinationToShowAds,
                            isShowLoadingView = false,
                            onRewardDone = onRewardDone,
                            onGetReward = onGetReward)
                    }else if (stateAds3 == StateLoadAd.SUCCESS && stateAds1 == StateLoadAd.LOAD_FAILED && stateAds2 == StateLoadAd.LOAD_FAILED){
                        //show 3
                        stateAds3 = StateLoadAd.HAS_BEEN_OPENED
                        handler.removeCallbacks(timeOutRunnable)
                        showLoadedRewardInter(
                            spaceNameConfig = spaceNameConfig,
                            spaceName = spaceName3,
                            isPreloadAfterShow = isPreloadAfterShow,
                            destinationToShowAds = destinationToShowAds,
                            isShowLoadingView = false,
                            onRewardDone = onRewardDone,
                            onGetReward = onGetReward)
                    }else if (stateAds1 == StateLoadAd.LOAD_FAILED && stateAds2 == StateLoadAd.LOAD_FAILED && stateAds3 == StateLoadAd.LOAD_FAILED){
                        //end luon
                        handler.removeCallbacks(timeOutRunnable)
                        AdsController.isInterIsShowing = false
                    }
                }

                safePreloadAds(spaceNameConfig = spaceNameConfig , spaceNameAds = spaceName1 , preloadCallback = object :
                    PreloadCallback {
                    override fun onLoadDone() {
                        stateAds1 = StateLoadAd.SUCCESS
                        checkShowAds()
                    }
                    override fun onLoadFail(error: String) {
                        stateAds1 = StateLoadAd.LOAD_FAILED
                        checkShowAds()
                    }
                })
                safePreloadAds(spaceNameConfig = spaceNameConfig , spaceNameAds = spaceName2 , preloadCallback = object :
                    PreloadCallback {
                    override fun onLoadDone() {
                        stateAds2 = StateLoadAd.SUCCESS
                        checkShowAds()
                    }
                    override fun onLoadFail(error: String) {
                        stateAds2 = StateLoadAd.LOAD_FAILED
                        checkShowAds()
                    }
                })
                safePreloadAds(spaceNameConfig = spaceNameConfig , spaceNameAds = spaceName3 , preloadCallback = object :
                    PreloadCallback {
                    override fun onLoadDone() {
                        stateAds3 = StateLoadAd.SUCCESS
                        checkShowAds()
                    }
                    override fun onLoadFail(error: String) {
                        stateAds3 = StateLoadAd.LOAD_FAILED
                        checkShowAds()
                    }
                })
            }
        }

        if (isShowLoadingView) {
            DialogLoadAdsUtils.getInstance().showDialogLoadingAds(activity, isScreenType)
            countDownTimer.start()
        } else {
            countDownTimer.onFinish()
        }


    }
}