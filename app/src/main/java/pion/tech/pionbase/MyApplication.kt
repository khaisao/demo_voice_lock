package pion.tech.pionbase

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.k2fsa.sherpa.onnx.SpeakerRecognition
import com.khaipv.recovery.core.Recovery
import dagger.hilt.android.HiltAndroidApp
import pion.tech.pionbase.main.presentation.MainActivity
import com.piontech.core.lifecycleCallback.ActivityLifecycleCallbacksImpl
import pion.tech.pionbase.register.SpeakerManager
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {
    @Inject
    lateinit var speakerManager: SpeakerManager
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        if (BuildConfig.DEBUG) {
            Recovery.getInstance()
                .debug(true)
                .recoverInBackground(false)
                .recoverStack(true)
                .mainPage(MainActivity::class.java)
                .recoverEnabled(true)
                .silent(false, Recovery.SilentMode.RECOVER_ACTIVITY_STACK)
                .init(this)

            Timber.Forest.plant(Timber.DebugTree())
        }
        speakerManager.initialize()
        registerActivityLifecycleCallbacks(ActivityLifecycleCallbacksImpl())
    }

}