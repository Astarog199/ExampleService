package com.example.myapplication

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var exampleService: ExampleService? = null
    private var isServiceBound = false
    private val scope = CoroutineScope(Dispatchers.IO)
    private val handler = Handler(Looper.getMainLooper())

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as ExampleService.localBinder
            exampleService = binder.getService()
            isServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            if (isServiceBound) {
                val counter = exampleService?.restartCountdown()
                binding.textView.text = counter.toString()
            } else {
                startPolling()
            }
        }
    }

    private fun startPolling() {
        if (!isServiceBound) {
            bindService()
        }
        handler.post(object : Runnable {
            override fun run() {
                if (isServiceBound) {
                    val counter = exampleService?.getValue()
                    if (counter!! > 0) {
                        binding.textView.text = counter.toString()
                        handler.postDelayed(this, 1000)
                    } else {
                        unbindService()
                        binding.textView.text = 0.toString()
                    }
                }
                else{
                    handler.postDelayed(this, 10)
                }
            }

        })
    }

    private fun bindService() {
        val serviceIntent = Intent(this, ExampleService::class.java)
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
    }

    private fun unbindService() {
        if (isServiceBound){
            unbindService(connection)
            isServiceBound = false
        }
    }
}