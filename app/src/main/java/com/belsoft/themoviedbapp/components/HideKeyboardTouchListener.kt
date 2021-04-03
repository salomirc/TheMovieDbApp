package com.belsoft.themoviedbapp.components

import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HideKeyboardTouchListener( private val hideKeyboard : () -> Unit) :
    RecyclerView.OnItemTouchListener, View.OnTouchListener {

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        if (e.action == MotionEvent.ACTION_DOWN){
            hideKeyboard()
        }
        return false
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN){
            hideKeyboard()
        }
        return false
    }
}