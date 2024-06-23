package com.crp.wikiAppNew.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.view.View
import android.view.inputmethod.InputMethodManager

class Helper {


    companion object {

        fun isNetworkAvailable(ctx: Context): Boolean {
            val connectivityManager =
                ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }


        fun hideKeyboard(activity: Activity) {
            val imm =
                activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            //Find the currently focused view, so we can grab the correct window token from it.
            var view = activity.currentFocus
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}