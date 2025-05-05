package pion.tech.pionbase.home.data.model

import com.google.gson.annotations.SerializedName
import pion.tech.pionbase.main.domain.model.ApiObjectResponseData

data class ApiObjectResponseEntity<T>(
    @SerializedName("message") var message: String,
    @SerializedName("data") var dataResponse: T,
    @SerializedName("status") var status: Int
)

fun <T> ApiObjectResponseEntity<T>.toDomain(): ApiObjectResponseData<T> =
    ApiObjectResponseData(
        message = this.message,
        dataResponse = this.dataResponse,
        status = this.status
    )