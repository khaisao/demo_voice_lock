package pion.datlt.libads.utils.adsuntils

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pion.datlt.libads.AdsController
import pion.datlt.libads.R
import pion.datlt.libads.admob.ads.AdmobNativeAds
import pion.datlt.libads.callback.AdCallback
import pion.datlt.libads.callback.PreloadCallback
import pion.datlt.libads.model.ConfigNative
import pion.datlt.libads.utils.AdsConstant
import pion.datlt.libads.utils.StateLoadAd

fun Fragment.showLoadedNative(
    spaceNameConfig: String,
    spaceName: String,
    includeHasBeenOpened: Boolean = false,
    adChoice: Int? = null,
    isResetConfig: Boolean = true,
    layoutToAttachAds: ViewGroup,
    layoutContainAds: ViewGroup? = null,
    viewAdsInflateFromXml: View? = null,
    onAdsClick: (() -> Unit)? = null
) {
    if (checkConditionShowAds(context, spaceNameConfig)) {

        //lay data ve view
        var newAdChoice = adChoice
        var newViewAdsInflateFromXml = viewAdsInflateFromXml

        AdsConstant.listConfigAds[spaceNameConfig]?.let { config ->
            if (isResetConfig) {
                config.getConfigNative(
                    context = context,
                    default = ConfigNative(
                        adChoice = AdsConstant.TOP_LEFT,
                        viewAds = LayoutInflater.from(context)
                            .inflate(R.layout.layout_native_small_icon_ctaright, null)
                    )
                ).let { configNative ->

                    if (newAdChoice == null) {
                        newAdChoice = configNative.adChoice
                    }

                    if (newViewAdsInflateFromXml == null) {
                        newViewAdsInflateFromXml = configNative.viewAds
                    }
                }

                //set cta shape
                //set cta color
                //set cta radius
                //set cta ratio
                runCatching {
                    if (newViewAdsInflateFromXml != null) {
                        val listGradientColor = mutableListOf<Int>()
                        config.ctaGradientListColor?.forEach { colorString ->
                            kotlin.runCatching {
                                listGradientColor.add(Color.parseColor(colorString))
                            }
                        }

                        if (listGradientColor.isEmpty()) {
                            if (context != null) {
                                listGradientColor.add(context!!.getColor(R.color.cta_color))
                                listGradientColor.add(context!!.getColor(R.color.cta_color))
                            } else {
                                listGradientColor.add(Color.parseColor("#3ADB41"))
                                listGradientColor.add(Color.parseColor("#3ADB41"))
                            }
                        }else if (listGradientColor.size == 1){
                            listGradientColor.add(listGradientColor[0])
                        }

                        val gradientDrawable = GradientDrawable(
                            GradientDrawable.Orientation.LEFT_RIGHT,
                            listGradientColor.toIntArray()
                        ).apply {
                            shape = GradientDrawable.RECTANGLE
                            cornerRadius = TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP,
                                config.ctaConnerRadius.toFloat(),
                                Resources.getSystem().displayMetrics
                            )
                        }
                        val ctaButton =
                            newViewAdsInflateFromXml!!.findViewById<TextView>(R.id.ad_call_to_action)
                        ctaButton.backgroundTintList = null
                        ctaButton.background = gradientDrawable
                        ctaButton.setTextColor(Color.parseColor(config.textCTAColor))
                        if (config.ctaRatio != null) {
                            val ctaButtonParams =
                                ctaButton?.layoutParams as ConstraintLayout.LayoutParams?
                            ctaButtonParams?.dimensionRatio = config.ctaRatio
                            ctaButton?.layoutParams = ctaButtonParams
                        }
                    }
                }

                //set background color
                runCatching {
                    if (newViewAdsInflateFromXml != null) {
                        val adViewHolder =
                            newViewAdsInflateFromXml!!.findViewById<ConstraintLayout>(R.id.adViewHolder)
                        adViewHolder.setBackgroundColor(Color.parseColor(config.backGroundColor))
                    }
                }

                //set content text color
                runCatching {
                    if (newViewAdsInflateFromXml != null) {
                        val headLineText =
                            newViewAdsInflateFromXml!!.findViewById<TextView>(R.id.ad_headline)
                        val bodyText =
                            newViewAdsInflateFromXml!!.findViewById<TextView>(R.id.ad_body)
                        headLineText.setTextColor(Color.parseColor(config.textContentColor))
                        bodyText.setTextColor(Color.parseColor(config.textContentColor))
                    }

                }

                //set ratio view
                runCatching {
                    val layoutContainAdsParams =
                        layoutContainAds?.layoutParams as ConstraintLayout.LayoutParams?
                    layoutContainAdsParams?.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
                    layoutContainAds?.layoutParams = layoutContainAdsParams
                    newViewAdsInflateFromXml?.visibility = View.INVISIBLE
                    layoutToAttachAds.addView(newViewAdsInflateFromXml)
                }

            }
        }

        AdsController.getInstance().showLoadedAds(
            spaceName = spaceName,
            includeHasBeenOpened = includeHasBeenOpened,
            layoutToAttachAds = layoutToAttachAds,
            viewAdsInflateFromXml = newViewAdsInflateFromXml,
            adChoice = newAdChoice,
            timeShowNativeCollapsibleAfterClose = AdsConstant.listConfigAds[spaceNameConfig]?.timeShowNativeCollapsibleAfterClose,
            adCallback = object : AdCallback {
                override fun onAdShow() {
                    if (checkIsPreloadAfterShow(spaceNameConfig = spaceNameConfig)) {
                        safePreloadAds(
                            spaceNameConfig = spaceNameConfig,
                            spaceNameAds = spaceName,
                            includeHasBeenOpened = includeHasBeenOpened,
                            adChoice = adChoice
                        )
                    }
                }

                override fun onAdClose() {}

                override fun onAdFailToLoad(messageError: String?) {}

                override fun onAdOff() {}

                override fun onAdClick() {
                    AdsController.isBlockOpenAds = true
                    if (AdsConstant.listConfigAds[spaceNameConfig]?.isCloseWhenClick == true) {
                        AdmobNativeAds.isCollapsed = true
                        layoutToAttachAds.removeAllViews()
                        layoutToAttachAds.visibility = View.GONE
//                        layoutContainAds?.visibility = View.GONE
                    }
                    onAdsClick?.invoke()
                }

                override fun onClickCloseCollapsible() {
                    if (AdsConstant.listConfigAds[spaceNameConfig]?.isCloseWhenClickCollapsible == true) {
                        layoutToAttachAds.visibility = View.GONE
                    }
                }
            }
        )

    } else {
        layoutToAttachAds.visibility = View.GONE
        layoutContainAds?.visibility = View.GONE
    }

}

