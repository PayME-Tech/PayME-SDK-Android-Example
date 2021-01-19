package com.example.testapp

import android.app.IntentService
import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.LinearGradient
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.BufferedReader
import java.io.InputStreamReader

class WindowService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var containerButtonShowLog: RelativeLayout
    private lateinit var buttonShowLog: Button
//    lateinit var recycleviewLog: RecyclerView

    companion object {
        var dataList = arrayListOf<String>()
        var showLog = false
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        initView()
        super.onCreate()


    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    private fun initView() {

        containerButtonShowLog = LayoutInflater.from(applicationContext)
            .inflate(R.layout.button_overlay, null) as RelativeLayout
        buttonShowLog = containerButtonShowLog.findViewById(R.id.buttonShowLog)

        initWindowLayer()


        buttonShowLog.setOnClickListener {
            if (showLog) {
                showLog = false
                sendBroadcast(Intent("abcdef"))
            } else {
                showLog = true
                dataList.clear()
                Log.d("tham bla blabla", "kdsfksdkf")
                val process: Process = Runtime.getRuntime().exec("logcat -d")
                val bufferedReader = BufferedReader(
                    InputStreamReader(process.inputStream)
                )

                var line: String? = ""
                while (bufferedReader.readLine().also { line = it } != null) {
                    dataList.add(line!!)
                }
                dataList.reverse()

                startActivity(Intent(this, LogActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }

        }


    }

    private fun initWindowLayer() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                    or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    or WindowManager.LayoutParams.FLAG_SPLIT_TOUCH
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
                    or (WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or 0),
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.BOTTOM or Gravity.START
        windowManager.addView(containerButtonShowLog, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        sendBroadcast(Intent("abcdef"))
        windowManager.removeViewImmediate(containerButtonShowLog)
    }
}