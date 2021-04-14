package com.belsoft.themoviedbapp.ui.search

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.belsoft.themoviedbapp.BaseViewModel
import com.belsoft.themoviedbapp.MainViewModel
import com.belsoft.themoviedbapp.models.SearchSelectItemModel
import com.belsoft.themoviedbapp.services.IRequestHelper

class SearchViewModel( val mainViewModel: MainViewModel,
                       val requestHelper : IRequestHelper,
                      private val appContext: Application
) : BaseViewModel() {

    var searchSelectItemsListForSearch = mutableListOf<SearchSelectItemModel>()
    val searchSelectItems = MutableLiveData<List<SearchSelectItemModel>>().apply { value = listOf() }


}