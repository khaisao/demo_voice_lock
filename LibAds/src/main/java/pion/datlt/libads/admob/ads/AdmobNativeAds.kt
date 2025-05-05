package pion.datlt.libads.admob.ads

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.Lifecycle
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
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

class AdmobNativeAds : AdmobAds() {

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
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(
                "TESTERADSEVENT",
                "start load native : ads name ${adsChild.spaceName} id ${adsChild.adsId}"
            )
            stateLoadAd = StateLoadAd.LOADING
            val id = if (AdsConstant.isDebug) AdsConstant.ID_ADMOB_NATIVE_TEST else adsChild.adsId
            mAdsChild = adsChild
            error = ""


            val videoOptions = VideoOptions.Builder().setStartMuted(true).build()
            val adOptions = NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .setAdChoicesPlacement(
                    adChoice ?: NativeAdOptions.ADCHOICES_TOP_RIGHT
                ) //set vị trí của ADCHOICES button
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
                            putString("ad_unit_id", adUnitId)
                            putInt("precision_type", adValue.precisionType)
                            putLong("revenue_micros", adValue.valueMicros)
                            putString("ad_source_id", adSourceId)
                            putString("ad_source_name", adSourceName)
                            putString("ad_type", AdDef.ADS_TYPE_ADMOB.NATIVE)
                            putString("currency_code", adValue.currencyCode)
                        }
                        mAdCallback?.onPaidEvent(bundle)
                    }

                }
                .withAdListener(object : AdListener() {
                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        Log.d(
                            "TESTERADSEVENT",
                            "load success native : ads name ${adsChild.spaceName} id ${adsChild.adsId}"
                        )
                        timeLoader = Date().time



                        if (isPreload) {
                            mPreloadCallback?.onLoadDone()
                        }
                    }

                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        //gọi khi ad load failed
                        Log.d(
                            "TESTERADSEVENT",
                            "load failed native : ads name ${adsChild.spaceName} id ${adsChild.adsId} error : ${adError.message}"
                        )
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
                            "click native : ads name ${adsChild.spaceName} id ${adsChild.adsId}"
                        )
                    }

                })
                .withNativeAdOptions(adOptions)
                .build()

            withContext(Dispatchers.Main) {
                adLoader.loadAd(AdRequest.Builder().build())
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
        val startTime = System.currentTimeMillis()
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
                    Log.d(
                        "TESTERADSEVENT",
                        "show failed native : ads name ${adsChild.spaceName} id ${adsChild.adsId} error : show in wrong destination"
                    )

                } else {
                    nativeAds?.let {
                        if (reshowAdsRunnable != null) {
                            kotlin.runCatching {
                                handler.removeCallbacks(reshowAdsRunnable!!)
                            }
                        }
                        reshowAdsRunnable = null
                        populateUnifiedNativeAdView(
                            it,
                            nativeAdView,
                            layoutToAttachAds,
                            viewAdsInflateFromXml,
                            timeShowNativeCollapsibleAfterClose ?: AdsConstant.RESHOW_NATIVE_TIME,
                            adCallback
                        )
                        //clear các view con nằm trong adsViewGroup
                        layoutToAttachAds.removeAllViews()
                        layoutToAttachAds.addView(nativeAdView)
                        stateLoadAd = StateLoadAd.HAS_BEEN_OPENED
                        CommonUtils.showToastDebug(
                            activity,
                            "Admob Native id: ${adsChild.adsId}"
                        )
                        mAdCallback?.onAdShow()
                    }
                    Log.d(
                        "TESTERADSEVENT",
                        "show success native : ads name ${adsChild.spaceName} id ${adsChild.adsId}"
                    )

                }
            } else {
                CommonUtils.showToastDebug(activity, "viewAdsInflateFromXml native not null")
                Log.d(
                    "TESTERADSEVENT",
                    "show failed native : ads name ${adsChild.spaceName} id ${adsChild.adsId} error : viewAdsInflateFromXml native not null"
                )
            }
        } else {
            CommonUtils.showToastDebug(activity, "layoutToAttachAds native not null")
            Log.d(
                "TESTERADSEVENT",
                "show failed native : ads name ${adsChild.spaceName} id ${adsChild.adsId} error : layoutToAttachAds native not null"
            )

        }
        Log.d("CHECKSHOWTIMENATIVE", "show: ${startTime - System.currentTimeMillis()}")
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

    private fun populateUnifiedNativeAdView(
        nativeAd: NativeAd,
        adView: NativeAdView,
        layoutToAttachAds: ViewGroup,
        viewAdsInflateFromXml: View,
        timeShowNativeCollapsibleAfterClose: Int,
        adCallback: AdCallback? = null
    ) {
        bindMediaView(
            nativeAd,
            adView,
            layoutToAttachAds,
            viewAdsInflateFromXml,
            timeShowNativeCollapsibleAfterClose,
            adCallback
        )
        bindHeadLineView(nativeAd, adView)
        bindBodyView(nativeAd, adView)
        bindIconView(nativeAd, adView)
        bindCTAView(nativeAd, adView)
        bindPriceView(nativeAd, adView)
        bindStoreView(nativeAd, adView)
        bindRatingView(nativeAd, adView)
        bindAdvertiseView(nativeAd, adView)
        adView.setNativeAd(nativeAd)
    }

    @SuppressLint("CutPasteId")
    private fun bindMediaView(
        nativeAd: NativeAd,
        adView: NativeAdView,
        layoutToAttachAds: ViewGroup,
        viewAdsInflateFromXml: View,
        timeShowNativeCollapsibleAfterClose: Int,
        adCallback: AdCallback? = null
    ) {
        if (nativeAd.mediaContent == null) return //qc nay khong co media view val ratio : Float = nativeAds.mediaContent!!.aspectRatio
        val viewGroup = adView.findViewById<ViewGroup>(R.id.ad_media)
        //neu co media view, hoac khong phai template collapsible thi chay code cu
        val viewTag = viewAdsInflateFromXml.tag
        isCollapsed = false


        if (viewTag != "collapsible") {

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
                if (adView.mediaView != null && nativeAd.mediaContent != null) {
                    adView.mediaView!!.mediaContent = nativeAd.mediaContent!!
                }
                return
            }

            return
        }
        closeOtherMediaView(layoutToAttachAds)
        val adsRatio = nativeAd.mediaContent!!.aspectRatio
        val ratio = if (adsRatio < 1.5) {
            1.5f
        } else {
            adsRatio
        }
        val layoutAds = layoutToAttachAds.parent as ViewGroup
        val rootView = layoutAds.parent as ConstraintLayout


        //check xem neu chua co id thi them id
        for (i in 0 until rootView.childCount) {
            val childView = rootView.getChildAt(i)
            if (childView.id == View.NO_ID) {
                val newId = View.generateViewId()
                childView.id = newId
            }
        }

        //dau tien la phai xoa di thang container truoc do neu co

        //tao 1 frame layout
        val mediaContainer = FrameLayout(layoutToAttachAds.context).apply {
            id = R.id.ad_media_container
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                0
            ).apply {
                dimensionRatio = ratio.toString()
            }
        }
        mediaContainer.background =
            viewAdsInflateFromXml.findViewById<ViewGroup>(R.id.adViewHolder).background
        rootView.addView(mediaContainer)
        val constraintSet = ConstraintSet().apply {
            clone(rootView)
            connect(mediaContainer.id, ConstraintSet.BOTTOM, layoutAds.id, ConstraintSet.TOP)
        }
        constraintSet.applyTo(rootView)

        //tao 1 mediaview trong media container
        val margin = convertDpToPx(mediaContainer.context, 8f).toInt()
        val mediaViewGroup = FrameLayout(mediaContainer.context).apply {
            id = R.id.ad_media
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            ).apply {
                setMargins(margin, margin * 2, margin, 0)
            }
        }
        mediaContainer.addView(mediaViewGroup)
        //bind qc vao mediaview group
        val mediaView = MediaView(adView.context)
        mediaViewGroup.addView(
            mediaView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        adView.mediaView = mediaView
        if (adView.mediaView != null && nativeAd.mediaContent != null) {
            adView.mediaView!!.mediaContent = nativeAd.mediaContent!!
        }


        //them button de close qc
        val buttonClose = CardView(mediaContainer.context).apply {
            id = View.generateViewId()
            layoutParams = FrameLayout.LayoutParams(
                convertDpToPx(mediaContainer.context, 36f).toInt(),
                convertDpToPx(mediaContainer.context, 36f).toInt()
            ).apply {
                setMargins(margin, margin, 0, 0)
            }
        }
        buttonClose.background =
            AppCompatResources.getDrawable(mediaContainer.context, R.drawable.bg_radius_20)
        buttonClose.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
        buttonClose.cardElevation = convertDpToPx(buttonClose.context, 8f)
        buttonClose.radius = convertDpToPx(buttonClose.context, 20f)
        mediaContainer.addView(buttonClose)
        buttonClose.setOnClickListener {
            reshowAdsRunnable = Runnable {
                try {
                    isCollapsed = false
                    mediaContainer.visibility = View.VISIBLE
                    layoutToAttachAds.visibility = View.VISIBLE
                    layoutAds.visibility = View.VISIBLE
                } catch (e: Exception) {
                    kotlin.runCatching {
                        mediaContainer.visibility = View.GONE
                    }
                    isCollapsed = true
                }
            }
            handler.postDelayed(reshowAdsRunnable!!, timeShowNativeCollapsibleAfterClose * 1000L)
            isCollapsed = true
            mediaContainer.visibility = View.GONE
            adCallback?.onClickCloseCollapsible()
        }

        val imageView = ImageView(mediaContainer.context).apply {
            id = View.generateViewId()
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        imageView.setImageResource(R.drawable.ic_close_collapsible)
        buttonClose.addView(imageView)


        //đoạn code này dùng trong trường hợp
        //khi aarn view quảng cáo thì colapsible cũng sẽ đóng luôn
        layoutToAttachAds.viewTreeObserver.addOnGlobalLayoutListener(object :
            OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (isCollapsed) {
                    mediaContainer.visibility = View.GONE
                    layoutAds.viewTreeObserver.removeOnGlobalLayoutListener(this)
                } else {
                    mediaContainer.visibility = layoutToAttachAds.visibility
                }
            }
        })

    }

    private fun bindHeadLineView(nativeAd: NativeAd, adView: NativeAdView) {
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        if (adView.headlineView == null || nativeAd.headline == null) return
        if (AdsConstant.isDebug) {
            (adView.headlineView as TextView).text = mAdsChild?.spaceName ?: "null"
        } else {
            (adView.headlineView as TextView).text = nativeAd.headline
        }


    }

    private fun bindBodyView(nativeAd: NativeAd, adView: NativeAdView) {
        adView.bodyView = adView.findViewById(R.id.ad_body)
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
    }

    private fun bindIconView(nativeAd: NativeAd, adView: NativeAdView) {
        val viewGroupIcon = adView.findViewById<View>(R.id.ad_app_icon)
        if (viewGroupIcon != null) {
            if (viewGroupIcon is ViewGroup) { //xử
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

    }

    private fun bindCTAView(nativeAd: NativeAd, adView: NativeAdView) {
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
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
    }

    private fun bindPriceView(nativeAd: NativeAd, adView: NativeAdView) {
        adView.priceView = adView.findViewById(R.id.ad_price)
        if (adView.priceView != null) {
            if (nativeAd.price == null) {
                adView.priceView!!.visibility = View.INVISIBLE
            } else {
                adView.priceView!!.visibility = View.VISIBLE
                (adView.priceView as TextView).text = nativeAd.price
            }
        }
    }

    private fun bindRatingView(nativeAd: NativeAd, adView: NativeAdView) {
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        if (adView.starRatingView != null) {
            if (nativeAd.starRating == null) {
                adView.starRatingView!!.visibility = View.INVISIBLE
            } else {
                (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
                adView.starRatingView!!.visibility = View.VISIBLE
            }
        }
    }

    private fun bindStoreView(nativeAd: NativeAd, adView: NativeAdView) {
        adView.storeView = adView.findViewById(R.id.ad_store)
        if (adView.storeView != null) {
            if (nativeAd.store == null) {
                adView.storeView!!.visibility = View.INVISIBLE
            } else {
                adView.storeView!!.visibility = View.VISIBLE
                (adView.storeView as TextView).text = nativeAd.store
            }
        }
    }

    private fun bindAdvertiseView(nativeAd: NativeAd, adView: NativeAdView) {
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)
        if (adView.advertiserView != null) {
            if (nativeAd.advertiser == null) {
                adView.advertiserView!!.visibility = View.INVISIBLE
            } else {
                (adView.advertiserView as TextView).text = nativeAd.advertiser
                adView.advertiserView!!.visibility = View.VISIBLE
            }
        }
    }

    private fun convertDpToPx(context: Context, dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        )
    }

    private fun closeOtherMediaView(layoutToAttachAds: ViewGroup) {
        val layoutAds = layoutToAttachAds.parent as ViewGroup
        val rootView = layoutAds.parent as ConstraintLayout
        val oldContainerView = rootView.findViewById<ViewGroup>(R.id.ad_media_container)
        if (oldContainerView != null) {
            rootView.removeView(oldContainerView)
        }
    }

    companion object {
        var isCollapsed = false
        private var handler = Handler(Looper.getMainLooper())
        private var reshowAdsRunnable: Runnable? = null
    }

}