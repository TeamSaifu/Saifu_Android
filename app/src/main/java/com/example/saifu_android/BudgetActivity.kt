package com.example.saifu_android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class BudgetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.budget_config)
        setTitle("予算設定画面")
    }
}