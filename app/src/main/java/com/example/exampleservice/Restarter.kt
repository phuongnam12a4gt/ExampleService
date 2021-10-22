package com.example.exampleservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class Restarter:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("TAG","Nhận được ở đây ko")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context!!.startForegroundService(Intent(context,MyService::class.java))
        } else {
            context!!.startService(Intent(context, MyService::class.java))
        }
    }
}