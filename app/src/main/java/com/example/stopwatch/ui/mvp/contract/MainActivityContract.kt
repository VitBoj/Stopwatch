package com.example.stopwatch.ui.mvp.contract

import android.content.Intent
import androidx.lifecycle.LifecycleOwner
import com.example.stopwatch.ui.TimeListAdapter

interface MainActivityContract {
    interface View {
        fun sendCommandToService(action: String): Intent
        fun removeLapBtnVisibilityChange(boolean: Boolean)
        fun getLifecycleOwner(): LifecycleOwner
        fun saveLapBtnStateChange()
        fun setStopwatchText()
    }

    interface Presenter {
        fun onSaveBtnClicked(text: String)
        fun saveLap(time: String)
        fun onStopBtnClicked()
        fun onRemoveLapBtnClicked()
        fun getAdapter(): TimeListAdapter
        fun onRemoveLapBtnLongClicked()
        fun onStartBtnClicked()
    }

}