package pion.datlt.libads.admob.ads

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MediaAspectRatio
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pion.datlt.libads.AdsController
import pion.datlt.libads.R
import pion.datlt.libads.callback.AdCallback
import pion.datlt.libads.callback.PreloadCallback
import pion.datlt.libads.model.AdsChild
import pion.datlt.libads.utils.AdDef
import pion.datlt.libads.utils.AdsConstant
import pion.datlt.libads.utils.CommonUtils
import pion.datlt.libads.utils.StateLoadAd
import java.util.*

class AdmobNativeFullScreenAds : AdmobAds(){

    private var nativeAds: NativeAd? = null

    private var mPreloadCallback: PreloadCallback? = null
    private var mAdCallback: AdCallback? = null
    private var error = ""
    private var stateLoadAd = StateLoadAd.NONE
    private var mAdsChild: AdsChild? = null

    private var mDestinationToShowAds: Int? = null

    var adSourceId = ""
    var adSourceName = ""
    var adUnitId = ""

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
        mAdCallback = adCallback
        mDestinationToShowAds = destinationToShowAds

        if (stateLoadAd == StateLoadAd.LOADING) {
            //khong load cai moi nua
            //doi cai cu load xong roi show
        } else {
            //load cai moi
            load(
                activity = activity,
                adsChild = adsChild,
                isPreload = false,
                adChoice = adChoice,
                loadCallback = object : PreloadCallback {
                    override fun onLoadDone() {
                        show(
                            activity = activity,
                            adsChild = adsChild,
                            destinationToShowAds = destinationToShowAds,
                            layoutToAttachAds = layoutToAttachAds,
                            viewAdsInflateFromXml = viewAdsInflateFromXml,
                            lifecycle = lifecycle,
                            adCallback = adCallback,
                            timeShowNativeCollapsibleAfterClose = timeShowNativeCollapsibleAfterClose
                        )
                    }

                    override fun onLoadFail(error: String) {
                        super.onLoadFail(error)
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
            adChoice = adChoice,
            isPreload = true,
        )
    }


    private fun load(
        activity: Activity,
        adsChild: AdsChild,
        isPreload: Boolean,
        adChoice: Int? = null,
        loadCallback: PreloadCallback? = null
    ) {

        Log.d(
            "TESTERADSEVENT",
            "start load native full screen : ads name ${adsChild.spaceName} id ${adsChild.adsId}"
        )

        CoroutineScope(Dispatchers.IO).launch {
            stateLoadAd = StateLoadAd.LOADING
            val id = if (AdsConstant.isDebug) AdsConstant.ID_ADMOB_NATIVE_VIDEO_TEST else adsChild.adsId
            mAdsChild = adsChild
            error = ""


            val videoOptions = VideoOptions.Builder().setStartMuted(true).build()
            val adOptions = NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .setAdChoicesPlacement(adChoice ?: NativeAdOptions.ADCHOICES_TOP_RIGHT) //set vị trí của ADCHOICES button
                .setMediaAspectRatio(MediaAspectRatio.ANY)
                .build()

            val adLoader = AdLoader.Builder(activity.applicationContext, id)
                .forNativeAd { adNative ->
                    //đã load xong view, hiển thị lên nếu cần
                    nativeAds?.destroy()
                    nativeAds = null
                    nativeAds = adNative
                    stateLoadAd = StateLoadAd.SUCCESS
                    loadCallback?.onLoadDone()
                    adNative.responseInfo?.adapterResponses?.forEach { responseInfo ->
                        if (responseInfo.adSourceId.isNotEmpty()) {
                            adSourceId = responseInfo.adSourceId
                        }
                        if (responseInfo.adSourceName.isNotEmpty()) {
                            adSourceName = responseInfo.adSourceName
                        }
                    }
                    adUnitId = id

                    adNative.setOnPaidEventListener { adValue ->
                        val bundle = Bundle().apply {
                            putString("ad_unit_id" , adUnitId)
                            putInt("precision_type" , adValue.precisionType)
                            putLong("revenue_micros" , adValue.valueMicros)
                            putString("ad_source_id" , adSourceId)
                            putString("ad_source_name" , adSourceName)
                            putString("ad_type" , AdDef.ADS_TYPE_ADMOB.NATIVE_FULL_SCREEN)
                            putString("currency_code" , adValue.currencyCode)
                        }
                        mAdCallback?.onPaidEvent(bundle)
                    }

                }
                .withAdListener(object : AdListener() {

                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        Log.d("TESTERADSEVENT", "load success native full screen : ads name ${adsChild.spaceName} id ${adsChild.adsId}")

                        timeLoader = Date().time

                        if (isPreload) {
                            mPreloadCallback?.onLoadDone()
                        }
                    }

                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        //gọi khi ad load failed
                        Log.d("TESTERADSEVENT", "load failed native full screen  : ads name ${adsChild.spaceName} id ${adsChild.adsId} error : ${adError.message}")

                        error = adError.message
                        stateLoadAd = StateLoadAd.LOAD_FAILED
                        mAdCallback?.onAdFailToLoad(adError.message)
                        loadCallback?.onLoadFail(adError.message)
                        if (isPreload) {
                            mPreloadCallback?.onLoadFail(adError.message)
                        }
                    }

                    override fun onAdClosed() {
                        super.onAdClosed()
                        mAdCallback?.onAdClose()
                    }

                    override fun onAdOpened() {
                        super.onAdOpened()
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
                        mAdCallback?.onAdClick()
                        Log.d(
                            "TESTERADSEVENT",
                            "click native full screen  : ads name ${adsChild.spaceName} id ${adsChild.adsId}"
                        )
                    }

                })
                .withNativeAdOptions(adOptions)
                .build()

            val request = AdRequest.Builder().build()

            withContext(Dispatchers.Main){
                adLoader.loadAd(request)
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
        mAdCallback = adCallback // show
        mDestinationToShowAds = destinationToShowAds


        if (layoutToAttachAds != null) {
            layoutToAttachAds.visibility = View.VISIBLE
            if (viewAdsInflateFromXml != null) {
                viewAdsInflateFromXml.visibility = View.VISIBLE
                val nativeAdView = NativeAdView(activity)
                nativeAdView.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                //qc native thường sẽ có 2 lớp
                //layout ads là lớp ngoài cùng, sẽ là layout chứa toàn bộ khu vực qc
                //adsviewgroup là lớp trong được chứa bới layout ads, sẽ là phần chứa native view
                //clear các view con nằm trong adsview
                viewAdsInflateFromXml.parent?.let {
                    (it as ViewGroup).removeView(viewAdsInflateFromXml)
                }

                nativeAdView.addView(viewAdsInflateFromXml)

                if (mDestinationToShowAds != null && mDestinationToShowAds != AdsController.currentDestinationId) {
                    adCallback?.onAdFailToLoad("show in wrong destination")
                    Log.d("TESTERADSEVENT", "show failed native full screen  : ads name ${adsChild.spaceName} id ${adsChild.adsId} error : show in wrong destination")

                } else {
                    nativeAds?.let {
                        populateUnifiedNativeAdView(it, nativeAdView)
                        //clear các view con nằm trong adsViewGroup
                        layoutToAttachAds.removeAllViews()
                        layoutToAttachAds.addView(nativeAdView)
                        stateLoadAd = StateLoadAd.HAS_BEEN_OPENED
                        CommonUtils.showToastDebug(activity, "Admob Native id: ${adsChild.adsId}")
                        mAdCallback?.onAdShow()
                    }
                    Log.d("TESTERADSEVENT", "show success native full screen  : ads name ${adsChild.spaceName} id ${adsChild.adsId}")

                }
            } else {
                CommonUtils.showToastDebug(activity, "viewAdsInflateFromXml native not null")
                Log.d("TESTERADSEVENT", "show failed native full screen  : ads name ${adsChild.spaceName} id ${adsChild.adsId} error : viewAdsInflateFromXml native not null")

            }
        } else {
            CommonUtils.showToastDebug(activity, "layoutToAttachAds native not null")
            Log.d("TESTERADSEVENT", "show failed native full screen  : ads name ${adsChild.spaceName} id ${adsChild.adsId} error : layoutToAttachAds native not null")

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

    private fun populateUnifiedNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        //set media view
        val viewGroup = adView.findViewById<ViewGroup>(R.id.ad_media)
        if (viewGroup != null) {
            val mediaView = MediaView(adView.context)
            viewGroup.addView(
                mediaView,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            adView.mediaView = mediaView
        } else {
            if (AdsConstant.isDebug) {
                val mediaView = MediaView(adView.context)
                adView.addView(
                    mediaView,
                    ViewGroup.LayoutParams(
                        0,
                        0
                    )
                )
                adView.mediaView = mediaView
            }
        }

        //set icon
        val viewGroupIcon = adView.findViewById<View>(R.id.ad_app_icon)
        if (viewGroupIcon != null) {
            if (viewGroupIcon is ViewGroup) {
                val nativeAdIcon = ImageView(adView.context)
                viewGroupIcon.addView(
                    nativeAdIcon,
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
                adView.iconView = nativeAdIcon
            } else {
                adView.iconView = viewGroupIcon
            }

        }

        // Set các thành phần khác
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        // head line là thành phần bắt buộc phải có trong ad native
        //không cần check null cứ hiển thị thoi
        if (AdsConstant.isDebug) {
            (adView.headlineView as TextView).text = mAdsChild?.spaceName ?: "null"
        } else {
            (adView.headlineView as TextView).text = nativeAd.headline
        }

        if (adView.mediaView != null && nativeAd.mediaContent != null) {
            adView.mediaView!!.mediaContent = nativeAd.mediaContent!!
        }


        // những thành phần còn lại có thể không được trả về hoặc null nên phải check
        if (AdsConstant.isDebug) {
            (adView.bodyView as TextView?)?.text = mAdsChild?.adsId ?: "null"
        } else {
            if (nativeAd.body == null) {
                if (adView.bodyView != null) {
                    adView.bodyView!!.visibility = View.INVISIBLE
                }
            } else {
                if (adView.bodyView != null) {
                    adView.bodyView!!.visibility = View.VISIBLE
                    (adView.bodyView as TextView).text = nativeAd.body
                }
            }
        }

        if (adView.callToActionView != null) {
            if (adView.callToActionView != null) {
                if (nativeAd.callToAction == null) {
                    adView.callToActionView!!.visibility = View.INVISIBLE
                } else {
                    adView.callToActionView!!.visibility = View.VISIBLE
                    if (adView.callToActionView is Button) {
                        (adView.callToActionView as Button).text = nativeAd.callToAction
                    } else {
                        (adView.callToActionView as TextView).text = nativeAd.callToAction
                    }
                }
            }
        }
        if (adView.iconView != null) {
            if (nativeAd.icon == null) {
                adView.iconView!!.visibility = View.GONE
            } else {
                (adView.iconView as ImageView).setImageDrawable(
                    nativeAd.icon!!.drawable
                )
                adView.iconView!!.visibility = View.VISIBLE
            }
        }
        if (adView.priceView != null) {
            if (nativeAd.price == null) {
                adView.priceView!!.visibility = View.INVISIBLE
            } else {
                adView.priceView!!.visibility = View.VISIBLE
                (adView.priceView as TextView).text = nativeAd.price
            }
        }
        if (adView.storeView != null) {
            if (nativeAd.store == null) {
                adView.storeView!!.visibility = View.INVISIBLE
            } else {
                adView.storeView!!.visibility = View.VISIBLE
                (adView.storeView as TextView).text = nativeAd.store
            }
        }
        if (adView.starRatingView != null) {
            if (nativeAd.starRating == null) {
                adView.starRatingView!!.visibility = View.INVISIBLE
            } else {
                (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
                adView.starRatingView!!.visibility = View.VISIBLE
            }
        }
        if (adView.advertiserView != null) {
            if (nativeAd.advertiser == null) {
                adView.advertiserView!!.visibility = View.INVISIBLE
            } else {
                (adView.advertiserView as TextView).text = nativeAd.advertiser
                adView.advertiserView!!.visibility = View.VISIBLE
            }
        }

        adView.setNativeAd(nativeAd)
    }
}