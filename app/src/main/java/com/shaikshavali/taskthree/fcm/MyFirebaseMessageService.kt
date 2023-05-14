package com.shaikshavali.taskthree.fcm


import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.shaikshavali.taskthree.R
import com.shaikshavali.taskthree.activities.HomeScreen
import com.shaikshavali.taskthree.activities.LoginActivity
import com.shaikshavali.taskthree.firestore.FirestoreClass
import com.shaikshavali.taskthree.utils.Constants
import java.util.Random

class MyFirebaseMessageService : FirebaseMessagingService() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload : ${remoteMessage.data}")

            val title = remoteMessage.data[Constants.FCM_KEY_TITLE]!!
            val message = remoteMessage.data[Constants.FCM_KEY_MESSAGE]!!


            //need to make changes here to not to get notification to me i.e., logged in user

            // Finally sent them to build a notification.
            sendNotification(title, message)

        }

        remoteMessage.notification?.let {

            Log.d(TAG, "Message notification body : ${it.body}")
        }

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d(TAG, "Refreshed Token : $token")

        sendRegistrationToSerer(token)

    }


    private fun sendRegistrationToSerer(token: String?) {
        val sharedPreferences =
            this.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(Constants.FCM_TOKEN, token)
        editor.apply()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("UnspecifiedImmutableFlag")
    private fun sendNotification(title: String, message: String) {

        lateinit var pendingIntent: PendingIntent

        val intent: Intent = if (FirestoreClass().getCurrentUserID().isNotEmpty()) {
            Intent(this, HomeScreen::class.java)
        } else {
            Intent(this, LoginActivity::class.java)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        val channelId = this.resources.getString(R.string.notification_channel_id)
        val soundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.img_cloud)
            .setAutoCancel(true)
            .setSound(soundURI)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, "task 3...", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        val rand: Int = Random().nextInt()
        //we can get random number of notifications now

//        stopForeground(true)
//        stopSelf()

        Log.e("Rand @FCM :", "$rand")
        notificationManager.notify(rand, notificationBuilder.build())

    }

    companion object {
        const val TAG = "MessagingService"
    }

}


//
//(30660) for package com.shaikshavali.taskthree ----------------------------
//2023-05-10 22:20:02.457 30660-31024 MessagingService        com.shaikshavali.taskthree           D  From: 645267907779
//2023-05-10 22:20:02.457 30660-31024 MessagingService        com.shaikshavali.taskthree           D  MessageId: 0:1683737403265274%276557def9fd7ecd
//2023-05-10 22:20:02.457 30660-31024 MessagingService        com.shaikshavali.taskthree           D  Notification : null
//2023-05-10 22:20:02.457 30660-31024 MessagingService        com.shaikshavali.taskthree           D  Sender ID : 645267907779
//2023-05-10 22:20:02.457 30660-31024 MessagingService        com.shaikshavali.taskthree           D  To : null
//2023-05-10 22:20:02.457 30660-31024 MessagingService        com.shaikshavali.taskthree           D  Message data payload : {title=Status, message=Successfully Updated}