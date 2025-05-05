package pion.datlt.libads.utils.adsuntils

import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import pion.datlt.libads.AdsController
import pion.datlt.libads.callback.AdCallback
import pion.datlt.libads.utils.AdsConstant

fun Fragment.showLoadedBannerCollapsible(
    spaceNameConfig: String,
    spaceName: String,
    positionCollapsibleBanner: String = AdsConstant.COLLAPSIBLE_BOTTOM,
    isOneTimeCollapsible: Boolean = false,
    includeHasBeenOpened: Boolean = false,
    ratioView: String? = null,
    isResetRatio: Boolean = true,
    layoutToAttachAds: ViewGroup,
    layoutContainAds: ViewGroup? = null
){
    if (checkConditionShowAds(context, spaceNameConfig)) {

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
            lifecycle = lifecycle,
            positionCollapsibleBanner = positionCollapsibleBanner,
            isOneTimeCollapsible = isOneTimeCollapsible,
            includeHasBeenOpened = includeHasBeenOpened,
            layoutToAttachAds = layoutToAttachAds,
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


    }else{
        layoutToAttachAds.visibility = View.GONE
        layoutContainAds?.visibility = View.GONE
    }
}

fun Fragment.loadAndShowBannerCollapsible(
    spaceNameConfig: String,
    spaceName: String,
    positionCollapsibleBanner: String = AdsConstant.COLLAPSIBLE_BOTTOM,
    isOneTimeCollapsible: Boolean = false,
    ratioView: String? = null,
    isResetRatio: Boolean = true,
    layoutToAttachAds: ViewGroup,
    layoutContainAds: ViewGroup? = null
){
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
            positionCollapsibleBanner = positionCollapsibleBanner,
            isOneTimeCollapsible = isOneTimeCollapsible,
            layoutToAttachAds = layoutToAttachAds,
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


    }else{
        layoutToAttachAds.visibility = View.GONE
        layoutContainAds?.visibility = View.GONE
    }
}