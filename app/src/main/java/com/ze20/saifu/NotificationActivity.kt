package com.ze20.saifu

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.android.synthetic.main.activity_notification.*

class NotificationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // チャネルを作成する
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel("2", name, importance)
            mChannel.description = descriptionText

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }

        // インテントを作成(通知をタップした時に表示する画面???)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        // 通知のアイコン、タイトル、本文、インテント(表示する画面)、trueでタップした通知は消える
        var builder = NotificationCompat.Builder(this, "2")
            .setSmallIcon(R.drawable.ic_baseline_notifications_48)
            .setContentTitle("通知テスト")
            .setContentText("Hello Word!!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // ボタンを押すと通知がステータスバーに表示される
        tsuchiButton.setOnClickListener {
            with(NotificationManagerCompat.from(this)) {
                notify(1, builder.build())
            }
        }
    }
    // メニュー作成時
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_delnotification, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
