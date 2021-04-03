package com.belsoft.themoviedbapp.models

import com.belsoft.themoviedbapp.models.api.Results


data class SearchSelectItemModel
    (
    val title: String,
    val id: String? = null,
    var isSelected: Boolean = false
)

fun Results.asViewModel(): SearchSelectItemModel {
    return SearchSelectItemModel(
        original_title
    )
}