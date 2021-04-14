package com.belsoft.themoviedbapp.ui.search

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.belsoft.themoviedbapp.MainActivity
import com.belsoft.themoviedbapp.MainActivity.Companion.hideSoftKeyboard
import com.belsoft.themoviedbapp.MainActivity.Companion.showSoftKeyboard
import com.belsoft.themoviedbapp.R
import com.belsoft.themoviedbapp.adapters.SearchListAdapter
import com.belsoft.themoviedbapp.api.API_KEY
import com.belsoft.themoviedbapp.components.HideKeyboardReadyFragment
import com.belsoft.themoviedbapp.databinding.SearchFragmentBinding
import com.belsoft.themoviedbapp.models.api.MovieDbResponseModel
import com.belsoft.themoviedbapp.models.asViewModel
import com.belsoft.themoviedbapp.utils.InjectorUtils
import kotlinx.coroutines.*

class SearchFragment : HideKeyboardReadyFragment() {

    private lateinit var viewModel: SearchViewModel
    private var _binding: SearchFragmentBinding? = null
    private var binding: SearchFragmentBinding
        get() = _binding!!
        set(value) { _binding = value }

    override var searchListRecyclerViewHide: RecyclerView? = null
    override var searchViewHide: SearchView? = null

    companion object {
        val TAG = SearchFragment::class.simpleName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.search_fragment, container, false)
        binding.lifecycleOwner = this
        return binding.root.apply {
            searchListRecyclerViewHide = binding.recyclerView
            searchViewHide = binding.searchView
            binding.backImageButton.visibility = View.INVISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        searchListRecyclerViewHide = null
        searchViewHide = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setArchitectureComponents()
        initializeUI()
    }

    private fun setArchitectureComponents() {

        // Get the LoginViewModelFactory with all of it's dependencies constructed
        val factory = InjectorUtils.getInstance(requireActivity().applicationContext as Application).
        provideSearchViewModelFactory(mainViewModel)

        // Use ViewModelProviders class to create/get already created ViewModel
        viewModel = ViewModelProvider(this, factory).get(SearchViewModel::class.java)

        // Assign the component to a property in the binding class.
        binding.viewmodel = viewModel
    }

    private fun initializeUI() {

        // Plug in the linear layout manager:
        val layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.layoutManager = layoutManager

        // Plug in my adapter:
        val adapter = SearchListAdapter {

        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)

        //Set Observer for RecyclerView source list
        viewModel.searchSelectItems.observe(viewLifecycleOwner, Observer { itemList ->
            // Update the cached copy of the words in the adapter.
            itemList?.let { adapter.submitList(it) }
        })

        binding.backImageButton.setOnClickListener {
            if (isRunning)
                return@setOnClickListener
            isRunning = true
            if (MainActivity.isKeyboardOnScreen()){
                hideSoftKeyboard(binding.searchView)
                binding.searchView.clearFocus()
            }
            activity!!.onBackPressed()
            isRunning = false
        }

        searchViewHide?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            private var job: Job? = null
            private var searchFor = ""

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                val searchText = newText.toString().trim()
                searchFor = searchText

                if (searchFor == ""){
                    viewModel.searchSelectItems.value = mutableListOf()
                    cancelJob()
                    viewModel.isVisibleProgressBar.value = false
                    return false
                }

                viewModel.viewModelScope.launch {
                    delay(500)  //debounce timeOut
                    if (searchFor != searchText)
                        return@launch

                    // do our magic here
                    cancelJob()

                    job = launch {
                        try {
                            getData(searchFor)
                        }
                        catch (e: CancellationException) {
                            Log.d(TAG, "Coroutine cancelled - ${e.message}")
                        }
                    }
                }
                return false
            }

            private fun cancelJob() {
                job?.let { it ->
                    if (it.isActive) it.cancel()
                }
            }
        })


        searchViewHide?.setOnQueryTextFocusChangeListener { v, hasFocus ->
            if (hasFocus){
                if (v != null) showSoftKeyboard(v.findFocus())
            }
        }

        searchViewHide?.requestFocus()

    }

    private suspend fun getData(search: String) {
        if (viewModel.requestHelper.hasInternetConnection()) {
            viewModel.isVisibleProgressBar.value = true

            val result: MovieDbResponseModel? = withContext(Dispatchers.IO) {
                viewModel.requestHelper.getMovieDbSearch(API_KEY, search)
            }

            if (result != null) {
                val itemList = result.results.map {
                    it.asViewModel()
                }
                viewModel.searchSelectItems.value = itemList
                viewModel.isVisibleProgressBar.value = false
            } else {
                viewModel.mainViewModel.toastMessage.value = R.string.something_went_wrong
            }
        }
        else {
            viewModel.mainViewModel.toastMessage.value = R.string.no_internet_connection
        }
    }
}