package com.belsoft.themoviedbapp.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.belsoft.themoviedbapp.BaseViewModel
import com.belsoft.themoviedbapp.IMainViewModel
import com.belsoft.themoviedbapp.R
import com.belsoft.themoviedbapp.api.API_KEY
import com.belsoft.themoviedbapp.models.SearchSelectItemModel
import com.belsoft.themoviedbapp.models.api.MovieDbResponseModel
import com.belsoft.themoviedbapp.models.asViewModel
import com.belsoft.themoviedbapp.services.IRequestHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchViewModel(private val mainViewModel: IMainViewModel,
                      private val requestHelper: IRequestHelper) : BaseViewModel() {

    private val _searchSelectItems = MutableLiveData<List<SearchSelectItemModel>>()
    val searchSelectItems: LiveData<List<SearchSelectItemModel>> = _searchSelectItems

    fun setSearchSelectItems(searchSelectItems: List<SearchSelectItemModel>) {
        _searchSelectItems.postValue(searchSelectItems)
    }

    suspend fun getData(search: String) {
        if (!requestHelper.hasInternetConnection) {
            mainViewModel.toastMessage.value = R.string.no_internet_connection
            return
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
            isVisibleProgressBar.value = false
        } ?: run {
            mainViewModel.toastMessage.value = R.string.something_went_wrong
        }
    }
}