fun Context.showLoadedNative(
    spaceNameConfig: String,
    spaceName: String,
    includeHasBeenOpened: Boolean = false,
    adChoice: Int? = null,
    isResetConfig: Boolean = true,
    layoutToAttachAds: ViewGroup,
    layoutContainAds: ViewGroup? = null,
    viewAdsInflateFromXml: View? = null,
    onAdsClick: (() -> Unit)? = null
) {
    val context = this
    if (checkConditionShowAds(context, spaceNameConfig)) {

        //lay data ve view
        var newAdChoice = adChoice
        var newViewAdsInflateFromXml = viewAdsInflateFromXml

        AdsConstant.listConfigAds[spaceNameConfig]?.let { config ->
            if (isResetConfig) {
                config.getConfigNative(
                    context = context,
                    default = ConfigNative(
                        adChoice = AdsConstant.TOP_LEFT,
                        viewAds = LayoutInflater.from(context)
                            .inflate(R.layout.layout_native_small_icon_ctaright, null)
                    )
                ).let { configNative ->

                    if (newAdChoice == null) {
                        newAdChoice = configNative.adChoice
                    }

                    if (newViewAdsInflateFromXml == null) {
                        newViewAdsInflateFromXml = configNative.viewAds
                    }
                }

                //set cta shape
                //set cta color
                //set cta radius
                //set cta ratio
                runCatching {
                    if (newViewAdsInflateFromXml != null) {
                        val listGradientColor = mutableListOf<Int>()
                        config.ctaGradientListColor?.forEach { colorString ->
                            kotlin.runCatching {
                                listGradientColor.add(Color.parseColor(colorString))
                            }
                        }

                        if (listGradientColor.isEmpty()) {
                            if (context != null) {
                                listGradientColor.add(context!!.getColor(R.color.cta_color))
                                listGradientColor.add(context!!.getColor(R.color.cta_color))
                            } else {
                                listGradientColor.add(Color.parseColor("#3ADB41"))
                                listGradientColor.add(Color.parseColor("#3ADB41"))
                            }
                        }else if (listGradientColor.size == 1){
                            listGradientColor.add(listGradientColor[0])
                        }

                        val gradientDrawable = GradientDrawable(
                            GradientDrawable.Orientation.LEFT_RIGHT,
                            listGradientColor.toIntArray()
                        ).apply {
                            shape = GradientDrawable.RECTANGLE
                            cornerRadius = TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP,
                                config.ctaConnerRadius.toFloat(),
                                Resources.getSystem().displayMetrics
                            )
                        }
                        val ctaButton =
                            newViewAdsInflateFromXml!!.findViewById<TextView>(R.id.ad_call_to_action)
                        ctaButton.backgroundTintList = null
                        ctaButton.background = gradientDrawable
                        ctaButton.setTextColor(Color.parseColor(config.textCTAColor))
                        if (config.ctaRatio != null) {
                            val ctaButtonParams =
                                ctaButton?.layoutParams as ConstraintLayout.LayoutParams?
                            ctaButtonParams?.dimensionRatio = config.ctaRatio
                            ctaButton?.layoutParams = ctaButtonParams
                        }
                    }
                }

                //set background color
                runCatching {
                    if (newViewAdsInflateFromXml != null) {
                        val adViewHolder =
                            newViewAdsInflateFromXml!!.findViewById<ConstraintLayout>(R.id.adViewHolder)
                        adViewHolder.setBackgroundColor(Color.parseColor(config.backGroundColor))
                    }
                }

                //set content text color
                runCatching {
                    if (newViewAdsInflateFromXml != null) {
                        val headLineText =
                            newViewAdsInflateFromXml!!.findViewById<TextView>(R.id.ad_headline)
                        val bodyText =
                            newViewAdsInflateFromXml!!.findViewById<TextView>(R.id.ad_body)
                        headLineText.setTextColor(Color.parseColor(config.textContentColor))
                        bodyText.setTextColor(Color.parseColor(config.textContentColor))
                    }

                }

                //set ratio view
                runCatching {
                    val layoutContainAdsParams =
                        layoutContainAds?.layoutParams as ConstraintLayout.LayoutParams?
                    layoutContainAdsParams?.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
                    layoutContainAds?.layoutParams = layoutContainAdsParams
                    newViewAdsInflateFromXml?.visibility = View.INVISIBLE
                    layoutToAttachAds.addView(newViewAdsInflateFromXml)
                }

            }
        }

        AdsController.getInstance().showLoadedAds(
            spaceName = spaceName,
            includeHasBeenOpened = includeHasBeenOpened,
            layoutToAttachAds = layoutToAttachAds,
            viewAdsInflateFromXml = newViewAdsInflateFromXml,
            adChoice = newAdChoice,
            timeShowNativeCollapsibleAfterClose = AdsConstant.listConfigAds[spaceNameConfig]?.timeShowNativeCollapsibleAfterClose,
            adCallback = object : AdCallback {
                override fun onAdShow() {
                    if (checkIsPreloadAfterShow(spaceNameConfig = spaceNameConfig)) {
                        safePreloadAds(
                            spaceNameConfig = spaceNameConfig,
                            spaceNameAds = spaceName,
                            includeHasBeenOpened = includeHasBeenOpened,
                            adChoice = adChoice
                        )
                    }
                }

                override fun onAdClose() {}

                override fun onAdFailToLoad(messageError: String?) {}

                override fun onAdOff() {}

                override fun onAdClick() {
                    AdsController.isBlockOpenAds = true
                    if (AdsConstant.listConfigAds[spaceNameConfig]?.isCloseWhenClick == true) {
                        AdmobNativeAds.isCollapsed = true
                        layoutToAttachAds.removeAllViews()
                        layoutToAttachAds.visibility = View.GONE
//                        layoutContainAds?.visibility = View.GONE
                    }
                    onAdsClick?.invoke()
                }

                override fun onClickCloseCollapsible() {
                    if (AdsConstant.listConfigAds[spaceNameConfig]?.isCloseWhenClickCollapsible == true) {
                        layoutToAttachAds.visibility = View.GONE
                    }
                }
            }
        )

    } else {
        layoutToAttachAds.visibility = View.GONE
        layoutContainAds?.visibility = View.GONE
    }

}

