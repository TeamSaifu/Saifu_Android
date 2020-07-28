package com.ze20.saifu

import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream

internal class ConvenientFunction {

    // 色んな所で使う関数をまとめておきます

    fun editToInt(editText: EditText): Int? {
        // editText を 数値に変換 空文字列ならNullを返す
        return editText.text.toString().toIntOrNull()
    }

    fun getBinaryFromBitmap(bitmap: Bitmap): ByteArray {
        // 画像をByteArray型に変換するやつです
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    fun getClip(applicationContext: Context): String? {
        // クリップボードを返す
        val mManager: ClipboardManager =
            applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        return mManager.primaryClip?.getItemAt(0)?.text.toString()
    }

    fun hideKeyboard(appCompatActivity: AppCompatActivity, view: View?) {
        // キーボードを探してあれば消します
        view.also {
            val manager =
                appCompatActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(view!!.windowToken, 0)
        }
    }
}
