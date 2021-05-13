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
import com.belsoft.themoviedbapp.services.IRequestHelper
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class SearchViewModel(private val mainViewModel: IMainViewModel,
                      private val requestHelper: IRequestHelper) : BaseViewModel() {

    private var job: Job? = null
    private val _searchSelectItems = MutableLiveData<List<SearchSelectItemModel>>()
    val searchSelectItems: LiveData<List<SearchSelectItemModel>> = _searchSelectItems

    fun setSearchSelectItems(searchSelectItems: List<SearchSelectItemModel>) {
        _searchSelectItems.value = searchSelectItems
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    fun search(search: Flow<String>): Flow<MovieDbResponseModel> =
        search.debounce(1000).mapLatest {
            requestHelper.getMovieDbSearch(API_KEY, it)
        }

    fun search(search: String) {
        job?.cancel()

        job = viewModelScope.launch {
            ensureActive()
            if (!requestHelper.hasInternetConnection()) {
                mainViewModel.toastMessage.value = R.string.no_internet_connection
                return@launch
            }

            if (search.isEmpty()) {
                _searchSelectItems.value = listOf()
                return@launch
            }

            isVisibleProgressBar.value = true
            val result = withContext(Dispatchers.IO) {
                requestHelper.getMovieDbSearch(API_KEY, search)
            }

            result.let {
                val itemList = result.results.map {
                    it.asViewModel()
                }
                _searchSelectItems.value = itemList
            } ?: run {
                mainViewModel.toastMessage.value = R.string.something_went_wrong
            }
            isVisibleProgressBar.value = false
        }
    }

    fun clear() {
        _searchSelectItems.value = listOf()
    }
}