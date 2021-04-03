package com.belsoft.themoviedbapp.models.api

data class MovieDbResponseModel (
    val page : Int,
    val results : List<Results>,
    val total_pages : Int,
    val total_results : Int
)