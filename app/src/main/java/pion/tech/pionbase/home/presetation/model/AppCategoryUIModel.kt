package pion.tech.pionbase.home.presetation.model

import pion.tech.pionbase.home.domain.model.AppCategoryData

data class AppCategoryUIModel(
    val id: String,
    val name: String
)

fun AppCategoryData.toPresentation(): AppCategoryUIModel =
    AppCategoryUIModel(id = this.id, name = this.name)