fun Fragment.loadAndShowNative(
    spaceNameConfig: String,
    spaceName: String,
    adChoice: Int? = null,
    isResetConfig: Boolean = true,
    layoutToAttachAds: ViewGroup,
    layoutContainAds: ViewGroup? = null,
    viewAdsInflateFromXml: View? = null,
    onAdsClick: (() -> Unit)? = null
) {
    if (checkConditionShowAds(context, spaceNameConfig)) {

        //lay data ve view
        var newAdChoice = adChoice
        var newViewAdsInflateFromXml = viewAdsInflateFromXml

        AdsConstant.listConfigAds[spaceNameConfig]?.let { config ->
            if (isResetConfig) {
                config.getConfigNative(
                    context = context,
                    default = ConfigNative(
                        adChoice = AdsConstant.TOP_LEFT,
                        viewAds = LayoutInflater.from(context)
                            .inflate(R.layout.layout_native_small_icon_ctaright, null)
                    )
                ).let { configNative ->

                    if (newAdChoice == null) {
                        newAdChoice = configNative.adChoice
                    }

                    if (newViewAdsInflateFromXml == null) {
                        newViewAdsInflateFromXml = configNative.viewAds
                    }
                }

                //set cta shape
                //set cta color
                //set cta radius
                //set cta ratio
                runCatching {
                    if (newViewAdsInflateFromXml != null) {
                        val listGradientColor = mutableListOf<Int>()
                        config.ctaGradientListColor?.forEach { colorString ->
                            kotlin.runCatching {
                                listGradientColor.add(Color.parseColor(colorString))
                            }
                        }

                        if (listGradientColor.isEmpty()) {
                            if (context != null) {
                                listGradientColor.add(context!!.getColor(R.color.cta_color))
                                listGradientColor.add(context!!.getColor(R.color.cta_color))
                            } else {
                                listGradientColor.add(Color.parseColor("#3ADB41"))
                                listGradientColor.add(Color.parseColor("#3ADB41"))
                            }
                        }else if (listGradientColor.size == 1){
                            listGradientColor.add(listGradientColor[0])
                        }

                        val gradientDrawable = GradientDrawable(
                            GradientDrawable.Orientation.LEFT_RIGHT,
                            listGradientColor.toIntArray()
                        ).apply {
                            shape = GradientDrawable.RECTANGLE
                            cornerRadius = TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP,
                                config.ctaConnerRadius.toFloat(),
                                Resources.getSystem().displayMetrics
                            )
                        }
                        val ctaButton =
                            newViewAdsInflateFromXml!!.findViewById<TextView>(R.id.ad_call_to_action)
                        ctaButton.backgroundTintList = null
                        ctaButton.background = gradientDrawable
                        ctaButton.setTextColor(Color.parseColor(config.textCTAColor))
                        if (config.ctaRatio != null) {
                            val ctaButtonParams =
                                ctaButton?.layoutParams as ConstraintLayout.LayoutParams?
                            ctaButtonParams?.dimensionRatio = config.ctaRatio
                            ctaButton?.layoutParams = ctaButtonParams
                        }
                    }
                }

                //set background color
                runCatching {
                    if (newViewAdsInflateFromXml != null) {
                        val adViewHolder =
                            newViewAdsInflateFromXml!!.findViewById<ConstraintLayout>(R.id.adViewHolder)
                        adViewHolder.setBackgroundColor(Color.parseColor(config.backGroundColor))
                    }
                }

                //set content text color
                runCatching {
                    if (newViewAdsInflateFromXml != null) {
                        val headLineText =
                            newViewAdsInflateFromXml!!.findViewById<TextView>(R.id.ad_headline)
                        val bodyText =
                            newViewAdsInflateFromXml!!.findViewById<TextView>(R.id.ad_body)
                        headLineText.setTextColor(Color.parseColor(config.textContentColor))
                        bodyText.setTextColor(Color.parseColor(config.textContentColor))
                    }

                }

                //set ratio view
                runCatching {
                    val layoutContainAdsParams =
                        layoutContainAds?.layoutParams as ConstraintLayout.LayoutParams?
                    layoutContainAdsParams?.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
                    layoutContainAds?.layoutParams = layoutContainAdsParams
                    newViewAdsInflateFromXml?.visibility = View.INVISIBLE
                    layoutToAttachAds.addView(newViewAdsInflateFromXml)
                }

            }
        }


        AdsController.getInstance().loadAndShow(
            spaceName = spaceName,
            layoutToAttachAds = layoutToAttachAds,
            viewAdsInflateFromXml = newViewAdsInflateFromXml,
            adChoice = newAdChoice,
            timeShowNativeCollapsibleAfterClose = AdsConstant.listConfigAds[spaceNameConfig]?.timeShowNativeCollapsibleAfterClose,
            adCallback = object : AdCallback {
                override fun onAdShow() {}

                override fun onAdClose() {}

                override fun onAdFailToLoad(messageError: String?) {}

                override fun onAdOff() {}

                override fun onAdClick() {
                    AdsController.isBlockOpenAds = true
                    if (AdsConstant.listConfigAds[spaceNameConfig]?.isCloseWhenClick == true) {
                        AdmobNativeAds.isCollapsed = true
                        layoutToAttachAds.removeAllViews()
                        layoutToAttachAds.visibility = View.GONE
//                        layoutContainAds?.visibility = View.GONE
                    }
                    onAdsClick?.invoke()
                }

                override fun onClickCloseCollapsible() {
                    if (AdsConstant.listConfigAds[spaceNameConfig]?.isCloseWhenClickCollapsible == true) {
                        layoutToAttachAds.visibility = View.GONE
                    }
                }
            }
        )

    } else {
        layoutToAttachAds.visibility = View.GONE
        layoutContainAds?.visibility = View.GONE
    }
}


