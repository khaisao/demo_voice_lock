package pion.datlt.libads.utils.adsuntils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import pion.datlt.libads.AdsController
import pion.datlt.libads.callback.AdCallback
import pion.datlt.libads.callback.PreloadCallback
import pion.datlt.libads.utils.AdsConstant
import pion.datlt.libads.utils.DialogLoadAdsUtils
import pion.datlt.libads.utils.StateLoadAd

fun Fragment.loadAndShowInterstitial(
    spaceNameConfig: String,
    spaceName: String,
    timeOut: Long = 7000L,
    destinationToShowAds: Int? = null,
    isScreenType: Boolean = true,
    navOrBack: () -> Unit,
    onCloseAds: (() -> Unit)? = null
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

        AdsController.getInstance().loadAndShow(
            spaceName = spaceName,
            destinationToShowAds = destinationToShowAds,
            lifecycle = lifecycle,
            timeout = timeOut,
            adCallback = object : AdCallback {
                override fun onAdShow() {
                    Log.d("CHECKNATIVEAFTERINTER", "showLoadedInter onAdShow $spaceNameConfig $spaceName : ${AdsConstant.listConfigAds[spaceNameConfig]?.isShowNativeAfterInter == true}")

                    if (AdsConstant.listConfigAds[spaceNameConfig]?.isShowNativeAfterInter == true){
                        Log.d("CHECKNATIVEAFTERINTER", "showNativeTrigger == null ${AdsController.getInstance().showNativeTrigger == null}")
                        AdsController.getInstance().showNativeTrigger?.invoke()
                    }



                    DialogLoadAdsUtils.getInstance().hideDialogLoadingAds()
                    AdsController.isInterIsShowing = true
                    setLastTimeShowInter(spaceNameConfig)
                    navOrBack.invoke()
                }

                override fun onAdClose() {
                    AdsController.isInterIsShowing = false
                    AdsController.isBlockOpenAds = fragmentEvent == Lifecycle.Event.ON_STOP
                    AdsController.getInstance().activity.application.unregisterActivityLifecycleCallbacks(
                        callback
                    )
                    lifecycle.removeObserver(lifecycleObserver)
                    setLastTimeShowInter(spaceNameConfig)
                    onCloseAds?.invoke()
                }

                override fun onAdFailToLoad(messageError: String?) {
                    DialogLoadAdsUtils.getInstance().hideDialogLoadingAds(0)
                    AdsController.isInterIsShowing = false
                    AdsController.isBlockOpenAds = fragmentEvent == Lifecycle.Event.ON_STOP
                    AdsController.getInstance().activity.application.unregisterActivityLifecycleCallbacks(
                        callback
                    )
                    lifecycle.removeObserver(lifecycleObserver)
                    navOrBack.invoke()
                    onCloseAds?.invoke()
                }

                override fun onAdClick() {
                    AdsController.isBlockOpenAds = true
                }
            })
    } else {
        navOrBack.invoke()
        onCloseAds?.invoke()
    }
}


