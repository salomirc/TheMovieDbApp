package com.belsoft.themoviedbapp.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.belsoft.themoviedbapp.BaseViewModel
import com.belsoft.themoviedbapp.IMainViewModel
import com.belsoft.themoviedbapp.R
import com.belsoft.themoviedbapp.api.API_KEY
import com.belsoft.themoviedbapp.models.SearchSelectItemModel
import com.belsoft.themoviedbapp.models.api.MovieDbResponseModel
import com.belsoft.themoviedbapp.models.asViewModel
import com.belsoft.themoviedbapp.services.ConnectionLiveData
import com.belsoft.themoviedbapp.services.IRequestHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(private val mainViewModel: IMainViewModel,
                      private val requestHelper: IRequestHelper) : BaseViewModel() {

    var isInitialised = false

    val connectionLiveData: ConnectionLiveData
        get() = requestHelper.connectionLiveData

    private val _searchSelectItems = MutableLiveData<List<SearchSelectItemModel>>()
    val searchSelectItems: LiveData<List<SearchSelectItemModel>> = _searchSelectItems

    fun setSearchSelectItems(searchSelectItems: List<SearchSelectItemModel>) {
        _searchSelectItems.postValue(searchSelectItems)
    }

    fun initialise() {
        viewModelScope.launch {
            getData("bean").also {
                if (it) {
                    isInitialised = true
                }
            }
        }
    }

    suspend fun getData(search: String): Boolean {
        var isSuccessful = false
        if (!connectionLiveData.hasInternetConnectivity) {
            mainViewModel.toastMessage.value = R.string.no_internet_connection
            return false
        }

        isVisibleProgressBar.value = true
        val result: MovieDbResponseModel? = withContext(Dispatchers.IO) {
            requestHelper.getMovieDbSearch(API_KEY, search)
        }

        result?.let {
            val itemList = result.results.map {
                it.asViewModel()
            }
            _searchSelectItems.value = itemList
            isSuccessful = true
        } ?: run {
            mainViewModel.toastMessage.value = R.string.something_went_wrong
        }
        isVisibleProgressBar.value = false
        return isSuccessful
    }
}