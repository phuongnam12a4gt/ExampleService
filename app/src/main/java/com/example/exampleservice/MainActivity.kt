package com.example.exampleservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.exampleservice.model.Song
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var mSong: Song? = null
    private var isPlaying: Boolean = false
    private var broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            var bundle = intent?.extras
            if (bundle != null) {
                mSong = bundle.get("objectSong") as Song
                isPlaying = bundle.getBoolean("statusPlayer")
                var action = bundle.getInt("actionMusic")
                handleLayoutMusic(action)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("TAG", "onActivityCreate")
        setContentView(R.layout.activity_main)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadCastReceiver, IntentFilter("send_action_to_activity"))
    }

    override fun onResume() {
        super.onResume()
        Log.i("TAG","Activity onResume")
        val intent = Intent(applicationContext, MyService::class.java)
        buttonStartService.setOnClickListener {
            val song = Song(
                "Khóc cùng anh",
                "Phuong Nam",
                R.drawable.nguoikhoccunganh,
                R.raw.khoccunganh_hoquanghieu
            )
            val bundle = Bundle().apply {
                putParcelable("ObjectSong", song)
            }
            intent.putExtras(bundle)
            ContextCompat.startForegroundService(this,intent)
        }
        buttonStopService.setOnClickListener {
            stopService(intent)
        }
    }

    override fun onDestroy() {
        Log.i("TAG", "onDestroy")
//        val broadcastIntent = Intent()
//        broadcastIntent.action = "restartservice"
//        broadcastIntent.setClass(this, Restarter::class.java)
//        this.sendBroadcast(broadcastIntent)
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadCastReceiver)
    }

    private fun handleLayoutMusic(action: Int) {
        when (action) {
            MyService.ACTION_START -> actionStart()
            MyService.ACTION_PLAY -> actionPlay()
            MyService.ACTION_PAUSE -> actionPause()
            MyService.ACTION_CLEAR -> actionClear()
        }
    }

    private fun actionClear() {
        relativeLayoutBottom.visibility = View.GONE
    }

    private fun actionPause() {
        setStatusPlayorPause()
    }

    private fun actionPlay() {
        setStatusPlayorPause()
    }

    private fun actionStart() {
        relativeLayoutBottom.visibility = View.VISIBLE
        showInfoSong()
        setStatusPlayorPause()
    }

    private fun showInfoSong() {
        mSong?.let {
            imageViewSong.setImageResource(mSong!!.image)
            texViewTitleSong.text = mSong!!.title
            texViewSingle.text = mSong!!.single
        }
        imageViewPlayOrPause.setOnClickListener {
            if (isPlaying) {
                sendActiontoService(MyService.ACTION_PAUSE)
            } else
                sendActiontoService(MyService.ACTION_PLAY)
        }
        imageViewClear.setOnClickListener {
            sendActiontoService(MyService.ACTION_CLEAR)
        }
    }

    private fun setStatusPlayorPause() {
        if (isPlaying) {
            imageViewPlayOrPause.setImageResource(R.drawable.ic_pause)
        } else {
            imageViewPlayOrPause.setImageResource(R.drawable.ic_play)
        }
    }

    private fun sendActiontoService(action: Int) {
        var intent = Intent(this, MyService::class.java)
        intent.putExtra("action_music_service", action)
        startService(intent)
    }
}
