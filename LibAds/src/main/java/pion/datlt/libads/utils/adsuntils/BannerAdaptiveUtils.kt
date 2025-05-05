package pion.datlt.libads.utils.adsuntils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import pion.datlt.libads.AdsController
import pion.datlt.libads.R
import pion.datlt.libads.callback.AdCallback

fun Fragment.showLoadedBannerAdaptive(
    spaceNameConfig: String,
    spaceName: String,
    includeHasBeenOpened: Boolean = false,
    isPreloadAfterShow: Boolean = false,
    ratioView: String? = null,
    isResetRatio: Boolean = true,
    layoutToAttachAds: ViewGroup,
    layoutContainAds: ViewGroup? = null,
    widthBannerAdaptiveAds: Int? = null
) {
    if (spaceName.contains("camera360")) {
        Log.d("CHECKSHOWBANNER", "showLoadedBannerAdaptive 1: $spaceName")
    }
    if (checkConditionShowAds(context, spaceNameConfig)) {
        if (spaceName.contains("camera360")) {
            Log.d("CHECKSHOWBANNER", "showLoadedBannerAdaptive 2: $spaceName")
        }
        ratioView?.let {
            if (isResetRatio) {
                val layoutContainAdsParams =
                    layoutContainAds?.layoutParams as ConstraintLayout.LayoutParams?
                layoutContainAdsParams?.dimensionRatio = it
                layoutContainAds?.layoutParams = layoutContainAdsParams
            }
        }


        AdsController.getInstance().showLoadedAds(
            spaceName = spaceName,
            includeHasBeenOpened = includeHasBeenOpened,
            lifecycle = lifecycle,
            layoutToAttachAds = layoutToAttachAds,
            widthBannerAdaptiveAds = widthBannerAdaptiveAds,
            adCallback = object : AdCallback {
                override fun onAdShow() {
                    //doan nay co the them tag ad
                    if (spaceName.contains("camera360")) {
                        Log.d("CHECKSHOWBANNER", "onAdShow: $spaceName")
                    }
                    if (isPreloadAfterShow) {
                        safePreloadAds(
                            spaceNameConfig = spaceNameConfig,
                            spaceNameAds = spaceName,
                            includeHasBeenOpened = includeHasBeenOpened,
                            widthBannerAdaptiveAds = widthBannerAdaptiveAds
                        )
                    }
                }

                override fun onAdClose() {
                    if (spaceName.contains("camera360")) {
                        Log.d("CHECKSHOWBANNER", "onAdClose: $spaceName")
                    }
                }

                override fun onAdFailToLoad(messageError: String?) {
                    if (spaceName.contains("camera360")) {
                        Log.d("CHECKSHOWBANNER", "onAdFailToLoad: $spaceName $messageError")
                    }
                }

                override fun onAdClick() {
                    AdsController.isBlockOpenAds = true
                    if (spaceName.contains("camera360")) {
                        Log.d("CHECKSHOWBANNER", "onAdClick: $spaceName")
                    }
                }

            }
        )

    } else {
        layoutToAttachAds.visibility = View.GONE
        layoutContainAds?.visibility = View.GONE
    }
}

fun Fragment.loadAndShowBannerAdaptive(
    spaceNameConfig: String,
    spaceName: String,
    ratioView: String? = null,
    isResetRatio: Boolean = true,
    layoutToAttachAds: ViewGroup,
    layoutContainAds: ViewGroup? = null,
    widthBannerAdaptiveAds: Int? = null
) {
    if (checkConditionShowAds(context, spaceNameConfig)) {

        ratioView?.let {
            if (isResetRatio) {
                val layoutContainAdsParams =
                    layoutContainAds?.layoutParams as ConstraintLayout.LayoutParams?
                layoutContainAdsParams?.dimensionRatio = it
                layoutContainAds?.layoutParams = layoutContainAdsParams
            }
        }


        AdsController.getInstance().loadAndShow(
            spaceName = spaceName,
            lifecycle = lifecycle,
            layoutToAttachAds = layoutToAttachAds,
            widthBannerAdaptiveAds = widthBannerAdaptiveAds,
            adCallback = object : AdCallback {
                override fun onAdShow() {
                    //doan nay co the them tag ad
                }

                override fun onAdClose() {

                }

                override fun onAdFailToLoad(messageError: String?) {
                }

                override fun onAdClick() {
                    AdsController.isBlockOpenAds = true
                }

            }
        )

    } else {
        layoutToAttachAds.visibility = View.GONE
        layoutContainAds?.visibility = View.GONE
    }
}


fun Fragment.showSplashBannerAdaptive(
    spaceNameConfig: String,
    spaceName: String,
    ratioView: String? = null,
    isResetRatio: Boolean = true,
    layoutToAttachAds: ViewGroup,
    layoutContainAds: ViewGroup? = null,
    onAdsDone: (() -> Unit)? = null
) {
    if (checkConditionShowAds(context, spaceNameConfig)) {

        ratioView?.let {
            if (isResetRatio) {
                val layoutContainAdsParams =
                    layoutContainAds?.layoutParams as ConstraintLayout.LayoutParams?
                layoutContainAdsParams?.dimensionRatio = it
                layoutContainAds?.layoutParams = layoutContainAdsParams
            }
        }

        AdsController.getInstance().loadAndShow(
            spaceName = spaceName,
            lifecycle = lifecycle,
            layoutToAttachAds = layoutToAttachAds,
            adCallback = object : AdCallback {
                override fun onAdShow() {
                    //doan nay co the them tag ad
                    context?.let { setTagAdsBanner(it, layoutContainAds) }
                    onAdsDone?.invoke()
                    Log.d("CHECKSPLASHBANNER", "onAdShow: 1")
                }

                override fun onAdClose() {

                }

                override fun onAdFailToLoad(messageError: String?) {
                    onAdsDone?.invoke()
                    Log.d("CHECKSPLASHBANNER", "onAdShow: 2 $messageError")
                }

                override fun onAdClick() {
                    AdsController.isBlockOpenAds = true
                }

            }
        )
    } else {
        layoutToAttachAds.visibility = View.GONE
        layoutContainAds?.visibility = View.GONE
        onAdsDone?.invoke()
    }

}

fun setTagAdsBanner(context: Context, layoutContainAds: ViewGroup?) {
    val layoutParams = FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.WRAP_CONTENT,
        FrameLayout.LayoutParams.WRAP_CONTENT
    ).apply {
        gravity = Gravity.TOP or Gravity.END
    }
    val mTypeface: Typeface? =
        context.let { ResourcesCompat.getFont(it, R.font.font_700) }
    val textView = TextView(context).apply {
        text = "AD"
        background = ContextCompat.getDrawable(context, R.drawable.bg_radius_1)
        val color = Color.parseColor("#FFA800")
        backgroundTintList = ColorStateList.valueOf(color)
        setTextColor(Color.WHITE)
        textSize = 12f
        typeface = mTypeface
        setPadding(6, 1, 6, 1)
    }
    textView.layoutParams = layoutParams
    layoutContainAds?.addView(textView)
}

