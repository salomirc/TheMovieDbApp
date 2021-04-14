package com.belsoft.themoviedbapp

import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.belsoft.themoviedbapp.utils.InjectorUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

abstract class BaseFragment : Fragment() {

    lateinit var mainViewModel: MainViewModel
    var fgmView: View? = null
    val localScope: CoroutineScope = MainScope()
    var isRunning: Boolean = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Get the MainViewModelFactory with all of it's dependencies constructed
        val factory = InjectorUtils.getInstance(requireActivity().applicationContext as Application).provideMainViewModelFactory()

        // Use ViewModelProviders class to create / get already created MainViewModelFactory
        mainViewModel = ViewModelProvider(requireActivity(), factory).get(MainViewModel::class.java)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fgmView = null
        localScope.cancel()
    }
}