fun Fragment.showLoadedInter(
    spaceNameConfig: String,
    spaceName: String,
    timeOut: Long = 7000L,
    isPreloadAfterShow: Boolean = false,
    destinationToShowAds: Int? = null,
    isShowLoadingView: Boolean = false,
    timeShowLoadingView: Long = 1000L,
    isScreenType: Boolean = true,
    navOrBack: () -> Unit,
    onCloseAds: (() -> Unit)? = null
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
                AdsController.getInstance().showLoadedAds(
                    spaceName = spaceName,
                    destinationToShowAds = destinationToShowAds,
                    lifecycle = lifecycle,
                    timeout = timeOut,
                    adCallback = object : AdCallback {

                        override fun onAdShow() {
                            Log.d("CHECKNATIVEAFTERINTER", "showLoadedInter onAdShow $spaceNameConfig $spaceName : ${AdsConstant.listConfigAds[spaceNameConfig]?.isShowNativeAfterInter == true}")

                            if (AdsConstant.listConfigAds[spaceNameConfig]?.isShowNativeAfterInter == true){
                                Log.d("CHECKNATIVEAFTERINTER", "showNativeTrigger == null ${AdsController.getInstance().showNativeTrigger == null}")
                                AdsController.getInstance().showNativeTrigger?.invoke()
                            }


                            DialogLoadAdsUtils.getInstance().hideDialogLoadingAds()
                            AdsController.isInterIsShowing = true
                            setLastTimeShowInter(spaceNameConfig)
                            navOrBack.invoke()
                        }

                        override fun onAdClose() {
                            AdsController.isInterIsShowing = false
                            AdsController.isBlockOpenAds = fragmentEvent == Lifecycle.Event.ON_STOP
                            AdsController.getInstance().activity.application.unregisterActivityLifecycleCallbacks(
                                callback
                            )
                            lifecycle.removeObserver(lifecycleObserver)
                            setLastTimeShowInter(spaceNameConfig)
                            if (isPreloadAfterShow) {
                                safePreloadAds(
                                    spaceNameAds = spaceName,
                                    spaceNameConfig = spaceNameConfig
                                )
                            }
                            onCloseAds?.invoke()
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
                            navOrBack.invoke()
                            onCloseAds?.invoke()
                        }

                        override fun onAdClick() {
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
        navOrBack.invoke()
        onCloseAds?.invoke()
    }
}

fun Fragment.showSplashInter(
    spaceNameConfig: String,
    spaceNameInter1: String,
    spaceNameInter2: String,
    spaceNameOpenAds: String,
    timeOut: Long = 15000L,
    destinationToShowAds: Int? = null,
    navOrBack: () -> Unit
) {
    if (checkConditionShowAds(context, spaceNameConfig)) {


        var stateAds1 = StateLoadAd.LOADING
        var stateAds2 = StateLoadAd.LOADING
        var stateAds3 = StateLoadAd.LOADING


        var isTimeOut = false
        val timeOutRunnable = Runnable {
            isTimeOut = true
            if (stateAds1 == StateLoadAd.SUCCESS) {
                //show 1
                showLoadedInter(
                    spaceNameConfig = spaceNameConfig,
                    spaceName = spaceNameInter1,
                    timeOut = 7000L,
                    destinationToShowAds = destinationToShowAds,
                    navOrBack = navOrBack
                )
            } else if (stateAds2 == StateLoadAd.SUCCESS) {
                //show 2
                showLoadedInter(
                    spaceNameConfig = spaceNameConfig,
                    spaceName = spaceNameInter2,
                    timeOut = 7000L,
                    destinationToShowAds = destinationToShowAds,
                    navOrBack = navOrBack
                )
            } else if (stateAds3 == StateLoadAd.SUCCESS) {
                //show 3
                showLoadedOpenApp(
                    spaceNameConfig = spaceNameConfig,
                    spaceName = spaceNameOpenAds,
                    destinationToShowAds = destinationToShowAds,
                    timeOut = 7000L,
                    navOrBack = navOrBack
                )
            } else {
                //nav luon
                navOrBack.invoke()
            }
        }
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(timeOutRunnable, timeOut)

        fun checkShowAds() {
            if (isTimeOut) return
            if (stateAds1 == StateLoadAd.SUCCESS) {
                stateAds1 = StateLoadAd.HAS_BEEN_OPENED
                handler.removeCallbacks(timeOutRunnable)
                //show 1
                showLoadedInter(
                    spaceNameConfig = spaceNameConfig,
                    spaceName = spaceNameInter1,
                    timeOut = 7000L,
                    destinationToShowAds = destinationToShowAds,
                    navOrBack = navOrBack
                )
            } else if (stateAds1 == StateLoadAd.LOAD_FAILED && stateAds2 == StateLoadAd.SUCCESS) {
                stateAds2 = StateLoadAd.HAS_BEEN_OPENED
                handler.removeCallbacks(timeOutRunnable)
                //show 2
                showLoadedInter(
                    spaceNameConfig = spaceNameConfig,
                    spaceName = spaceNameInter2,
                    timeOut = 7000L,
                    destinationToShowAds = destinationToShowAds,
                    navOrBack = navOrBack
                )
            } else if (stateAds1 == StateLoadAd.LOAD_FAILED && stateAds2 == StateLoadAd.LOAD_FAILED && stateAds3 == StateLoadAd.SUCCESS) {
                stateAds3 = StateLoadAd.HAS_BEEN_OPENED
                handler.removeCallbacks(timeOutRunnable)
                //show 3
                showLoadedOpenApp(
                    spaceNameConfig = spaceNameConfig,
                    spaceName = spaceNameOpenAds,
                    destinationToShowAds = destinationToShowAds,
                    timeOut = 7000L,
                    navOrBack = navOrBack
                )
            } else if (stateAds1 == StateLoadAd.LOAD_FAILED && stateAds2 == StateLoadAd.LOAD_FAILED && stateAds3 == StateLoadAd.LOAD_FAILED) {
                //nav luon
                handler.removeCallbacks(timeOutRunnable)
                navOrBack.invoke()
            }
        }

        safePreloadAds(
            spaceNameConfig = spaceNameConfig,
            spaceNameAds = spaceNameInter1,
            preloadCallback = object : PreloadCallback {
                override fun onLoadDone() {
                    stateAds1 = StateLoadAd.SUCCESS
                    checkShowAds()
                }

                override fun onLoadFail(error: String) {
                    stateAds1 = StateLoadAd.LOAD_FAILED
                    checkShowAds()
                }
            })
        safePreloadAds(
            spaceNameConfig = spaceNameConfig,
            spaceNameAds = spaceNameInter2,
            preloadCallback = object : PreloadCallback {
                override fun onLoadDone() {
                    stateAds2 = StateLoadAd.SUCCESS
                    checkShowAds()
                }

                override fun onLoadFail(error: String) {
                    stateAds2 = StateLoadAd.LOAD_FAILED
                    checkShowAds()
                }
            })
        safePreloadAds(
            spaceNameConfig = spaceNameConfig,
            spaceNameAds = spaceNameOpenAds,
            preloadCallback = object : PreloadCallback {
                override fun onLoadDone() {
                    stateAds3 = StateLoadAd.SUCCESS
                    checkShowAds()
                }

                override fun onLoadFail(error: String) {
                    stateAds3 = StateLoadAd.LOAD_FAILED
                    checkShowAds()
                }
            })

    } else {
        navOrBack.invoke()
    }
}

fun Fragment.show3LoadedInter(
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
    navOrBack: () -> Unit,
    onCloseAds: (() -> Unit)? = null
) {
    if (checkConditionShowAds(context, spaceNameConfig)) {
        //show dialog load truoc
        AdsController.isInterIsShowing = true
        val countDownTimer = object : CountDownTimer(timeShowLoadingView, timeShowLoadingView) {
            override fun onTick(p0: Long) {
                //do nothing
            }

            override fun onFinish() {
                //load qc o day

                var stateInter1 = StateLoadAd.LOADING
                var stateInter2 = StateLoadAd.LOADING
                var stateInter3 = StateLoadAd.LOADING

                var isTimeOut = false
                val timeOutRunnable = Runnable {
                    isTimeOut = true
                    if (stateInter1 == StateLoadAd.SUCCESS){
                        //show 1
                        showLoadedInter(
                            spaceNameConfig = spaceNameConfig,
                            spaceName = spaceName1,
                            timeOut = timeOut,
                            isPreloadAfterShow = isPreloadAfterShow,
                            destinationToShowAds = destinationToShowAds,
                            navOrBack = navOrBack,
                            onCloseAds = onCloseAds)
                    }else if (stateInter2 == StateLoadAd.SUCCESS){
                        //show 2
                        showLoadedInter(
                            spaceNameConfig = spaceNameConfig,
                            spaceName = spaceName2,
                            timeOut = timeOut,
                            isPreloadAfterShow = isPreloadAfterShow,
                            destinationToShowAds = destinationToShowAds,
                            navOrBack = navOrBack,
                            onCloseAds = onCloseAds)
                    }else if (stateInter3 == StateLoadAd.SUCCESS){
                        //show 3
                        showLoadedInter(
                            spaceNameConfig = spaceNameConfig,
                            spaceName = spaceName3,
                            timeOut = timeOut,
                            isPreloadAfterShow = isPreloadAfterShow,
                            destinationToShowAds = destinationToShowAds,
                            navOrBack = navOrBack,
                            onCloseAds = onCloseAds)
                    }else{
                        //ket thuc luong
                        DialogLoadAdsUtils.getInstance().hideDialogLoadingAds(0)
                        AdsController.isInterIsShowing = false
                        navOrBack.invoke()
                        onCloseAds?.invoke()
                    }
                }

                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed(timeOutRunnable , timeOut)

                fun checkShowInter(){
                    if (!isTimeOut){
                        if (stateInter1 == StateLoadAd.SUCCESS){
                            //show 1
                            handler.removeCallbacks(timeOutRunnable)
                            stateInter1 = StateLoadAd.HAS_BEEN_OPENED
                            showLoadedInter(
                                spaceNameConfig = spaceNameConfig,
                                spaceName = spaceName1,
                                timeOut = timeOut,
                                isPreloadAfterShow = isPreloadAfterShow,
                                destinationToShowAds = destinationToShowAds,
                                navOrBack = navOrBack,
                                onCloseAds = onCloseAds)
                        }else if (stateInter1 == StateLoadAd.LOAD_FAILED && stateInter2 == StateLoadAd.SUCCESS){
                            //show 2
                            handler.removeCallbacks(timeOutRunnable)
                            stateInter2 = StateLoadAd.HAS_BEEN_OPENED
                            showLoadedInter(
                                spaceNameConfig = spaceNameConfig,
                                spaceName = spaceName2,
                                timeOut = timeOut,
                                isPreloadAfterShow = isPreloadAfterShow,
                                destinationToShowAds = destinationToShowAds,
                                navOrBack = navOrBack,
                                onCloseAds = onCloseAds)
                        }else if (stateInter1 == StateLoadAd.LOAD_FAILED && stateInter2 == StateLoadAd.LOAD_FAILED && stateInter3 == StateLoadAd.SUCCESS){
                            //show 3
                            handler.removeCallbacks(timeOutRunnable)
                            stateInter3 = StateLoadAd.HAS_BEEN_OPENED
                            showLoadedInter(
                                spaceNameConfig = spaceNameConfig,
                                spaceName = spaceName3,
                                timeOut = timeOut,
                                isPreloadAfterShow = isPreloadAfterShow,
                                destinationToShowAds = destinationToShowAds,
                                navOrBack = navOrBack,
                                onCloseAds = onCloseAds)
                        }else if (stateInter1 == StateLoadAd.LOAD_FAILED && stateInter2 == StateLoadAd.LOAD_FAILED && stateInter3 == StateLoadAd.LOAD_FAILED){
                            //ket thuc luong
                            DialogLoadAdsUtils.getInstance().hideDialogLoadingAds(0)
                            handler.removeCallbacks(timeOutRunnable)
                            AdsController.isInterIsShowing = false
                            navOrBack.invoke()
                            onCloseAds?.invoke()
                        }
                    }
                }

                safePreloadAds(spaceNameConfig = spaceNameConfig, spaceNameAds = spaceName1, preloadCallback = object : PreloadCallback{
                    override fun onLoadDone() {
                        stateInter1 = StateLoadAd.SUCCESS
                        checkShowInter()
                    }

                    override fun onLoadFail(error: String) {
                        stateInter1 = StateLoadAd.LOAD_FAILED
                        checkShowInter()
                    }

                })
                safePreloadAds(spaceNameConfig = spaceNameConfig, spaceNameAds = spaceName2, preloadCallback = object : PreloadCallback{
                    override fun onLoadDone() {
                        stateInter2 = StateLoadAd.SUCCESS
                        checkShowInter()
                    }

                    override fun onLoadFail(error: String) {
                        stateInter2 = StateLoadAd.LOAD_FAILED
                        checkShowInter()
                    }
                })
                safePreloadAds(spaceNameConfig = spaceNameConfig, spaceNameAds = spaceName3, preloadCallback = object : PreloadCallback{
                    override fun onLoadDone() {
                        stateInter3 = StateLoadAd.SUCCESS
                        checkShowInter()
                    }

                    override fun onLoadFail(error: String) {
                        stateInter3 = StateLoadAd.LOAD_FAILED
                        checkShowInter()
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
    }else{
        navOrBack.invoke()
        onCloseAds?.invoke()
    }
}

fun Fragment.loadAndShow3Inter(
    spaceNameConfig: String,
    spaceName1: String,
    spaceName2: String,
    spaceName3: String,
    timeOut: Long = 7000L,
    destinationToShowAds: Int? = null,
    isScreenType: Boolean = true,
    navOrBack: () -> Unit,
    onCloseAds: (() -> Unit)? = null
) {
    if (checkConditionShowAds(context, spaceNameConfig)) {
        //show dialog load truoc
        AdsController.isInterIsShowing = true
        DialogLoadAdsUtils.getInstance().showDialogLoadingAds(activity, isScreenType)


        //load qc o day

        var stateInter1 = StateLoadAd.LOADING
        var stateInter2 = StateLoadAd.LOADING
        var stateInter3 = StateLoadAd.LOADING

        var isTimeOut = false
        val timeOutRunnable = Runnable {
            isTimeOut = true
            if (stateInter1 == StateLoadAd.SUCCESS){
                //show 1
                showLoadedInter(
                    spaceNameConfig = spaceNameConfig,
                    spaceName = spaceName1,
                    timeOut = timeOut,
                    destinationToShowAds = destinationToShowAds,
                    navOrBack = navOrBack,
                    onCloseAds = onCloseAds)
            }else if (stateInter2 == StateLoadAd.SUCCESS){
                //show 2
                showLoadedInter(
                    spaceNameConfig = spaceNameConfig,
                    spaceName = spaceName2,
                    timeOut = timeOut,
                    destinationToShowAds = destinationToShowAds,
                    navOrBack = navOrBack,
                    onCloseAds = onCloseAds)
            }else if (stateInter3 == StateLoadAd.SUCCESS){
                //show 3
                showLoadedInter(
                    spaceNameConfig = spaceNameConfig,
                    spaceName = spaceName3,
                    timeOut = timeOut,
                    destinationToShowAds = destinationToShowAds,
                    navOrBack = navOrBack,
                    onCloseAds = onCloseAds)
            }else{
                //ket thuc luong
                DialogLoadAdsUtils.getInstance().hideDialogLoadingAds(0)
                AdsController.isInterIsShowing = false
                navOrBack.invoke()
                onCloseAds?.invoke()
            }
        }

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(timeOutRunnable , timeOut)

        fun checkShowInter(){
            if (!isTimeOut){
                if (stateInter1 == StateLoadAd.SUCCESS){
                    //show 1
                    handler.removeCallbacks(timeOutRunnable)
                    stateInter1 = StateLoadAd.HAS_BEEN_OPENED
                    showLoadedInter(
                        spaceNameConfig = spaceNameConfig,
                        spaceName = spaceName1,
                        timeOut = timeOut,
                        destinationToShowAds = destinationToShowAds,
                        navOrBack = navOrBack,
                        onCloseAds = onCloseAds)
                }else if (stateInter1 == StateLoadAd.LOAD_FAILED && stateInter2 == StateLoadAd.SUCCESS){
                    //show 2
                    handler.removeCallbacks(timeOutRunnable)
                    stateInter2 = StateLoadAd.HAS_BEEN_OPENED
                    showLoadedInter(
                        spaceNameConfig = spaceNameConfig,
                        spaceName = spaceName2,
                        timeOut = timeOut,
                        destinationToShowAds = destinationToShowAds,
                        navOrBack = navOrBack,
                        onCloseAds = onCloseAds)
                }else if (stateInter1 == StateLoadAd.LOAD_FAILED && stateInter2 == StateLoadAd.LOAD_FAILED && stateInter3 == StateLoadAd.SUCCESS){
                    //show 3
                    handler.removeCallbacks(timeOutRunnable)
                    stateInter3 = StateLoadAd.HAS_BEEN_OPENED
                    showLoadedInter(
                        spaceNameConfig = spaceNameConfig,
                        spaceName = spaceName3,
                        timeOut = timeOut,
                        destinationToShowAds = destinationToShowAds,
                        navOrBack = navOrBack,
                        onCloseAds = onCloseAds)
                }else if (stateInter1 == StateLoadAd.LOAD_FAILED && stateInter2 == StateLoadAd.LOAD_FAILED && stateInter3 == StateLoadAd.LOAD_FAILED){
                    //ket thuc luong
                    DialogLoadAdsUtils.getInstance().hideDialogLoadingAds(0)
                    handler.removeCallbacks(timeOutRunnable)
                    AdsController.isInterIsShowing = false
                    navOrBack.invoke()
                    onCloseAds?.invoke()
                }
            }
        }

        safePreloadAds(spaceNameConfig = spaceNameConfig, spaceNameAds = spaceName1, preloadCallback = object : PreloadCallback{
            override fun onLoadDone() {
                stateInter1 = StateLoadAd.SUCCESS
                checkShowInter()
            }

            override fun onLoadFail(error: String) {
                stateInter1 = StateLoadAd.LOAD_FAILED
                checkShowInter()
            }

        })
        safePreloadAds(spaceNameConfig = spaceNameConfig, spaceNameAds = spaceName2, preloadCallback = object : PreloadCallback{
            override fun onLoadDone() {
                stateInter2 = StateLoadAd.SUCCESS
                checkShowInter()
            }

            override fun onLoadFail(error: String) {
                stateInter2 = StateLoadAd.LOAD_FAILED
                checkShowInter()
            }
        })
        safePreloadAds(spaceNameConfig = spaceNameConfig, spaceNameAds = spaceName3, preloadCallback = object : PreloadCallback{
            override fun onLoadDone() {
                stateInter3 = StateLoadAd.SUCCESS
                checkShowInter()
            }

            override fun onLoadFail(error: String) {
                stateInter3 = StateLoadAd.LOAD_FAILED
                checkShowInter()
            }
        })


    }else{
        navOrBack.invoke()
        onCloseAds?.invoke()
    }
}