fun Fragment.show3NativeUsePriority(
    spaceNameConfig: String,
    spaceName1: String,
    spaceName2: String,
    spaceName3: String,
    timeOut: Long = AdsConstant.timeDelayNative,
    includeHasBeenOpened: Boolean = false,
    adChoice: Int? = null,
    isResetConfig: Boolean = true,
    layoutToAttachAds: ViewGroup,
    layoutContainAds: ViewGroup? = null,
    viewAdsInflateFromXml: View? = null,
    onAdsClick: (() -> Unit)? = null
) {
    if (checkConditionShowAds(context, spaceNameConfig)) {

        //lay data ve view
        var newAdChoice = adChoice
        var newViewAdsInflateFromXml = viewAdsInflateFromXml

        AdsConstant.listConfigAds[spaceNameConfig]?.let { config ->
            if (isResetConfig) {
                config.getConfigNative(
                    context = context,
                    default = ConfigNative(
                        adChoice = AdsConstant.TOP_LEFT,
                        viewAds = LayoutInflater.from(context)
                            .inflate(R.layout.layout_native_small_icon_ctaright, null)
                    )
                ).let { configNative ->

                    if (newAdChoice == null) {
                        newAdChoice = configNative.adChoice
                    }

                    if (newViewAdsInflateFromXml == null) {
                        newViewAdsInflateFromXml = configNative.viewAds
                    }
                }

                //set cta shape
                //set cta color
                //set cta radius
                //set cta ratio
                runCatching {
                    if (newViewAdsInflateFromXml != null) {
                        val listGradientColor = mutableListOf<Int>()
                        config.ctaGradientListColor?.forEach { colorString ->
                            kotlin.runCatching {
                                listGradientColor.add(Color.parseColor(colorString))
                            }
                        }

                        if (listGradientColor.isEmpty()) {
                            if (context != null) {
                                listGradientColor.add(context!!.getColor(R.color.cta_color))
                                listGradientColor.add(context!!.getColor(R.color.cta_color))
                            } else {
                                listGradientColor.add(Color.parseColor("#3ADB41"))
                                listGradientColor.add(Color.parseColor("#3ADB41"))
                            }
                        }else if (listGradientColor.size == 1){
                            listGradientColor.add(listGradientColor[0])
                        }

                        val gradientDrawable = GradientDrawable(
                            GradientDrawable.Orientation.LEFT_RIGHT,
                            listGradientColor.toIntArray()
                        ).apply {
                            shape = GradientDrawable.RECTANGLE
                            cornerRadius = TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP,
                                config.ctaConnerRadius.toFloat(),
                                Resources.getSystem().displayMetrics
                            )
                        }
                        val ctaButton =
                            newViewAdsInflateFromXml!!.findViewById<TextView>(R.id.ad_call_to_action)
                        ctaButton.backgroundTintList = null
                        ctaButton.background = gradientDrawable
                        ctaButton.setTextColor(Color.parseColor(config.textCTAColor))
                        if (config.ctaRatio != null) {
                            val ctaButtonParams =
                                ctaButton?.layoutParams as ConstraintLayout.LayoutParams?
                            ctaButtonParams?.dimensionRatio = config.ctaRatio
                            ctaButton?.layoutParams = ctaButtonParams
                        }
                    }
                }

                //set background color
                runCatching {
                    if (newViewAdsInflateFromXml != null) {
                        val adViewHolder =
                            newViewAdsInflateFromXml!!.findViewById<ConstraintLayout>(R.id.adViewHolder)
                        adViewHolder.setBackgroundColor(Color.parseColor(config.backGroundColor))
                    }
                }

                //set content text color
                runCatching {
                    if (newViewAdsInflateFromXml != null) {
                        val headLineText =
                            newViewAdsInflateFromXml!!.findViewById<TextView>(R.id.ad_headline)
                        val bodyText =
                            newViewAdsInflateFromXml!!.findViewById<TextView>(R.id.ad_body)
                        headLineText.setTextColor(Color.parseColor(config.textContentColor))
                        bodyText.setTextColor(Color.parseColor(config.textContentColor))
                    }

                }

                //set ratio view
                runCatching {
                    val layoutContainAdsParams =
                        layoutContainAds?.layoutParams as ConstraintLayout.LayoutParams?
                    layoutContainAdsParams?.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
                    layoutContainAds?.layoutParams = layoutContainAdsParams
                    newViewAdsInflateFromXml?.visibility = View.INVISIBLE
                    layoutToAttachAds.addView(newViewAdsInflateFromXml)
                }

            }
        }

        var stateNative1 = StateLoadAd.LOADING
        var stateNative2 = StateLoadAd.LOADING
        var stateNative3 = StateLoadAd.LOADING

        var isTimeOut = false
        val timeOutRunnable = Runnable {
            isTimeOut = true
            if (stateNative1 == StateLoadAd.HAS_BEEN_OPENED || stateNative2 == StateLoadAd.HAS_BEEN_OPENED || stateNative3 == StateLoadAd.HAS_BEEN_OPENED) return@Runnable
            //show 3 native
            show3Native(
                spaceNameConfig = spaceNameConfig,
                spaceName1 = spaceName1,
                spaceName2 = spaceName2,
                spaceName3 = spaceName3,
                adChoice = newAdChoice,
                includeHasBeenOpened = includeHasBeenOpened,
                isResetConfig = false,
                layoutToAttachAds = layoutToAttachAds,
                layoutContainAds = layoutContainAds,
                viewAdsInflateFromXml = newViewAdsInflateFromXml,
                onAdsClick = onAdsClick
            )
        }

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(timeOutRunnable, timeOut)


        fun checkShowNative() {
            if (isTimeOut) return
            if (stateNative1 == StateLoadAd.SUCCESS) {
                //show 1
                handler.removeCallbacks(timeOutRunnable)
                stateNative1 = StateLoadAd.HAS_BEEN_OPENED
                showLoadedNative(
                    spaceNameConfig = spaceNameConfig,
                    spaceName = spaceName1,
                    adChoice = newAdChoice,
                    includeHasBeenOpened = includeHasBeenOpened,
                    isResetConfig = false,
                    layoutToAttachAds = layoutToAttachAds,
                    layoutContainAds = layoutContainAds,
                    viewAdsInflateFromXml = newViewAdsInflateFromXml,
                    onAdsClick = onAdsClick
                )
            } else if (stateNative1 == StateLoadAd.LOAD_FAILED && stateNative2 == StateLoadAd.SUCCESS) {
                //show 2
                handler.removeCallbacks(timeOutRunnable)
                stateNative2 = StateLoadAd.HAS_BEEN_OPENED
                showLoadedNative(
                    spaceNameConfig = spaceNameConfig,
                    spaceName = spaceName2,
                    adChoice = newAdChoice,
                    isResetConfig = false,
                    includeHasBeenOpened = includeHasBeenOpened,
                    layoutToAttachAds = layoutToAttachAds,
                    layoutContainAds = layoutContainAds,
                    viewAdsInflateFromXml = newViewAdsInflateFromXml,
                    onAdsClick = onAdsClick
                )
            } else if (stateNative1 == StateLoadAd.LOAD_FAILED && stateNative2 == StateLoadAd.LOAD_FAILED && stateNative3 == StateLoadAd.SUCCESS) {
                //show 3
                handler.removeCallbacks(timeOutRunnable)
                stateNative3 = StateLoadAd.HAS_BEEN_OPENED
                showLoadedNative(
                    spaceNameConfig = spaceNameConfig,
                    spaceName = spaceName3,
                    adChoice = newAdChoice,
                    isResetConfig = false,
                    includeHasBeenOpened = includeHasBeenOpened,
                    layoutToAttachAds = layoutToAttachAds,
                    layoutContainAds = layoutContainAds,
                    viewAdsInflateFromXml = newViewAdsInflateFromXml,
                    onAdsClick = onAdsClick
                )
            } else if (stateNative1 == StateLoadAd.LOAD_FAILED && stateNative2 == StateLoadAd.LOAD_FAILED && stateNative3 == StateLoadAd.LOAD_FAILED) {
                //ket thuc luong
                handler.removeCallbacks(timeOutRunnable)
            }
        }

        safePreloadAds(
            spaceNameConfig = spaceNameConfig,
            spaceNameAds = spaceName1,
            adChoice = newAdChoice,
            includeHasBeenOpened = includeHasBeenOpened,
            preloadCallback = object : PreloadCallback {
                override fun onLoadDone() {
                    stateNative1 = StateLoadAd.SUCCESS
                    checkShowNative()
                }

                override fun onLoadFail(error: String) {
                    stateNative1 = StateLoadAd.LOAD_FAILED
                    checkShowNative()
                }
            })
        safePreloadAds(
            spaceNameConfig = spaceNameConfig,
            spaceNameAds = spaceName2,
            adChoice = newAdChoice,
            includeHasBeenOpened = includeHasBeenOpened,
            preloadCallback = object : PreloadCallback {
                override fun onLoadDone() {
                    stateNative2 = StateLoadAd.SUCCESS
                    checkShowNative()
                }

                override fun onLoadFail(error: String) {
                    stateNative2 = StateLoadAd.LOAD_FAILED
                    checkShowNative()
                }
            })
        safePreloadAds(
            spaceNameConfig = spaceNameConfig,
            spaceNameAds = spaceName3,
            adChoice = newAdChoice,
            includeHasBeenOpened = includeHasBeenOpened,
            preloadCallback = object : PreloadCallback {
                override fun onLoadDone() {
                    stateNative3 = StateLoadAd.SUCCESS
                    checkShowNative()
                }

                override fun onLoadFail(error: String) {
                    stateNative3 = StateLoadAd.LOAD_FAILED
                    checkShowNative()
                }
            })


    } else {
        layoutToAttachAds.visibility = View.GONE
        layoutContainAds?.visibility = View.GONE
    }
}

