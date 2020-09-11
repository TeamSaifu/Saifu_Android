package com.ze20.saifu

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Dialog
import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

internal class UtilityFunClass {

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

    fun checkIntent(context: Context, intent: Intent, view: View?) {

        // 対応するアプリがあるかどうか確認

        val activities: List<ResolveInfo> = context.packageManager.queryIntentActivities(
            intent,
            PackageManager.MATCH_ALL
        )

        when (activities.isNotEmpty()) {
            // なければボタンが消滅
            false -> view?.visibility = View.GONE
            true -> view?.visibility = View.VISIBLE
        }
    }

    fun photoOrCamera(
        context: Context,
        contentResolver: ContentResolver?,
        requestCode: Int,
        resultCode: Int,
        resultData: Intent?,
        imageView: ImageView
    ): Boolean {

        // 写真を撮ったり選んだりしたあとの処理です

        if (resultCode != RESULT_OK) {
            return false
        }
        when (requestCode) {
            1 -> { // REQUEST_IMAGE_CAPTURE
                val bitmap: Bitmap

                resultData?.extras.also {
                    bitmap = resultData?.extras?.get("data") as Bitmap
                    bitmap.also {
                        imageView.setImageBitmap(bitmap)
                    }
                }
                return true
            }
            42 -> { // READ_REQUEST_CODE
                try {
                    resultData?.data?.also { uri ->
                        val inputStream = contentResolver?.openInputStream(uri)
                        val image = BitmapFactory.decodeStream(inputStream)
                        imageView.setImageBitmap(image)
                        return true
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, context.getString(R.string.Error), Toast.LENGTH_LONG)
                        .show()
                    return false
                }
            }
            else -> {
            }
        }
        return false
    }
    fun quickInsert(context: Context?, price: Int, name: String = "", category: Int = 0): Boolean {
        try {
            val dbHelper = SQLiteDBClass(context!!, "SaifuDB", null, 1)
            val database = dbHelper.writableDatabase // 書き込み可能

            // log表
            // inputDate primary key,payDate,name,price,category,splitCount,picture

            val inputDate = java.util.Date()
            // INSERTするのに必要なデータをvalueにまとめる
            val values = ContentValues()
            values.run {
                put(
                    "inputDate",
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.JAPANESE).format(
                        inputDate
                    )
                )
                put(
                    "payDate",
                    SimpleDateFormat("yyyy-MM-dd 00:00:00", Locale.JAPANESE).format(
                        inputDate
                    )
                )
                put("name", name)
                put("price", price)
                put("category", category)
                put("splitCount", 1)
                put("sign", 0)
            }
            // DBに登録する できなければエラーを返す
            database.insertOrThrow("log", null, values)
            return true
        } catch (exception: Exception) {
            Log.e("insertData", exception.toString()) // エラーをログに出力
            return false
        }
    }

    fun CategoryImage(no: Int): Int? {
        return when (no) {
            0 -> R.drawable.ic_baseline_restaurant_24
            1 -> R.drawable.ic_baseline_sports_esports_24
            2 -> R.drawable.ic_baseline_phonelink_ring_24
            3 -> R.drawable.ic_baseline_train_24
            4 -> R.drawable.ic_baseline_medical
            5 -> R.drawable.ic_baseline_local_airport_24
            6 -> R.drawable.ic_baseline_utility
            7 -> R.drawable.ic_baseline_necessities
            8 -> R.drawable.ic_baseline_house
            9 -> R.drawable.ic_baseline_salary
            10 -> R.drawable.ic_baseline_bonus
            11 -> R.drawable.ic_baseline_extraordinary
            12 -> R.drawable.ic_baseline_more_horiz_24
            else -> null
        }
    }

    fun monthEnd(month: Int, year: Int = 1999): Int {
        return when (month) {
            2 -> {
                when {
                    year % 400 == 0 -> 28
                    year % 100 == 0 -> 29
                    else -> 28
                }
            }
            4, 6, 9, 11 -> 30
            else -> 31
        }
    }

    fun incomeSum(context: Context): Int {
        val dbName: String = "SaifuDB"
        val tableName: String = "budget"
        val dbVersion: Int = 1
        var incomeSum: Int = 0
        try {
            // DBにアクセス
            val SQLiteDB = SQLiteDBClass(context, dbName, null, dbVersion)
            val database = SQLiteDB.readableDatabase

            // SQL文を構成

            // budget表
            // id INTEGER primary key AUTOINCREMENT,name,type,price
            val sql =
                "select sum(price) from " + tableName + " where type = 'income';"
            val cursor = database.rawQuery(sql, null)

            if (cursor.count > 0) {
                cursor.moveToFirst()
                incomeSum = cursor.getInt(0)
            }
        } catch (e: Exception) {
            Log.e("DBSelectError", e.toString())
        }
        return incomeSum
    }

    fun spendSum(context: Context): Int {
        val dbName: String = "SaifuDB"
        val tableName: String = "budget"
        val dbVersion: Int = 1
        var spendSum: Int = 0

        try {
            // DBにアクセス
            val SQLiteDB = SQLiteDBClass(context, dbName, null, dbVersion)
            val database = SQLiteDB.readableDatabase

            // SQL文を構成

            // budget表
            // id INTEGER primary key AUTOINCREMENT,name,type,price
            val sql =
                "select sum(price) from " + tableName + " where type = 'spend';"
            val cursor = database.rawQuery(sql, null)

            if (cursor.count > 0) {
                cursor.moveToFirst()
                spendSum = cursor.getInt(0)
            }
        } catch (e: Exception) {
            Log.e("DBSelectError", e.toString())
        }
        return spendSum
    }
}

class okCancelDialogFragment : DialogFragment() {

    // OKとキャンセルを表示するダイアログです

    // https://qiita.com/suzukihr/items/8973527ebb8bb35f6bb8

    // https://qiita.com/kumas/items/739f410438d182098b31

    var title = "タイトル"
    var message = "メッセージ"
    var okText = "OK"
    var onOkClickListener: DialogInterface.OnClickListener? = null
    var cancelText = "Cancel"
    var onCancelClickListener: DialogInterface.OnClickListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(getActivity()).run {
            setTitle(title)
            setMessage(message)
            setPositiveButton(okText, onOkClickListener)
            setNegativeButton(cancelText, onCancelClickListener)
            create()
        }
    }

    override fun onPause() {
        super.onPause()

        // onPause でダイアログを閉じる場合
        dismiss()
    }
}
