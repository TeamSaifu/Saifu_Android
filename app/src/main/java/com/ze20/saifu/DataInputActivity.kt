package com.ze20.saifu

import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_data_input.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

open class DataInputActivity : AppCompatActivity() {

    private var date = java.util.Date() // 今日の日付を格納
    private var sign = false // プラス・マイナス
    private var userSetDate: java.util.Date = java.util.Date() // 設定された日付・初期値は今日
    private var mode = "New" // 現在のモード
    private var id: String? = null // 欲しい物リストのID
    private var busyFlag = false // ローディング中はいろいろ動かなくするためのフラグ
    private var addShortcutflag = false

    private val fileIntent = Intent(Intent.ACTION_OPEN_DOCUMENT) // ファイルの選択
    private val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) // カメラ撮影
    private val cFunc = ConvenientFunction()

    companion object {
        private const val REQUEST_IMAGE_CAPTURE: Int = 1
        private const val READ_REQUEST_CODE: Int = 42
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_input)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.title_data_input)

        // UserSetDateを表示しておく
        dayText.text = SimpleDateFormat("yyyy/MM/dd", Locale.JAPANESE).format(userSetDate)
        checkIntent()
        modeCheck()
        setListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // メニューボタンをセットする
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_apply, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        // 戻るボタンを押したときの処理
        finish()
        return super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)

        // 写真を撮ったり選んだりしたあとの処理です

        if (resultCode != RESULT_OK) {
            return
        }
        when (requestCode) {
            REQUEST_IMAGE_CAPTURE -> {
                val bitmap: Bitmap
                val imageView: ImageView = findViewById(R.id.photoImageView)

                resultData?.extras.also {
                    bitmap = resultData?.extras?.get("data") as Bitmap
                    bitmap.also {
                        imageView.setImageBitmap(bitmap)
                    }
                }
                showPicture()
            }
            READ_REQUEST_CODE -> {
                try {
                    resultData?.data?.also { uri ->
                        val inputStream = contentResolver?.openInputStream(uri)
                        val image = BitmapFactory.decodeStream(inputStream)
                        val imageView = findViewById<ImageView>(R.id.photoImageView)
                        imageView.setImageBitmap(image)
                        showPicture()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "エラーが発生しました", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // 登録ボタンを押した時の処理です
        when (item.itemId) {
            R.id.applyButton -> {
                if (moneyEdit.text.length == 0 || cFunc.editToInt(moneyEdit) == 0) {
                    val alartDialogFragment = okCancelDialogFragment()
                    alartDialogFragment.run {
                        title = "注意"
                        message = "入力金額が0です。本当に登録してよろしいですか？"
                        onOkClickListener = DialogInterface.OnClickListener { _, _ ->
                            insertDB()
                        }
                        cancelText = "キャンセル"
                        onCancelClickListener = DialogInterface.OnClickListener { dialog, _ ->
                            dialog.dismiss()
                        }
                        show(supportFragmentManager, null)
                    }
                } else {
                    return insertDB()
                }
            }
            else -> {
                finish()
            }
        }
        return true
    }

    private fun checkIntent() {

        // 画像ギャラリーがあるかどうか確認

        val activities: List<ResolveInfo> = packageManager.queryIntentActivities(
            intent,
            PackageManager.MATCH_ALL
        )

        when (activities.isNotEmpty()) {
            // なければボタンが消滅
            false -> pictureAdd.visibility = View.GONE
            true -> pictureAdd.visibility = View.VISIBLE
        }

        // カメラアプリがあるかどうか確認

        val cameraActivities: List<ResolveInfo> = packageManager.queryIntentActivities(
            cameraIntent,
            PackageManager.MATCH_ALL
        )

        when (cameraActivities.isNotEmpty()) {
            // なければボタンが消滅
            false -> picturePhoto.visibility = View.GONE
            true -> picturePhoto.visibility = View.VISIBLE
        }
    }

    private fun setListeners() {

        // クリック時とかのリスナーをセットするとこ

        MainLayout.setOnClickListener {
            // 画面のどこかおしたらキーボードを消す
            cFunc.hideKeyboard(this, this@DataInputActivity.currentFocus)
        }

        plusMinusButton.setOnClickListener {
            // プラスマイナス切り替え
            plusMinus()
        }
        moneyEdit.setOnClickListener {
            // 選択位置を末尾にする
            moneyEdit.setSelection(moneyEdit.length())
        }
        editTimes.setOnClickListener {
            // 選択位置を末尾にする
            editTimes.setSelection(editTimes.length())
        }
        memoEdit.setOnClickListener {
            // 選択位置を末尾にする
            memoEdit.setSelection(memoEdit.length())
        }
        moneyEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // 自動で入力幅を広げる
                emsAutoSet()
                perTimesCalculation()
            }
        })
        dayText.setOnClickListener {
            // カレンダーを表示する
            showDatePicker()
        }
        // QuickDaySet ボタンを使用したときの処理
        m7d.setOnClickListener {
            setDayQuick(-7)
        }
        m1d.setOnClickListener {
            setDayQuick(-1)
        }
        today.setOnClickListener {
            setDayQuick(0)
        }
        p1d.setOnClickListener {
            setDayQuick(1)
        }
        p7d.setOnClickListener {
            setDayQuick(7)
        }
        // スイッチの状態に応じて split_option を表示
        splitSwitch.setOnCheckedChangeListener { _, isChecked ->
            splitOption.visibility = if (isChecked) View.VISIBLE else View.GONE
            editTimes.setText(getString(R.string.one))
            perTimesCalculation()
        }
        editTimes.addTextChangedListener(object : TextWatcher {
            // 回数を変更した時計算をやり直す
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                perTimesCalculation()
            }
        })
        memoAddButton.setOnClickListener {
            // メモ欄を表示して追加のボタンを消す
            memoAddButton.visibility = View.GONE
            memoEdit.visibility = View.VISIBLE
        }
        pictureAdd.setOnClickListener {
            // ファイルを追加するときの画面を表示
            fileIntent.apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            startActivityForResult(fileIntent, READ_REQUEST_CODE)
        }
        picturePhoto.setOnClickListener {
            // 写真を撮影する画面を表示
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
        }
        pictureDelete.setOnClickListener {
            deletePicture()
        }
        shortcutAdd.setOnClickListener {
            addShortcut()
        }
    }

    private fun modeCheck() {

        // 呼び出されたモードをチェック

        val intent = intent
        when (intent.getStringExtra("mode")) {
            "Wish" -> {
                mode = "Wish"
                id = intent.getStringExtra("id")
                memoEdit.setText(intent.getStringExtra("name"))
                // メモがあればメモを表示させる
                if (memoEdit.text.isNotEmpty()) {
                    memoAddButton.visibility = View.GONE
                    memoEdit.visibility = View.VISIBLE
                }
                moneyEdit.setText(intent.getIntExtra("price", -1).toString())
                emsAutoSet()
                intent.getByteArrayExtra("picture")?.let {
                    val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                    photoImageView.setImageBitmap(bitmap)
                    showPicture()
                }
            }
            "Shortcut" -> {
                memoEdit.setText(intent.getStringExtra("name"))
                // メモがあればメモを表示させる
                if (memoEdit.text.isNotEmpty()) {
                    memoAddButton.visibility = View.GONE
                    memoEdit.visibility = View.VISIBLE
                }
                moneyEdit.setText(intent.getIntExtra("price", -1).toString())
                emsAutoSet()
            }
        }
    }