fun Fragment.show3Native(
    spaceNameConfig: String,
    spaceName1: String,
    spaceName2: String,
    spaceName3: String,
    adChoice: Int? = null,
    includeHasBeenOpened: Boolean = false,
    isResetConfig: Boolean = true,
    layoutToAttachAds: ViewGroup,
    layoutContainAds: ViewGroup? = null,
    viewAdsInflateFromXml: View? = null,
    onAdsClick: (() -> Unit)? = null
) {
    if (checkConditionShowAds(context, spaceNameConfig)) {

        //lay data ve view
        var newAdChoice = adChoice
        var newViewAdsInflateFromXml = viewAdsInflateFromXml

        AdsConstant.listConfigAds[spaceNameConfig]?.let { config ->
            if (isResetConfig) {
                config.getConfigNative(
                    context = context,
                    default = ConfigNative(
                        adChoice = AdsConstant.TOP_LEFT,
                        viewAds = LayoutInflater.from(context)
                            .inflate(R.layout.layout_native_small_icon_ctaright, null)
                    )
                ).let { configNative ->

                    if (newAdChoice == null) {
                        newAdChoice = configNative.adChoice
                    }

                    if (newViewAdsInflateFromXml == null) {
                        newViewAdsInflateFromXml = configNative.viewAds
                    }
                }

                //set cta shape
                //set cta color
                //set cta radius
                //set cta ratio
                runCatching {
                    if (newViewAdsInflateFromXml != null) {
                        val listGradientColor = mutableListOf<Int>()
                        config.ctaGradientListColor?.forEach { colorString ->
                            kotlin.runCatching {
                                listGradientColor.add(Color.parseColor(colorString))
                            }
                        }

                        if (listGradientColor.isEmpty()) {
                            if (context != null) {
                                listGradientColor.add(context!!.getColor(R.color.cta_color))
                                listGradientColor.add(context!!.getColor(R.color.cta_color))
                            } else {
                                listGradientColor.add(Color.parseColor("#3ADB41"))
                                listGradientColor.add(Color.parseColor("#3ADB41"))
                            }
                        }else if (listGradientColor.size == 1){
                            listGradientColor.add(listGradientColor[0])
                        }

                        val gradientDrawable = GradientDrawable(
                            GradientDrawable.Orientation.LEFT_RIGHT,
                            listGradientColor.toIntArray()
                        ).apply {
                            shape = GradientDrawable.RECTANGLE
                            cornerRadius = TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP,
                                config.ctaConnerRadius.toFloat(),
                                Resources.getSystem().displayMetrics
                            )
                        }
                        val ctaButton =
                            newViewAdsInflateFromXml!!.findViewById<TextView>(R.id.ad_call_to_action)
                        ctaButton.backgroundTintList = null
                        ctaButton.background = gradientDrawable
                        ctaButton.setTextColor(Color.parseColor(config.textCTAColor))
                        if (config.ctaRatio != null) {
                            val ctaButtonParams =
                                ctaButton?.layoutParams as ConstraintLayout.LayoutParams?
                            ctaButtonParams?.dimensionRatio = config.ctaRatio
                            ctaButton?.layoutParams = ctaButtonParams
                        }
                    }
                }

                //set background color
                runCatching {
                    if (newViewAdsInflateFromXml != null) {
                        val adViewHolder =
                            newViewAdsInflateFromXml!!.findViewById<ConstraintLayout>(R.id.adViewHolder)
                        adViewHolder.setBackgroundColor(Color.parseColor(config.backGroundColor))
                    }
                }

                //set content text color
                runCatching {
                    if (newViewAdsInflateFromXml != null) {
                        val headLineText =
                            newViewAdsInflateFromXml!!.findViewById<TextView>(R.id.ad_headline)
                        val bodyText =
                            newViewAdsInflateFromXml!!.findViewById<TextView>(R.id.ad_body)
                        headLineText.setTextColor(Color.parseColor(config.textContentColor))
                        bodyText.setTextColor(Color.parseColor(config.textContentColor))
                    }

                }

                //set ratio view
                runCatching {
                    val layoutContainAdsParams =
                        layoutContainAds?.layoutParams as ConstraintLayout.LayoutParams?
                    layoutContainAdsParams?.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
                    layoutContainAds?.layoutParams = layoutContainAdsParams
                    newViewAdsInflateFromXml?.visibility = View.INVISIBLE
                    layoutToAttachAds.addView(newViewAdsInflateFromXml)
                }

            }
        }

        var stateNative1 = StateLoadAd.LOADING
        var stateNative2 = StateLoadAd.LOADING
        var stateNative3 = StateLoadAd.LOADING
        var isAnyShow = false

        fun checkShowNative() {
            if (isAnyShow) return
            if (stateNative1 == StateLoadAd.SUCCESS) {
                isAnyShow = true
                showLoadedNative(
                    spaceNameConfig = spaceNameConfig,
                    spaceName = spaceName1,
                    adChoice = newAdChoice,
                    isResetConfig = false,
                    includeHasBeenOpened = includeHasBeenOpened,
                    layoutToAttachAds = layoutToAttachAds,
                    layoutContainAds = layoutContainAds,
                    viewAdsInflateFromXml = newViewAdsInflateFromXml,
                    onAdsClick = onAdsClick
                )
            } else if (stateNative2 == StateLoadAd.SUCCESS) {
                isAnyShow = true
                showLoadedNative(
                    spaceNameConfig = spaceNameConfig,
                    spaceName = spaceName2,
                    adChoice = newAdChoice,
                    isResetConfig = false,
                    includeHasBeenOpened = includeHasBeenOpened,
                    layoutToAttachAds = layoutToAttachAds,
                    layoutContainAds = layoutContainAds,
                    viewAdsInflateFromXml = newViewAdsInflateFromXml,
                    onAdsClick = onAdsClick
                )
            } else if (stateNative3 == StateLoadAd.SUCCESS) {
                isAnyShow = true
                showLoadedNative(
                    spaceNameConfig = spaceNameConfig,
                    spaceName = spaceName3,
                    adChoice = newAdChoice,
                    isResetConfig = false,
                    includeHasBeenOpened = includeHasBeenOpened,
                    layoutToAttachAds = layoutToAttachAds,
                    layoutContainAds = layoutContainAds,
                    viewAdsInflateFromXml = newViewAdsInflateFromXml,
                    onAdsClick = onAdsClick
                )
            }
        }


        safePreloadAds(
            spaceNameConfig = spaceNameConfig,
            spaceNameAds = spaceName1,
            adChoice = newAdChoice,
            includeHasBeenOpened = includeHasBeenOpened,
            preloadCallback = object :
                PreloadCallback {
                override fun onLoadDone() {
                    stateNative1 = StateLoadAd.SUCCESS
                    checkShowNative()
                }
            })
        safePreloadAds(
            spaceNameConfig = spaceNameConfig,
            spaceNameAds = spaceName2,
            adChoice = newAdChoice,
            includeHasBeenOpened = includeHasBeenOpened,
            preloadCallback = object :
                PreloadCallback {
                override fun onLoadDone() {
                    stateNative2 = StateLoadAd.SUCCESS
                    checkShowNative()
                }
            })
        safePreloadAds(
            spaceNameConfig = spaceNameConfig,
            spaceNameAds = spaceName3,
            adChoice = newAdChoice,
            includeHasBeenOpened = includeHasBeenOpened,
            preloadCallback = object :
                PreloadCallback {
                override fun onLoadDone() {
                    stateNative3 = StateLoadAd.SUCCESS
                    checkShowNative()
                }
            })

    } else {
        layoutToAttachAds.visibility = View.GONE
        layoutContainAds?.visibility = View.GONE
    }
}

