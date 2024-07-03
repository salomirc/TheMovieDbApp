package com.belsoft.themoviedbapp.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.belsoft.themoviedbapp.MainActivity
import com.belsoft.themoviedbapp.MainActivity.Companion.hideSoftKeyboard
import com.belsoft.themoviedbapp.MainActivity.Companion.showSoftKeyboard
import com.belsoft.themoviedbapp.R
import com.belsoft.themoviedbapp.adapters.SearchListAdapter
import com.belsoft.themoviedbapp.components.HideKeyboardReadyFragment
import com.belsoft.themoviedbapp.databinding.SearchFragmentBinding
import com.belsoft.themoviedbapp.models.asViewModel
import com.belsoft.themoviedbapp.utils.InjectorUtils
import com.belsoft.themoviedbapp.utils.onQueryTextChange
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class SearchFragment : HideKeyboardReadyFragment() {

    private lateinit var viewModel: SearchViewModel
    private var _binding: SearchFragmentBinding? = null
    private val binding: SearchFragmentBinding
        get() = _binding!!

    override val searchListRecyclerViewHide: RecyclerView
        get() = binding.recyclerView

    override val searchViewHide: SearchView
        get() = binding.searchView

    companion object {
        val TAG = SearchFragment::class.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = InjectorUtils.getInstance.provideSearchViewModelFactory(mainViewModel)
        viewModel = ViewModelProvider(this, factory).get(SearchViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.search_fragment, container, false)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        return binding.root.apply {
            binding.backImageButton.visibility = View.INVISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initializeUI()
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    private fun initializeUI() {

        val adapter = SearchListAdapter { }

        binding.apply {
            recyclerView.apply {
                // Plug in the linear layout manager:
                layoutManager = LinearLayoutManager(activity)

                // Plug in my adapter:
                this.adapter = adapter
                setHasFixedSize(true)
                //Set Observer for RecyclerView source list
//                viewModel.searchSelectItems.observe(viewLifecycleOwner, Observer { itemList ->
//                    // Update the cached copy of the words in the adapter.
//                    itemList?.let { (adapter as SearchListAdapter).submitList(it) }
//                })
            }

            backImageButton.setOnClickListener {
                if (isRunning)
                    return@setOnClickListener
                isRunning = true
                if (MainActivity.isKeyboardOnScreen()) {
                    hideSoftKeyboard(binding.searchView)
                    binding.searchView.clearFocus()
                }
                requireActivity().onBackPressed()
                isRunning = false
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.search(searchViewHide.onQueryTextChange()).collect { movieDbResponseModel ->
                adapter.submitList(movieDbResponseModel.results.map {
                    it.asViewModel()
                })
            }
        }

//        lifecycleScope.launch {
//            searchViewHide.
//            onQueryTextChange().
//            onEach {
//                if (it.isEmpty()) {
//                    viewModel.clear()
//                }
//            }.
//            debounce(1000).
//            filter { it.isNotEmpty() }.
//            collect { viewModel.search(it) }
//        }


        searchViewHide.setOnQueryTextFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                if (v != null) showSoftKeyboard(v.findFocus())
            }
        }
        searchViewHide.requestFocus()
    }
}