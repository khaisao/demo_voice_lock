package com.piontech.core.lifecycleCallback

import android.app.Activity
import android.app.Application
import android.os.Bundle
import timber.log.Timber

class ActivityLifecycleCallbacksImpl : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        Timber.d("${activity::class.java.simpleName} onActivityCreated")
    }

    override fun onActivityStarted(activity: Activity) {
        Timber.d("${activity::class.java.simpleName} onActivityStarted")
    }

    override fun onActivityResumed(activity: Activity) {
        Timber.d("${activity::class.java.simpleName} onActivityResumed")
    }

    override fun onActivityPaused(activity: Activity) {
        Timber.d("${activity::class.java.simpleName} onActivityPaused")
    }

    override fun onActivityStopped(activity: Activity) {
        Timber.d("${activity::class.java.simpleName} onActivityStopped")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        Timber.d("${activity::class.java.simpleName} onActivitySaveInstanceState")
    }

    override fun onActivityDestroyed(activity: Activity) {
        Timber.d("${activity::class.java.simpleName} onActivityDestroyed")
    }
}