fun Fragment.show2NativeUsePriority(
    spaceNameConfig: String,
    spaceName1: String,
    spaceName2: String,
    timeOut: Long = AdsConstant.timeDelayNative,
    adChoice: Int? = null,
    includeHasBeenOpened: Boolean = false,
    isResetConfig: Boolean = true,
    layoutToAttachAds: ViewGroup,
    layoutContainAds: ViewGroup? = null,
    viewAdsInflateFromXml: View? = null,
    onAdsClick: (() -> Unit)? = null
) {
    if (checkConditionShowAds(context, spaceNameConfig)) {

        //lay data ve view
        var newAdChoice = adChoice
        var newViewAdsInflateFromXml = viewAdsInflateFromXml

        AdsConstant.listConfigAds[spaceNameConfig]?.let { config ->
            if (isResetConfig) {
                config.getConfigNative(
                    context = context,
                    default = ConfigNative(
                        adChoice = AdsConstant.TOP_LEFT,
                        viewAds = LayoutInflater.from(context)
                            .inflate(R.layout.layout_native_small_icon_ctaright, null)
                    )
                ).let { configNative ->

                    if (newAdChoice == null) {
                        newAdChoice = configNative.adChoice
                    }

                    if (newViewAdsInflateFromXml == null) {
                        newViewAdsInflateFromXml = configNative.viewAds
                    }
                }

                //set cta shape
                //set cta color
                //set cta radius
                //set cta ratio
                runCatching {
                    if (newViewAdsInflateFromXml != null) {
                        val listGradientColor = mutableListOf<Int>()
                        config.ctaGradientListColor?.forEach { colorString ->
                            kotlin.runCatching {
                                listGradientColor.add(Color.parseColor(colorString))
                            }
                        }

                        if (listGradientColor.isEmpty()) {
                            if (context != null) {
                                listGradientColor.add(context!!.getColor(R.color.cta_color))
                                listGradientColor.add(context!!.getColor(R.color.cta_color))
                            } else {
                                listGradientColor.add(Color.parseColor("#3ADB41"))
                                listGradientColor.add(Color.parseColor("#3ADB41"))
                            }
                        }else if (listGradientColor.size == 1){
                            listGradientColor.add(listGradientColor[0])
                        }

                        val gradientDrawable = GradientDrawable(
                            GradientDrawable.Orientation.LEFT_RIGHT,
                            listGradientColor.toIntArray()
                        ).apply {
                            shape = GradientDrawable.RECTANGLE
                            cornerRadius = TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP,
                                config.ctaConnerRadius.toFloat(),
                                Resources.getSystem().displayMetrics
                            )
                        }
                        val ctaButton =
                            newViewAdsInflateFromXml!!.findViewById<TextView>(R.id.ad_call_to_action)
                        ctaButton.backgroundTintList = null
                        ctaButton.background = gradientDrawable
                        ctaButton.setTextColor(Color.parseColor(config.textCTAColor))
                        if (config.ctaRatio != null) {
                            val ctaButtonParams =
                                ctaButton?.layoutParams as ConstraintLayout.LayoutParams?
                            ctaButtonParams?.dimensionRatio = config.ctaRatio
                            ctaButton?.layoutParams = ctaButtonParams
                        }
                    }
                }

                //set background color
                runCatching {
                    if (newViewAdsInflateFromXml != null) {
                        val adViewHolder =
                            newViewAdsInflateFromXml!!.findViewById<ConstraintLayout>(R.id.adViewHolder)
                        adViewHolder.setBackgroundColor(Color.parseColor(config.backGroundColor))
                    }
                }

                //set content text color
                runCatching {
                    if (newViewAdsInflateFromXml != null) {
                        val headLineText =
                            newViewAdsInflateFromXml!!.findViewById<TextView>(R.id.ad_headline)
                        val bodyText =
                            newViewAdsInflateFromXml!!.findViewById<TextView>(R.id.ad_body)
                        headLineText.setTextColor(Color.parseColor(config.textContentColor))
                        bodyText.setTextColor(Color.parseColor(config.textContentColor))
                    }

                }

                //set ratio view
                runCatching {
                    val layoutContainAdsParams =
                        layoutContainAds?.layoutParams as ConstraintLayout.LayoutParams?
                    layoutContainAdsParams?.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
                    layoutContainAds?.layoutParams = layoutContainAdsParams
                    newViewAdsInflateFromXml?.visibility = View.INVISIBLE
                    layoutToAttachAds.addView(newViewAdsInflateFromXml)
                }

            }
        }

        var stateNative1 = StateLoadAd.LOADING
        var stateNative2 = StateLoadAd.LOADING

        var isTimeOut = false
        val timeOutRunnable = Runnable {
            isTimeOut = true
            if (stateNative1 == StateLoadAd.HAS_BEEN_OPENED || stateNative2 == StateLoadAd.HAS_BEEN_OPENED) return@Runnable
            //show 2 native
            show2Native(
                spaceNameConfig = spaceNameConfig,
                spaceName1 = spaceName1,
                spaceName2 = spaceName2,
                isResetConfig = false,
                includeHasBeenOpened = includeHasBeenOpened,
                layoutToAttachAds = layoutToAttachAds,
                layoutContainAds = layoutContainAds,
                viewAdsInflateFromXml = newViewAdsInflateFromXml,
                onAdsClick = onAdsClick
            )
        }

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(timeOutRunnable, timeOut)

        fun checkShowNative() {
            if (isTimeOut) return
            if (stateNative1 == StateLoadAd.HAS_BEEN_OPENED || stateNative2 == StateLoadAd.HAS_BEEN_OPENED) return
            if (stateNative1 == StateLoadAd.SUCCESS) {
                stateNative1 = StateLoadAd.HAS_BEEN_OPENED
                handler.removeCallbacks(timeOutRunnable)
                showLoadedNative(
                    spaceNameConfig = spaceNameConfig,
                    spaceName = spaceName1,
                    adChoice = newAdChoice,
                    isResetConfig = false,
                    includeHasBeenOpened = includeHasBeenOpened,
                    layoutToAttachAds = layoutToAttachAds,
                    layoutContainAds = layoutContainAds,
                    viewAdsInflateFromXml = newViewAdsInflateFromXml,
                    onAdsClick = onAdsClick
                )
            } else if (stateNative2 == StateLoadAd.SUCCESS && stateNative1 == StateLoadAd.LOAD_FAILED) {
                stateNative2 = StateLoadAd.HAS_BEEN_OPENED
                handler.removeCallbacks(timeOutRunnable)
                showLoadedNative(
                    spaceNameConfig = spaceNameConfig,
                    spaceName = spaceName2,
                    adChoice = newAdChoice,
                    isResetConfig = false,
                    includeHasBeenOpened = includeHasBeenOpened,
                    layoutToAttachAds = layoutToAttachAds,
                    layoutContainAds = layoutContainAds,
                    viewAdsInflateFromXml = newViewAdsInflateFromXml,
                    onAdsClick = onAdsClick
                )
            } else if (stateNative1 == StateLoadAd.LOAD_FAILED && stateNative2 == StateLoadAd.LOAD_FAILED) {
                handler.removeCallbacks(timeOutRunnable)
            }
        }

        safePreloadAds(
            spaceNameConfig = spaceNameConfig,
            spaceNameAds = spaceName1,
            adChoice = newAdChoice,
            includeHasBeenOpened = includeHasBeenOpened,
            preloadCallback = object : PreloadCallback {
                override fun onLoadDone() {
                    stateNative1 = StateLoadAd.SUCCESS
                    checkShowNative()
                }

                override fun onLoadFail(error: String) {
                    stateNative1 = StateLoadAd.LOAD_FAILED
                    checkShowNative()
                }
            })
        safePreloadAds(
            spaceNameConfig = spaceNameConfig,
            spaceNameAds = spaceName2,
            adChoice = newAdChoice,
            includeHasBeenOpened = includeHasBeenOpened,
            preloadCallback = object : PreloadCallback {
                override fun onLoadDone() {
                    stateNative2 = StateLoadAd.SUCCESS
                    checkShowNative()
                }

                override fun onLoadFail(error: String) {
                    stateNative2 = StateLoadAd.LOAD_FAILED
                    checkShowNative()
                }
            })

    } else {
        layoutToAttachAds.visibility = View.GONE
        layoutContainAds?.visibility = View.GONE
    }
}

