package com.belsoft.themoviedbapp.utils

import android.app.Application
import com.belsoft.themoviedbapp.MainViewModel
import com.belsoft.themoviedbapp.services.RequestHelper
import com.belsoft.themoviedbapp.ui.search.SearchViewModel

class InjectorUtils private constructor(appContext: Application) {

    companion object {
        @Volatile
        private var instance: InjectorUtils? = null

        @Volatile
        lateinit var getInstance: InjectorUtils

        fun getInstance(appContext: Application): InjectorUtils {
            return instance ?: synchronized(this) {
                instance ?: InjectorUtils(appContext).also {
                    instance = it
                    getInstance = it
                }
            }
        }
    }

    private val requestHelper = RequestHelper(appContext)

    fun provideMainViewModelFactory(): ViewModelFactory<MainViewModel> {
        return ViewModelFactory {
            MainViewModel()
        }
    }

    fun provideSearchViewModelFactory(mainViewModel: MainViewModel): ViewModelFactory<SearchViewModel> {
        return ViewModelFactory {
            SearchViewModel(mainViewModel, requestHelper)
        }
    }
}