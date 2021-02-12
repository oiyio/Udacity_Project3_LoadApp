package com.udacity.extension

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import com.udacity.DetailActivity
import com.udacity.R

private const val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(context: Context, isSuccess: Boolean, title: String) {

    cancelAll()

    val detailIntent = DetailActivity.getIntent(context = context, status = isSuccess, title = title)

    val contentPendingIntent = PendingIntent.getActivity(
        context,
        NOTIFICATION_ID,
        detailIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val actionButtonPendingIntent: PendingIntent = PendingIntent.getActivity(
        context,
        NOTIFICATION_ID,
        detailIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(
        context,
        context.getString(R.string.notification_channel_id_download)
    )
        .setContentTitle(context.getString(R.string.notification_title))
        .setContentText(title)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .addAction(
            R.drawable.ic_assistant_black_24dp,
            context.getString(R.string.notification_button),
            actionButtonPendingIntent
        ).setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
    notify(NOTIFICATION_ID, builder.build())
}