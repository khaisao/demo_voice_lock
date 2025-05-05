package pion.datlt.libads.model

import android.content.Context
import android.view.LayoutInflater
import com.google.gson.annotations.SerializedName
import pion.datlt.libads.R
import pion.datlt.libads.utils.AdDef
import pion.datlt.libads.utils.AdsConstant

class ConfigAds {
    @SerializedName("nameConfig")
    val nameConfig: String = ""

    @SerializedName("isOn")
    val isOn: Boolean = false

    @SerializedName("type")
    val type: String = "interstitial"


    @SerializedName("network")
    val network: String = AdDef.NETWORK.GOOGLE


    //inter

    @SerializedName("timeDelayShowInter")
    val timeDelayShowInter: Int? = null

    @SerializedName("isShowNativeAfterInter")
    val isShowNativeAfterInter: Boolean = false


    //native

    @SerializedName("ctaGradientListColor")
    val ctaGradientListColor: List<String>? = null

    @SerializedName("ctaColor")
    val ctaColor: String = "#2F8FE6"

    @SerializedName("textCTAColor")
    val textCTAColor: String = "#FFFFFF"

    @SerializedName("ctaRatio")
    val ctaRatio: String? = null

    @SerializedName("ctaConnerRadius")
    val ctaConnerRadius: Int = 10

    @SerializedName("layoutTemplate")
    val layoutTemplate: String = small_icon_ctaright

    @SerializedName("backGroundColor")
    val backGroundColor: String = "#E8E6E6"

    @SerializedName("textContentColor")
    val textContentColor: String = "#444444"

    @SerializedName("isPreloadAfterShow")
    val isPreloadAfterShow: Boolean = false

    @SerializedName("isCloseWhenClick")
    val isCloseWhenClick: Boolean = false

    @SerializedName("isCloseWhenClickCollapsible")
    val isCloseWhenClickCollapsible: Boolean = true

    @SerializedName("timeShowNativeCollapsibleAfterClose")
    val timeShowNativeCollapsibleAfterClose: Int = 5


    fun getConfigNative(
        context: Context?,
        default: ConfigNative
    ): ConfigNative {
        val adChoice: Int
        val viewAds = when (this.layoutTemplate) {


            small_icon_ctaright -> {
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_small_icon_ctaright, null)
                }
            }


            small_ctaright -> {
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_small_ctaright, null)
                }
            }

            Medium1_icontop_ctabot -> {
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_medium1_icontop_ctabot, null)
                }
            }

            medium3_icon_ctabot -> {
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_medium3_icon_ctabot, null)
                }
            }

            medium3_ctabot -> {
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_medium3_ctabot, null)
                }
            }

            Medium2_icon_ctatop -> {
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_medium2_icon_ctatop, null)
                }
            }

            Medium2_icon_ctabot -> {
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_medium2_icon_ctabot, null)
                }
            }

            medium3_ctatop -> {
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_medium3_ctatop, null)
                }
            }

            Larger_iconbot_cta_bot -> {
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_larger_iconbot_cta_bot, null)
                }
            }

            Larger_icontop_ctabot -> {
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_larger_icontop_ctabot, null)
                }
            }

            Larger_iconframe_cta_bot -> {
                adChoice = AdsConstant.TOP_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_larger_iconframe_cta_bot, null)
                }
            }

            Medium1_icontop_ctabot_collapsible -> {
                adChoice = AdsConstant.BOTTOM_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_medium1_icontop_ctabot_collapsible, null)
                }
            }

            medium2_icon_ctabot_collapsible -> {
                adChoice = AdsConstant.BOTTOM_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_medium2_icon_ctabot_collapsible, null)
                }
            }

            medium2_ctabot_collapsible -> {
                adChoice = AdsConstant.BOTTOM_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_medium2_ctabot_collapsible, null)
                }
            }

            small_icon_ctaright_collapsible -> {
                adChoice = AdsConstant.BOTTOM_LEFT
                if (context == null) {
                    null
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.layout_native_small_icon_ctaright_collapsible, null)
                }
            }


            else -> {
                adChoice = default.adChoice
                default.viewAds
            }
        }
        return ConfigNative(
            adChoice = adChoice,
            viewAds = viewAds
        )
    }

    companion object {
        //small
        const val small_icon_ctaright = "small_icon_ctaright"
        const val small_ctaright = "small_ctaright"


        //medium
        const val Medium1_icontop_ctabot = "Medium1_icontop_ctabot"
        const val medium3_icon_ctabot = "medium3_icon_ctabot"
        const val medium3_ctabot = "medium3_ctabot"
        const val Medium2_icon_ctatop = "Medium2_icon_ctatop"
        const val Medium2_icon_ctabot = "Medium2_icon_ctabot"
        const val medium3_ctatop = "medium3_ctatop"


        //large
        const val Larger_iconbot_cta_bot = "Larger_iconbot_cta_bot"
        const val Larger_icontop_ctabot = "Larger_icontop_ctabot"
        const val Larger_iconframe_cta_bot = "Larger_iconframe_cta_bot"

        //collapsible
        const val Medium1_icontop_ctabot_collapsible = "Medium1_icontop_ctabot_collapsible"
        const val medium2_icon_ctabot_collapsible = "medium2_icon_ctabot_collapsible"
        const val medium2_ctabot_collapsible = "medium2_ctabot_collapsible"
        const val small_icon_ctaright_collapsible = "small_icon_ctaright_collapsible"

    }
}