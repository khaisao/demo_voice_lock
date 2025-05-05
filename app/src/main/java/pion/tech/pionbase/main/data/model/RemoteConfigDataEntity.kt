package pion.tech.pionbase.main.data.model

import pion.tech.pionbase.main.domain.model.RemoteConfigData

data class RemoteConfigDataEntity(
    val configShowAds: String,
    val admobId: String,
    val isRealData: Boolean
)

fun RemoteConfigDataEntity.toDomain() =
    RemoteConfigData(
        configShowAds = this.configShowAds,
        isRealData = this.isRealData,
        admobId = this.admobId,
    )
