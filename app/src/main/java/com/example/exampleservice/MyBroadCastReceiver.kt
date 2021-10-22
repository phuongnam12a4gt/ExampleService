package com.example.exampleservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi

class MyBroadCastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        var actionMusic = intent?.getIntExtra("action_music", 0)
        var intentService = Intent(context, MyService::class.java)
        intentService.putExtra("action_music_service", actionMusic)
        context?.startService(intentService)
    }
}