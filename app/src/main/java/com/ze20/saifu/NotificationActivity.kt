package com.ze20.saifu

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity

class NotificationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    //メニュー作成時
    override fun onCreateOptionsMenu(menu: Menu?):Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_delnotification,menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
