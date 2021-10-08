package com.belsoft.themoviedbapp

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.belsoft.themoviedbapp.databinding.ActivityMainBinding
import com.belsoft.themoviedbapp.utils.InjectorUtils

class MainActivity : BaseActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    companion object {
        lateinit var isKeyboardOnScreen: () -> Boolean
        lateinit var hideSoftKeyboard: (View) -> Unit
        lateinit var showSoftKeyboard: (View) -> Unit
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setArchitectureComponents()
        setCompanionObjectMembers()
        initializeUI()
    }

    private fun setArchitectureComponents() {

        // Get the MainViewModelFactory with all of it's dependencies constructed
        val factory = InjectorUtils.getInstance.provideMainViewModelFactory()

        // Obtain the ViewModel component.
        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)

        // Inflate view and obtain an instance of the binding class.
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // Specify the current activity as the lifecycle owner.
        binding.lifecycleOwner = this

        // Assign the component to a property in the binding class.
        binding.viewmodel = viewModel
    }

    private fun setCompanionObjectMembers() {
        val rootView = binding.root
        isKeyboardOnScreen = {
            val r = Rect()
            rootView.getWindowVisibleDisplayFrame(r)
            val heightDiff = rootView.rootView.height - (r.bottom - r.top)
            heightDiff > rootView.rootView.height / 4
        }

        hideSoftKeyboard = { view: View ->
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }

        showSoftKeyboard = { view: View ->
            val hasFocus = if (view.hasFocus()) true else view.requestFocus()
            if (hasFocus) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    private fun initializeUI() {
        //SingleEvent received from ViewModel
        viewModel.toastMessage.observe(this, Observer { message ->
            // Update the cached copy of the words in the adapter.
            message?.let {
                displayToastMessage(this, resources.getString(it))
            }
        })

        //SingleEvent received from ViewModel
        viewModel.toastMessageString.observe(this, Observer { message ->
            // Update the cached copy of the words in the adapter.
            message?.let {
                displayToastMessage(this, it)
            }
        })
    }

    private fun displayToastMessage(context: Context, message: String) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.CENTER,0, 0)
        toast.show()
    }
}