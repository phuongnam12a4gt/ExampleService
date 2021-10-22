package com.example.exampleservice

import android.app.Notification.CATEGORY_SERVICE
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.exampleservice.model.Song
import java.util.*


class MyService : Service() {
    private var mediaplayer: MediaPlayer? = null
    private var isPlaying = false
    private var mSong: Song? = null
    override fun onCreate() {
        super.onCreate()
        Log.i("TAG", "Service Created")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val bundle = intent?.extras
        if (bundle != null) {
            val song = bundle.get("ObjectSong")
            if (song != null) {
                mSong = song as Song?
                startMusic(song)
                sendNotification(song)
            } else {
                val actionMusic = intent.getIntExtra("action_music_service", 0)
                handleActionMusic(actionMusic)
            }
            Log.i("TAG","onStartCommand")
        }
        return START_REDELIVER_INTENT
    }

    private fun startMusic(song: Song) {
        if (mediaplayer == null) {
            mediaplayer = MediaPlayer.create(applicationContext, song.resource)
        }
        mediaplayer?.start()
        isPlaying = true
        sendToActivity(ACTION_START)
    }

    private fun sendNotification(song: Song?) {
        val bitmapSong = song?.image?.let { BitmapFactory.decodeResource(resources, it) }
        val remoteView = RemoteViews(packageName, R.layout.layout_custom_notification)
        remoteView.setTextViewText(R.id.texViewTitleSong, song?.title)
        remoteView.setTextViewText(R.id.texViewSingle, song?.single)
        remoteView.setImageViewBitmap(R.id.imageViewSong, bitmapSong)
        remoteView.setImageViewResource(R.id.imageViewPlayOrPause, R.drawable.ic_pause)
        remoteView.setImageViewResource(R.id.imageViewClear, R.drawable.ic_clear)
        if (isPlaying == true) {
            remoteView.setOnClickPendingIntent(
                R.id.imageViewPlayOrPause,
                getPendingIntent(this, ACTION_PAUSE)
            )
            remoteView.setImageViewResource(R.id.imageViewPlayOrPause, R.drawable.ic_pause)
        }
        if (isPlaying == false) {
            remoteView.setOnClickPendingIntent(
                R.id.imageViewPlayOrPause,
                getPendingIntent(this, ACTION_PLAY)
            )
            remoteView.setImageViewResource(R.id.imageViewPlayOrPause, R.drawable.ic_play)
        }
        remoteView.setOnClickPendingIntent(
            R.id.imageViewClear,
            getPendingIntent(this, ACTION_CLEAR)
        )
        val notification = NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_play)
            .setCustomContentView(remoteView)
            .setSound(null)
            .setCategory(CATEGORY_SERVICE)
            .build()
        startForeground(1, notification)
    }

    private fun getPendingIntent(context: Context, action: Int): PendingIntent? {
        val intent = Intent(this, MyBroadCastReceiver::class.java)
        intent.putExtra("action_music", action)
        return PendingIntent.getBroadcast(
            context,
            action,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun handleActionMusic(action: Int) {
        when (action) {
            1 -> pauseMusic()
            2 -> playMusic()
            3 -> clearMusic()
        }
    }

    private fun clearMusic() {
        stopSelf()
        sendToActivity(ACTION_CLEAR)
    }

    private fun playMusic() {
        if (mediaplayer != null && isPlaying == false) {
            mediaplayer!!.start()
            isPlaying = true
        }
        mSong?.let {
            sendNotification(mSong)
        }
        sendToActivity(ACTION_PLAY)
    }

    private fun pauseMusic() {
        if (mediaplayer != null && isPlaying == true) {
            mediaplayer!!.pause()
            isPlaying = false
        }
        Log.i("TAG", mSong.toString())
        mSong?.let {
            sendNotification(mSong)
        }
        sendToActivity(ACTION_PAUSE)
    }

    override fun onDestroy() {
        Log.i("TAG", "Destroy Service")
        super.onDestroy()
        if (mediaplayer != null) {
            mediaplayer!!.release()
            mediaplayer = null
        }
        super.onDestroy()
    }

    fun sendToActivity(action: Int) {
        var intent = Intent("send_action_to_activity")
        var bundle = Bundle()
        bundle.putParcelable("objectSong", mSong)
        bundle.putBoolean("statusPlayer", isPlaying)
        bundle.putInt("actionMusic", action)
        intent.putExtras(bundle)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.i("TAG", "Task Remove")
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Log.i("TAG","TrimMemory")
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Log.i("TAG","LowMemory")
    }

    companion object {
        val ACTION_PAUSE = 1
        val ACTION_PLAY = 2
        val ACTION_CLEAR = 3
        val ACTION_START = 4
    }
}
