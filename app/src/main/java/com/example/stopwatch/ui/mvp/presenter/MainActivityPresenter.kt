package com.example.stopwatch.ui.mvp.presenter

import com.example.stopwatch.realm.Lap
import com.example.stopwatch.realm.RealmManager
import com.example.stopwatch.ui.mvp.contract.MainActivityContract
import com.example.stopwatch.AppConstants
import com.example.stopwatch.ui.TimeListAdapter

class MainActivityPresenter(private var view: MainActivityContract.View) : MainActivityContract.Presenter {
    private var isRunning: Boolean = false
    private var inPause: Boolean = false
    private var realmManager: RealmManager = RealmManager()
    private var timeListAdapter: TimeListAdapter = TimeListAdapter(realmManager.getAllLap())

    init {
        if (realmManager.getAllLap().isNotEmpty()) {
            view.removeLapBtnVisibilityChange(true)
        }
    }

    override fun getAdapter(): TimeListAdapter {
        return timeListAdapter
    }

//save time lap to realm DB
    override fun saveLap(time: String) {
        if (isRunning) {
            val lap = Lap(time, System.currentTimeMillis())
            realmManager.saveLap(lap)
            timeListAdapter.notifyDataSetChanged()
            view.removeLapBtnVisibilityChange(true)
        }
    }

//onclick functions
    override fun onSaveBtnClicked(text: String) {
        saveLap(text)
        timeListAdapter.setData(realmManager.getAllLap())
    }

    override fun onStartBtnClicked() {
        inPause = if (isRunning) {
            view.sendCommandToService(AppConstants.ACTION_PAUSE_SERVICE)
            isRunning = false
            true
        } else {
            view.sendCommandToService(AppConstants.ACTION_START_OR_RESUME_SERVICE)
            if (inPause) {
                isRunning = true
                false
            } else {
                isRunning = true
                false
            }
        }
    }

    override fun onStopBtnClicked() {
        view.sendCommandToService(AppConstants.ACTION_STOP_SERVICE)
        isRunning = false
        inPause = false
    }

    override fun onRemoveLapBtnClicked() {
        realmManager.removeLastAddedLap()
        timeListAdapter.setData(realmManager.getAllLap())
        if (realmManager.getAllLap().isEmpty()) {
            view.removeLapBtnVisibilityChange(false)
        }
    }

    override fun onRemoveLapBtnLongClicked() {
        realmManager.deleteAllLaps()
        timeListAdapter.setData(realmManager.getAllLap())
        view.removeLapBtnVisibilityChange(false)
    }

}