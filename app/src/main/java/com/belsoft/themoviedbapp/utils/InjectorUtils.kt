package com.belsoft.themoviedbapp.utils

import android.content.Context
import com.belsoft.themoviedbapp.MainViewModel
import com.belsoft.themoviedbapp.services.RequestHelper
import com.belsoft.themoviedbapp.ui.search.SearchViewModel

class InjectorUtils private constructor(context: Context) {

    companion object {
        @Volatile
        private var instance: InjectorUtils? = null

        fun getInstance(context: Context): InjectorUtils {
            return instance ?: synchronized(this) {
                instance ?: InjectorUtils(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }

    private val requestHelper = RequestHelper(context)

    fun provideMainViewModelFactory(): ViewModelFactory<MainViewModel> {
        return ViewModelFactory {
            MainViewModel(requestHelper)
        }
    }

    fun provideSearchViewModelFactory(mainViewModel: MainViewModel): ViewModelFactory<SearchViewModel> {
        return ViewModelFactory {
            SearchViewModel(mainViewModel, requestHelper)
        }
    }
}