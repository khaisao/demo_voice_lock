package pion.datlt.libads.utils.adsuntils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import pion.datlt.libads.AdsController
import pion.datlt.libads.callback.AdCallback

fun Fragment.loadAndShowOpenApp(
    spaceNameConfig: String,
    spaceName: String,
    destinationToShowAds: Int? = null,
    timeOut: Long = 7000L,
    navOrBack: () -> Unit
) {
    if (checkConditionShowAds(context, spaceNameConfig)) {
        AdsController.isOtherOpenAdsIsShowing = true
        var fragmentEvent = Lifecycle.Event.ON_ANY
        val lifecycleObserver = LifecycleEventObserver { source, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                fragmentEvent = event
            }
        }
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
        AdsController.getInstance().activity.application.registerActivityLifecycleCallbacks(callback)//lắng nghe Lifecycle của activity nếu show qc mà app phải nhảy vào onstop thì sẽ chuyển màn khi qc được đóng
        lifecycle.addObserver(lifecycleObserver)//lắng nghe Lifecycle của fragment nếu show qc mà app phải nhảy vào onstop thì sẽ chuyển màn khi qc được đóng

        AdsController.getInstance().loadAndShow(
            spaceName = spaceName,
            destinationToShowAds = destinationToShowAds,
            lifecycle = lifecycle,
            timeout = timeOut,
            adCallback = object : AdCallback {
                override fun onAdShow() {
                    AdsController.isOtherOpenAdsIsShowing = true
                    //do nothing
                }

                override fun onAdClose() {
                    AdsController.isBlockOpenAds = fragmentEvent == Lifecycle.Event.ON_STOP
                    AdsController.getInstance().activity.application.unregisterActivityLifecycleCallbacks(
                        callback
                    )
                    lifecycle.removeObserver(lifecycleObserver)
                    AdsController.isOtherOpenAdsIsShowing = false
                    navOrBack.invoke()
                    setLastTimeShowInter(spaceNameConfig)
                }

                override fun onAdFailToLoad(messageError: String?) {
                    AdsController.isBlockOpenAds = fragmentEvent == Lifecycle.Event.ON_STOP
                    AdsController.getInstance().activity.application.unregisterActivityLifecycleCallbacks(
                        callback
                    )
                    lifecycle.removeObserver(lifecycleObserver)
                    AdsController.isOtherOpenAdsIsShowing = false
                    navOrBack.invoke()
                }

                override fun onAdClick() {
                    AdsController.isBlockOpenAds = true
                }
            }
        )
    } else {
        navOrBack.invoke()
    }
}

fun Fragment.showLoadedOpenApp(
    spaceNameConfig: String,
    spaceName: String,
    destinationToShowAds: Int? = null,
    timeOut: Long = 7000L,
    navOrBack: () -> Unit
) {
    if (checkConditionShowAds(context, spaceNameConfig)) {
        AdsController.isOtherOpenAdsIsShowing = true
        var fragmentEvent = Lifecycle.Event.ON_ANY
        val lifecycleObserver = LifecycleEventObserver { source, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                fragmentEvent = event
            }
        }
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
        AdsController.getInstance().activity.application.registerActivityLifecycleCallbacks(callback)//lắng nghe Lifecycle của activity nếu show qc mà app phải nhảy vào onstop thì sẽ chuyển màn khi qc được đóng. Dùng cho trường hợp show qc pangle không chuyển màn
        lifecycle.addObserver(lifecycleObserver)//lắng nghe Lifecycle của fragment nếu show qc mà app phải nhảy vào onstop thì sẽ chuyển màn khi qc được đóng. Dùng cho trường hợp show qc pangle không chuyển màn


        AdsController.getInstance().showLoadedAds(
            spaceName = spaceName,
            destinationToShowAds = destinationToShowAds,
            lifecycle = lifecycle,
            timeout = timeOut,
            adCallback = object : AdCallback {
                override fun onAdShow() {
                    AdsController.isOtherOpenAdsIsShowing = true
                }

                override fun onAdClose() {
                    AdsController.isBlockOpenAds = fragmentEvent == Lifecycle.Event.ON_STOP
                    AdsController.getInstance().activity.application.unregisterActivityLifecycleCallbacks(
                        callback
                    )
                    lifecycle.removeObserver(lifecycleObserver)
                    AdsController.isOtherOpenAdsIsShowing = false
                    navOrBack.invoke()
                    setLastTimeShowInter(spaceNameConfig)
                }

                override fun onAdFailToLoad(messageError: String?) {
                    AdsController.isBlockOpenAds = fragmentEvent == Lifecycle.Event.ON_STOP
                    AdsController.getInstance().activity.application.unregisterActivityLifecycleCallbacks(
                        callback
                    )
                    lifecycle.removeObserver(lifecycleObserver)
                    AdsController.isOtherOpenAdsIsShowing = false
                    navOrBack.invoke()
                }

                override fun onAdClick() {
                    AdsController.isBlockOpenAds = true
                }

            }
        )
    } else {
        navOrBack.invoke()
    }
}