fun Fragment.show2Native(
    spaceNameConfig: String,
    spaceName1: String,
    spaceName2: String,
    adChoice: Int? = null,
    includeHasBeenOpened: Boolean = false,
    isResetConfig: Boolean = true,
    layoutToAttachAds: ViewGroup,
    layoutContainAds: ViewGroup? = null,
    viewAdsInflateFromXml: View? = null,
    onAdsClick: (() -> Unit)? = null
) {
    if (checkConditionShowAds(context, spaceNameConfig)) {

        //lay data ve view
        var newAdChoice = adChoice
        var newViewAdsInflateFromXml = viewAdsInflateFromXml


        AdsConstant.listConfigAds[spaceNameConfig]?.let { config ->
            if (isResetConfig) {
                config.getConfigNative(
                    context = context,
                    default = ConfigNative(
                        adChoice = AdsConstant.TOP_LEFT,
                        viewAds = LayoutInflater.from(context)
                            .inflate(R.layout.layout_native_small_icon_ctaright, null)
                    )
                ).let { configNative ->

                    if (newAdChoice == null) {
                        newAdChoice = configNative.adChoice
                    }

                    if (newViewAdsInflateFromXml == null) {
                        newViewAdsInflateFromXml = configNative.viewAds
                    }
                }

                //set cta shape
                //set cta color
                //set cta radius
                //set cta ratio
                runCatching {
                    if (newViewAdsInflateFromXml != null) {
                        val listGradientColor = mutableListOf<Int>()
                        config.ctaGradientListColor?.forEach { colorString ->
                            kotlin.runCatching {
                                listGradientColor.add(Color.parseColor(colorString))
                            }
                        }

                        if (listGradientColor.isEmpty()) {
                            if (context != null) {
                                listGradientColor.add(context!!.getColor(R.color.cta_color))
                                listGradientColor.add(context!!.getColor(R.color.cta_color))
                            } else {
                                listGradientColor.add(Color.parseColor("#3ADB41"))
                                listGradientColor.add(Color.parseColor("#3ADB41"))
                            }
                        }else if (listGradientColor.size == 1){
                            listGradientColor.add(listGradientColor[0])
                        }

                        val gradientDrawable = GradientDrawable(
                            GradientDrawable.Orientation.LEFT_RIGHT,
                            listGradientColor.toIntArray()
                        ).apply {
                            shape = GradientDrawable.RECTANGLE
                            cornerRadius = TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP,
                                config.ctaConnerRadius.toFloat(),
                                Resources.getSystem().displayMetrics
                            )
                        }
                        val ctaButton =
                            newViewAdsInflateFromXml!!.findViewById<TextView>(R.id.ad_call_to_action)
                        ctaButton.backgroundTintList = null
                        ctaButton.background = gradientDrawable
                        ctaButton.setTextColor(Color.parseColor(config.textCTAColor))
                        if (config.ctaRatio != null) {
                            val ctaButtonParams =
                                ctaButton?.layoutParams as ConstraintLayout.LayoutParams?
                            ctaButtonParams?.dimensionRatio = config.ctaRatio
                            ctaButton?.layoutParams = ctaButtonParams
                        }
                    }
                }

                //set background color
                runCatching {
                    if (newViewAdsInflateFromXml != null) {
                        val adViewHolder =
                            newViewAdsInflateFromXml!!.findViewById<ConstraintLayout>(R.id.adViewHolder)
                        adViewHolder.setBackgroundColor(Color.parseColor(config.backGroundColor))
                    }
                }

                //set content text color
                runCatching {
                    if (newViewAdsInflateFromXml != null) {
                        val headLineText =
                            newViewAdsInflateFromXml!!.findViewById<TextView>(R.id.ad_headline)
                        val bodyText =
                            newViewAdsInflateFromXml!!.findViewById<TextView>(R.id.ad_body)
                        headLineText.setTextColor(Color.parseColor(config.textContentColor))
                        bodyText.setTextColor(Color.parseColor(config.textContentColor))
                    }

                }

                //set ratio view
                runCatching {
                    val layoutContainAdsParams =
                        layoutContainAds?.layoutParams as ConstraintLayout.LayoutParams?
                    layoutContainAdsParams?.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
                    layoutContainAds?.layoutParams = layoutContainAdsParams
                    newViewAdsInflateFromXml?.visibility = View.INVISIBLE
                    layoutToAttachAds.addView(newViewAdsInflateFromXml)
                }

            }
        }

        var stateNative1 = StateLoadAd.LOADING
        var stateNative2 = StateLoadAd.LOADING
        var isAnyShow = false

        fun checkShowNative() {
            if (isAnyShow) return
            if (stateNative1 == StateLoadAd.SUCCESS) {
//                isAnyShow = true
                stateNative1 = StateLoadAd.HAS_BEEN_OPENED
                //show 1
                showLoadedNative(
                    spaceNameConfig = spaceNameConfig,
                    spaceName = spaceName1,
                    adChoice = newAdChoice,
                    isResetConfig = false,
                    includeHasBeenOpened = includeHasBeenOpened,
                    layoutToAttachAds = layoutToAttachAds,
                    layoutContainAds = layoutContainAds,
                    viewAdsInflateFromXml = newViewAdsInflateFromXml,
                    onAdsClick = onAdsClick
                )
            } else if (stateNative2 == StateLoadAd.SUCCESS) {
                isAnyShow = true
                //show 2
                stateNative2 = StateLoadAd.HAS_BEEN_OPENED
                showLoadedNative(
                    spaceNameConfig = spaceNameConfig,
                    spaceName = spaceName2,
                    adChoice = newAdChoice,
                    isResetConfig = false,
                    includeHasBeenOpened = includeHasBeenOpened,
                    layoutToAttachAds = layoutToAttachAds,
                    layoutContainAds = layoutContainAds,
                    viewAdsInflateFromXml = newViewAdsInflateFromXml,
                    onAdsClick = onAdsClick
                )
            }
        }

        safePreloadAds(
            spaceNameConfig = spaceNameConfig,
            spaceNameAds = spaceName1,
            adChoice = newAdChoice,
            includeHasBeenOpened = includeHasBeenOpened,
            preloadCallback = object : PreloadCallback {
                override fun onLoadDone() {
                    stateNative1 = StateLoadAd.SUCCESS
                    checkShowNative()
                }

                override fun onLoadFail(error: String) {
                    stateNative1 = StateLoadAd.LOAD_FAILED
                    checkShowNative()
                }
            })
        safePreloadAds(
            spaceNameConfig = spaceNameConfig,
            spaceNameAds = spaceName2,
            adChoice = newAdChoice,
            includeHasBeenOpened = includeHasBeenOpened,
            preloadCallback = object : PreloadCallback {
                override fun onLoadDone() {
                    stateNative2 = StateLoadAd.SUCCESS
                    checkShowNative()
                }

                override fun onLoadFail(error: String) {
                    stateNative2 = StateLoadAd.LOAD_FAILED
                    checkShowNative()
                }
            })

    } else {
        layoutToAttachAds.visibility = View.GONE
        layoutContainAds?.visibility = View.GONE
    }
}