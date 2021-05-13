package com.belsoft.themoviedbapp.utils

import android.widget.SearchView
import com.belsoft.themoviedbapp.ui.search.SearchViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@ExperimentalCoroutinesApi
fun SearchView.onQueryTextChange(): Flow<String> = callbackFlow {
    setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean {
            return false
        }

        override fun onQueryTextChange(newText: String): Boolean {
            offer(newText)
            return false
        }

    })
//    this.send("Panther")
    awaitClose { setOnQueryTextListener(null) }
}