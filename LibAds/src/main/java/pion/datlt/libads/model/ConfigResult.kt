package pion.datlt.libads.model

data class ConfigResult(
    val timeDelayNative: Long = 4000L,
    val disableAllConfig: Boolean = false,
    val isOpenAppOn: Boolean = true,
    val isInterstitialOn: Boolean = true,
    val isNativeOn: Boolean = true,
    val isNativeFullScreenOn: Boolean = true,
    val isBannerOn: Boolean = true,
    val isBannerAdaptiveOn: Boolean = true,
    val isBannerLargeOn: Boolean = true,
    val isBannerInlineOn: Boolean = true,
    val isBannerCollapsibleOn: Boolean = true,
    val isRewardVideoOn: Boolean = true,
    val isRewardInter: Boolean = true,
    val isPreloadOnBoardAtLanguage: Boolean = false,
    val timeShowDialogChangeLanguage: Long = 4000L,
    val listConfig: List<ConfigAds> = listOf(),
)
