package com.example.myapplication

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log


class ExampleService : Service() {
    private var time = 11
    private val binder = localBinder()
    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false

    inner class localBinder : Binder() {
        fun getService(): ExampleService = this@ExampleService
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        stopSelf()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        Log.d("ExampleService", "onDestroy")
    }

    override fun onBind(intent: Intent?): IBinder {
        if (!isRunning) {
            startCountdown()
            isRunning = false
        }
        return binder
    }

    fun getValue(): Int{
        return time
    }

    fun restartCountdown(): Int {
        return 11
    }

    private fun startCountdown(){
        handler.post(object : Runnable{
            override fun run() {
                if (time>0){
                    handler.postDelayed(this,1000)
                    time--
                }
                else{
                    isRunning = false
                }
            }

        })
    }
}