// ここから各ボタンごとの処理

    private fun plusMinus() {
        // プラスマイナスを切り替えて表示も切り替える
        sign = !sign
        plusMinusButton.setText(if (sign) R.string.plus else R.string.minus)
    }

    fun emsAutoSet() {
        // 文字列の幅を適切にする
        // 0 ~ 2 : 1em / 3 ~ 4 : 2em / 5 ~ 6 : 3em / 7 ~ 8 : 4em
        // 1em につき数字2文字の幅を確保します

        // maxとかminとかでめちゃくちゃしなくても
        // Kotlinでは coerceなんちゃらで結果の範囲を指定できるみたいです。
        // AtLeastで最低値 Inで範囲 coerceAtMostで最大値を指定できるみたい。
        // 詳しくは https://pouhon.net/kotlin-coerce/3967/ 便利だね

        moneyEdit.setEms(((moneyEdit.length() + 1) / 2).coerceAtLeast(1))
    }

    private fun perTimesCalculation() {

        // なんちゃら 円／回 って表示を計算して更新します
        // emsAutoSet関数で説明しているのと同じのを使ってます

        perTimes.text = getString(
            R.string.pertimes,
            (cFunc.editToInt(moneyEdit) ?: 0).coerceAtLeast(0) / (cFunc.editToInt(editTimes)
                ?: 1).coerceAtLeast(1)
        )
    }

    private fun showDatePicker() {
        // カレンダーを表示する
        if (!busyFlag) {
            busyFlag = true
            // DatePickerDialogでカレンダー式の選択画面を作れる
            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    date = java.util.Date()
                    // UserSetDate に選択された日付を格納
                    userSetDate = SimpleDateFormat(
                        "yyyy/M/d", Locale.JAPANESE
                    ).parse(getString(R.string.dateformat, year, month, dayOfMonth))!!
                    // フォーマットを作成
                    val sdFormat = SimpleDateFormat("yyyy/MM/dd", Locale.JAPANESE)
                    // 表示テキストを作成
                    dayText.text = sdFormat.format(userSetDate)
                    // 日付の差を計算する
                    val dateDiff = dateDiff(sdFormat.format(userSetDate), sdFormat.format(date))
                    // 差に応じて表示を変更する
                    diffShow(dateDiff)
                }, // Dateピッカーの初期値にUserSetDateを設定
                SimpleDateFormat("yyyy", Locale.JAPANESE).format(userSetDate).toInt(),
                SimpleDateFormat("MM", Locale.JAPANESE).format(userSetDate).toInt(),
                SimpleDateFormat("dd", Locale.JAPANESE).format(userSetDate).toInt()
            )
            // 表示します
            datePickerDialog.show()

            busyFlag = false
        }
    }

    private fun dateDiff(dateFromString: String, dateToString: String): Int {
        val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.JAPANESE)
        // Date型に変換
        return try {
            val dateFrom = sdf.parse(dateFromString)
            val dateTo = sdf.parse(dateToString)
            // 差分の日数を計算する
            val dayDiff = (dateTo!!.time - dateFrom!!.time) / (1000 * 60 * 60 * 24)
            dayDiff.toInt()
        } catch (e: ParseException) {
            e.printStackTrace()
            0
        }
    }

    private fun setDayQuick(num: Int) {
        date = java.util.Date()
        when (num) {

            // 0 の場合、今日と同じにする
            0 -> userSetDate = date

            else -> {
                // 一度 calendar型 に変換して .add を使用する
                val calendar: Calendar = Calendar.getInstance()
                calendar.time = userSetDate
                calendar.add(Calendar.DAY_OF_MONTH, num)
                // Date型に戻す
                userSetDate = calendar.time
            }
        }
        // UserSetDateを表示する
        dayText.text = SimpleDateFormat("yyyy/MM/dd", Locale.JAPANESE).format(userSetDate)
        val sdFormat =
            SimpleDateFormat("yyyy/MM/dd", Locale.JAPANESE)
        // 日付の差を計算する
        val dateDiff = dateDiff(sdFormat.format(userSetDate), sdFormat.format(date))
        // 差に応じて表示を変更する
        diffShow(dateDiff)
    }

    private fun diffShow(dateDiff: Int) {
        // 差に応じて表示を変更するやつ
        when {
            dateDiff == 0 -> dayDiff.text = getString(R.string.today_parentheses)
            dateDiff > 0 -> dayDiff.text = getString(R.string.prev_day, dateDiff)
            dateDiff < 0 -> dayDiff.text = getString(R.string.next_day, dateDiff * -1)
        }
    }

    private fun showPicture() {
        // 追加したピクチャーを出して、追加ボタンを削除ボタンに差し替えます
        pictureAdd.visibility = View.GONE
        picturePhoto.visibility = View.GONE
        pictureDelete.visibility = View.VISIBLE
        photoImageView.visibility = View.VISIBLE
    }

    private fun deletePicture() {
        // 追加したピクチャーを消して、削除ボタンを追加ボタンに差し替えます
        checkIntent()
        pictureDelete.visibility = View.GONE
        photoImageView.visibility = View.GONE
    }

    private fun insertDB(): Boolean {

        // DBに登録するときに呼び出されます

        try {
            val dbHelper = SQLiteDB(applicationContext, "SaifuDB", null, 1)
            val database = dbHelper.writableDatabase // 書き込み可能

            // log表
            // inputDate primary key,payDate,name,price,category,splitCount,picture

            val inputDate = java.util.Date()
            // Bitmapに画像があれば取得 なければNull
            val bmp: Bitmap? = (photoImageView.drawable as BitmapDrawable?)?.bitmap
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
                        userSetDate
                    )
                )
                put("name", memoEdit.text.toString())
                put("price", cFunc.editToInt(moneyEdit))
                put("category", 0)
                put("splitCount", cFunc.editToInt(editTimes))
                // bmpがnullでなければ、ByteArray型にbmpを変換する
                bmp?.let { put("picture", cFunc.getBinaryFromBitmap(it)) }
                put("sign", if (sign) 1 else 0)
            }
            // DBに登録する できなければエラーを返す
            database.insertOrThrow("log", null, values)
            if (mode == "Wish") {
                // 欲しい物リストから登録した場合、購入したとみなして欲しい物を削除する
                id?.let { deleteDB(it) }
            }
            finish() // 登録できたら画面を閉じる
            return true
        } catch (exception: Exception) {
            Toast.makeText(this, "データ登録エラー", Toast.LENGTH_LONG).show()
            Log.e("insertData", exception.toString()) // エラーをログに出力
            return false
        }
    }

    fun deleteDB(whereId: String) {
        try {
            val dbHelper = SQLiteDB(applicationContext, "SaifuDB", null, 1)
            val database = dbHelper.writableDatabase

            val whereClauses = "id = ?"
            val whereArgs = arrayOf(whereId)
            database.delete("wish", whereClauses, whereArgs)
        } catch (exception: Exception) {
            Log.e("deleteData", exception.toString())
        }
    }

    private fun addShortcut() {
        // DBに登録するときに呼び出されます

        try {
            if (!addShortcutflag) {
                val dbHelper = SQLiteDB(applicationContext, "SaifuDB", null, 1)
                val database = dbHelper.writableDatabase // 書き込み可能

                // shortcut表
                // id INTEGER primary key,name,price,category

                val inputDate = java.util.Date()
                // Bitmapに画像があれば取得 なければNull
                val bmp: Bitmap? = (photoImageView.drawable as BitmapDrawable?)?.bitmap
                // INSERTするのに必要なデータをvalueにまとめる
                val values = ContentValues()
                values.run {
                    put("name", memoEdit.text.toString())
                    put("price", cFunc.editToInt(moneyEdit))
                    put("category", 0)
                }
                // DBに登録する できなければエラーを返す
                database.insertOrThrow("shortcut", null, values)
                addShortcutflag = true
                Toast.makeText(this, "追加しました。", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "すでに登録済みです。", Toast.LENGTH_LONG).show()
            }
        } catch (exception: Exception) {
            Toast.makeText(this, "データ登録エラー", Toast.LENGTH_LONG).show()
            Log.e("insertData", exception.toString()) // エラーをログに出力
        }
    }
}
