package pion.tech.pionbase.home.data.model.template

import com.google.gson.annotations.SerializedName

data class TemplateModelEntity(
    val id: String,
    val name: String,
    val status: Boolean,
    @SerializedName("is_pro")
    val isPro: Boolean? = null,
    @SerializedName("custom_fields")
    val customField: TemplateEntity
)


