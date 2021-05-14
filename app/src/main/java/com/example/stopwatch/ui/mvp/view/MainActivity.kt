package com.example.stopwatch.ui.mvp.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.stopwatch.realm.RealmManager
import com.example.stopwatch.ui.mvp.contract.MainActivityContract
import com.example.stopwatch.ui.mvp.presenter.MainActivityPresenter
import com.example.stopwatch.utils.Converter
import com.example.stopwatch.AppConstants
import com.example.stopwatch.databinding.ActivityMainBinding
import com.example.stopwatch.service.StopwatchService

class MainActivity : AppCompatActivity(), MainActivityContract.View {
    private lateinit var realmManager: RealmManager
    private lateinit var viewBinding: ActivityMainBinding
    private var presenter: MainActivityPresenter? = null
    private var timingState: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = viewBinding.root
        setContentView(view)
        presenter = MainActivityPresenter(this)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        realmManager = RealmManager()
        viewBinding.timeListRecycler.layoutManager = LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false)
        viewBinding.timeListRecycler.adapter = presenter!!.getAdapter()
        viewBinding.startBtn.setOnClickListener {
            onStartBtnClicked()
        }
        viewBinding.stopBtn.setOnClickListener {
            onStopBtnClicked()
        }
        viewBinding.saveLapBtn.setOnClickListener {
            onSaveLapBtnClicked()
        }
        viewBinding.removeLapBtn.setOnClickListener {
            onRemoveLapBtnClicked()
        }
        viewBinding.removeLapBtn.setOnLongClickListener {
            onRemoveLapBtnLongClicked()
            true
        }
        saveLapBtnStateChange()
        setStopwatchText()
    }

    override fun onDestroy() {
        realmManager.closeRealm()
        sendCommandToService(AppConstants.ACTION_STOP_SERVICE)
        super.onDestroy()
    }

    override fun getLifecycleOwner(): LifecycleOwner {
        return this
    }

//onclick listeners

    private fun onStartBtnClicked() {
        presenter!!.onStartBtnClicked()
    }

    private fun onStopBtnClicked() {
        presenter!!.onStopBtnClicked()

    }

    private fun onSaveLapBtnClicked() {
        presenter!!.onSaveBtnClicked(viewBinding.stopwatch.text.toString())
    }

    private fun onRemoveLapBtnClicked() {
        presenter!!.onRemoveLapBtnClicked()
    }

    private fun onRemoveLapBtnLongClicked() {
        presenter!!.onRemoveLapBtnLongClicked()
    }

//start service
    override fun sendCommandToService(action: String) =
            Intent(this, StopwatchService::class.java).also {
                it.action = action
                this.startService(it)
            }

//change ui elements
    override fun saveLapBtnStateChange() {
        StopwatchService.isRunning.observe(this, {
            timingState = it
            if (timingState) {
                viewBinding.saveLapBtn.visibility = View.VISIBLE
            } else {
                viewBinding.saveLapBtn.visibility = View.INVISIBLE
            }
        })
    }

    override fun setStopwatchText() {
        StopwatchService.timeRunInMillis.observe(this, {
            val formattedTime = Converter.getFormattedTime(it, true)
            viewBinding.stopwatch.text = formattedTime
        })
    }

    override fun removeLapBtnVisibilityChange(boolean: Boolean) {
        if (boolean) {
            viewBinding.removeLapBtn.visibility = View.VISIBLE
        } else {
            viewBinding.removeLapBtn.visibility = View.INVISIBLE
        }
    }

}