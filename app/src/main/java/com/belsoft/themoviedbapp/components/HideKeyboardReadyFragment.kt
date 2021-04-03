package com.belsoft.themoviedbapp.components

import android.os.Bundle
import android.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.belsoft.themoviedbapp.BaseFragment
import com.belsoft.themoviedbapp.MainActivity

abstract class HideKeyboardReadyFragment : BaseFragment() {

    protected abstract var searchListRecyclerViewHide : RecyclerView?
    protected abstract var searchViewHide: SearchView?
    private lateinit var _hideTouchListener : HideKeyboardTouchListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _hideTouchListener = HideKeyboardTouchListener { hideKeyboardSafe() }
    }

    override fun onStart() {
        super.onStart()

        if (searchListRecyclerViewHide == null) return
        searchListRecyclerViewHide!!.setOnTouchListener(_hideTouchListener)
        searchListRecyclerViewHide!!.addOnItemTouchListener(_hideTouchListener)
    }

    override fun onPause() {
        super.onPause()

        if (searchListRecyclerViewHide == null) return
        searchListRecyclerViewHide!!.removeOnItemTouchListener(_hideTouchListener)
    }

    private fun hideKeyboardSafe() {
        if (MainActivity.isKeyboardOnScreen()) {
            if (searchViewHide == null) return

            MainActivity.hideSoftKeyboard(searchViewHide!!)
            searchViewHide!!.clearFocus()
        }
    }
}