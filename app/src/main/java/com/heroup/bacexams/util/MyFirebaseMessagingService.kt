package com.heroup.bacexams.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.heroup.bacexams.MainActivity
import com.heroup.bacexams.R

class MyFirebaseMessagingService : FirebaseMessagingService() {
	override fun onMessageReceived(remoteMessage: RemoteMessage) {

		Log.d(TAG, "From: ${remoteMessage.from}")


		// Check if message contains a notification payload.
		remoteMessage.notification?.let {
			sendNotification(it)
			Log.d(TAG, "Message Notification Body: ${it.body}")
		}

		// Also if you intend on generating your own notifications as a result of a received FCM
		// message, here is where that should be initiated. See sendNotification method below.
	}
	// [END receive_message]

	// [START on_new_token]
	/**
	 * Called if InstanceID token is updated. This may occur if the security of
	 * the previous token had been compromised. Note that this is called when the InstanceID token
	 * is initially generated so this is where you would retrieve the token.
	 */
	override fun onNewToken(token: String) {
		Log.d(TAG, "Refreshed token: $token")

		// If you want to send messages to this application instance or
		// manage this apps subscriptions on the server side, send the
		// Instance ID token to your app server.
		sendRegistrationToServer(token)
	}
	// [END on_new_token]


	/**
	 * Persist token to third-party servers.
	 *
	 * Modify this method to associate the user's FCM InstanceID token with any server-side account
	 * maintained by your application.
	 *
	 * @param token The new token.
	 */
	private fun sendRegistrationToServer(token: String?) {
		// TODO: Implement this method to send token to your app server.
		Log.d(TAG, "sendRegistrationTokenToServer($token)")
	}

	/**
	 * Create and show a simple notification containing the received FCM message.
	 *
	 */
	private fun sendNotification(notification: RemoteMessage.Notification) {
		val intent = Intent(this, MainActivity::class.java)
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
		val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
				PendingIntent.FLAG_ONE_SHOT)

		val channelId = notification.channelId ?: "fcm"
		val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
		val notificationBuilder = NotificationCompat.Builder(this, channelId)
				.setSmallIcon(R.drawable.ic_logo)
				.setLargeIcon(
						ContextCompat.getDrawable(this,R.drawable.ic_logo)?.toBitmap()
				)
				.setContentTitle(notification.title)
				.setContentText(notification.body)
				.setAutoCancel(true)
				.setSound(defaultSoundUri)
				.setContentIntent(pendingIntent)

		val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

		// Since android Oreo notification channel is needed.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val channel = NotificationChannel(channelId,
					"Channel human readable title",
					NotificationManager.IMPORTANCE_DEFAULT)
			notificationManager.createNotificationChannel(channel)
		}

		notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
	}

	companion object {

		private const val TAG = "MyFirebaseMsgService"
	}
}