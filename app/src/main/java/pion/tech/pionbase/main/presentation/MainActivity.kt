package pion.tech.pionbase.main.presentation

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.k2fsa.sherpa.onnx.SpeakerRecognition
import dagger.hilt.android.AndroidEntryPoint
import pion.datlt.libads.AdsController
import pion.tech.pionbase.BuildConfig
import pion.tech.pionbase.R
import com.piontech.core.lifecycleCallback.FragmentLifecycleCallbacksImpl

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportFragmentManager.registerFragmentLifecycleCallbacks(
            FragmentLifecycleCallbacksImpl(),
            true
        )
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initAds()
        SpeakerRecognition.initExtractor(this.assets)
    }

    private fun initAds() {
        AdsController.Companion.init(
            activity = this,
            isDebug = BuildConfig.DEBUG,
            listAppId = arrayListOf(
                getString(R.string.admob_application_id)
            ),
            packageName = packageName,
            navController = getNavHost()
        )
    }

    fun initAppResumeAds() {
        AdsController.Companion.getInstance().initResumeAds(
            lifecycle = lifecycle,
            listSpaceName = listOf("appresume_openad1", "appresume_openad2", "appresume_openad3"),
            onShowOpenApp = {
                findViewById<TextView>(R.id.viewShowOpenApp).isVisible = true
            },
            onStartToShowOpenAds = {
                findViewById<TextView>(R.id.viewShowOpenApp).isVisible = true
            },
            onCloseOpenApp = {
                findViewById<TextView>(R.id.viewShowOpenApp).isVisible = false
            },
            onPaidEvent = {
                //do nothing
            })
    }


    private fun getNavHost(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerMain) as NavHostFragment
        return navHostFragment.navController
    }
}