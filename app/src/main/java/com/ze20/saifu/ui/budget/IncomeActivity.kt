package com.ze20.saifu.ui.budget

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.ze20.saifu.R

class IncomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_income_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setTitle("固定収入")
    }

    // メニューアイテムを表示
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        val inflater = menuInflater

        inflater.inflate(R.menu.menu_spendadd, menu)
        return true
    }

    // 一つ前の画面に戻るボタンを表示
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
