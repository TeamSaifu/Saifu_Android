package com.ze20.saifu.ui.budget

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.ze20.saifu.R

class SpendSettings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spend_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setTitle("固定支出")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        val inflater = menuInflater

        inflater.inflate(R.menu.spend_menu, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
