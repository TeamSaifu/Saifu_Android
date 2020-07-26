package com.ze20.saifu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class AddWishActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_wish)
        setTitle(R.string.title_new)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
