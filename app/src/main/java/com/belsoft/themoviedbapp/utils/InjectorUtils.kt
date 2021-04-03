package com.belsoft.themoviedbapp.utils

import android.content.Context
import com.belsoft.themoviedbapp.MainViewModel
import com.belsoft.themoviedbapp.services.RequestHelper
import com.belsoft.themoviedbapp.ui.search.SearchViewModel

class InjectorUtils private constructor(private val appContext: Context) {

    companion object {
        @Volatile
        private var instance: InjectorUtils? = null

        fun getInstance(appContext: Context): InjectorUtils {
            return instance ?: synchronized(this) {
                instance ?: InjectorUtils(appContext).also { instance = it }
            }
        }
    }

    private val requestHelper = RequestHelper.getInstance(appContext)

    fun provideMainViewModelFactory(): ViewModelFactory<MainViewModel> {
        return ViewModelFactory {
            MainViewModel()
        }
    }

    fun provideSearchViewModelFactory(mainViewModel: MainViewModel): ViewModelFactory<SearchViewModel> {
        return ViewModelFactory {
            SearchViewModel(mainViewModel, requestHelper, appContext)
        }
    }
}