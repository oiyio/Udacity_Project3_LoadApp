package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udacity.extension.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private val notificationManager: NotificationManager by lazy {
        ContextCompat.getSystemService(this, NotificationManager::class.java) as NotificationManager
    }
    private val downloadManager: DownloadManager by lazy {
        applicationContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        loadingButton.setOnClickListener {

            when (radioGroup.checkedRadioButtonId) {
                R.id.radioButtonGlide -> download(URL_DOWNLOAD.GLIDE)
                R.id.radioButtonUdacity -> download(URL_DOWNLOAD.UDACITY)
                R.id.radioButtonRetrofit -> download(URL_DOWNLOAD.RETROFIT)
                else -> Toast.makeText(
                    this,
                    resources.getString(R.string.please_make_selection),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        createChannel(
            getString(R.string.notification_channel_id_download),
            getString(R.string.notification_channel_name_download)
        )
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.apply {
                /*setShowBadge(false)*/
                enableLights(true)      // display notification lights, on devices that support that feature.
                lightColor = Color.RED
                enableVibration(true) // vibrate when notification comes
                description = resources.getString(R.string.channel_description)
            }

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }


    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (downloadID == id) {
                val query = DownloadManager.Query()
                query.setFilterById(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0));
                val cursor: Cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    if (cursor.count > 0) {
                        val downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                        val downloadDescription = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION))
                        if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL) {
                            loadingButton.setState(ButtonState.Completed)
                            notificationManager.sendNotification(context!!, true, downloadDescription)
                        } else {
                            loadingButton.setState(ButtonState.Completed)
                            notificationManager.sendNotification(context!!, false, downloadDescription)
                        }
                    }
                }
            }
        }
    }

    private fun download(urlDownload: URL_DOWNLOAD) {
        loadingButton.buttonState = ButtonState.Loading

        val request =
            DownloadManager.Request(Uri.parse(urlDownload.url))
                .setTitle(getString(R.string.app_name))
                .setDescription(applicationContext.getString(urlDownload.urlTitle))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)  // enqueue puts the download request in the queue.
    }

    companion object {

        private var downloadID: Long = 0
    }

}

enum class URL_DOWNLOAD(val url: String, @StringRes val urlTitle: Int) {
    UDACITY("https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip", R.string.udacity),
    GLIDE("https://github.com/bumptech/glide/archive/master.zip", R.string.glide),
    RETROFIT("https://github.com/square/retrofit/archive/master.zip", R.string.retrofit)
}
