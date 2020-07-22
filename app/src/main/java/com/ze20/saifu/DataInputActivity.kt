package com.ze20.saifu

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_data_input.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar

class DataInputActivity : AppCompatActivity() {

    var date = java.util.Date() // 今日の日付を格納
    var sign = false // プラス・マイナス
    var UserSetDate: java.util.Date = java.util.Date() // 設定された日付・初期値は今日
    var BusyFlag = false // ローディング中はいろいろ動かなくするためのフラグ

    val fileintent = Intent(Intent.ACTION_OPEN_DOCUMENT) //ファイルの選択
    val cameraintent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) //カメラ撮影

    companion object {
        private const val REQUEST_IMAGE_CAPTURE: Int = 1
        private const val READ_REQUEST_CODE: Int = 42
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_input)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle(R.string.title_data_input)

        // UserSetDateを表示しておく
        day_text.setText(SimpleDateFormat("yyyy/MM/dd").format(UserSetDate))
        checkintent()
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
                val imageView: ImageView = findViewById<ImageView>(R.id.photo_imageView)

                resultData?.getExtras().also {
                    bitmap = resultData?.getExtras()?.get("data") as Bitmap
                    bitmap.also {
                        imageView.setImageBitmap(bitmap)
                    }
                }
                showpicture()
            }
            READ_REQUEST_CODE -> {
                try {
                    resultData?.data?.also { uri ->
                        val inputStream = contentResolver?.openInputStream(uri)
                        val image = BitmapFactory.decodeStream(inputStream)
                        val imageView = findViewById<ImageView>(R.id.photo_imageView)
                        imageView.setImageBitmap(image)
                        showpicture()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "エラーが発生しました", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    protected fun checkintent() {

        // 画像ギャラリーがあるかどうか確認

        val activities: List<ResolveInfo> = packageManager.queryIntentActivities(
            intent,
            PackageManager.MATCH_ALL
        )
        val isIntentSafe: Boolean = activities.isNotEmpty()

        when (isIntentSafe) {
            // なければボタンが消滅
            false -> picture_add.visibility = View.GONE
            true -> picture_add.visibility = View.VISIBLE
        }

        // カメラアプリがあるかどうか確認
        val cameraactivities: List<ResolveInfo> = packageManager.queryIntentActivities(
            cameraintent,
            PackageManager.MATCH_ALL
        )
        val iscameraIntentSafe: Boolean = cameraactivities.isNotEmpty()

        when (iscameraIntentSafe) {
            // なければボタンが消滅
            false -> picture_photo.visibility = View.GONE
            true -> picture_photo.visibility = View.VISIBLE
        }
    }

    protected fun setListeners() {

        // クリック時とかのリスナーをセットするとこ

        MainLayout.setOnClickListener {
            // 画面のどこかおしたらキーボードを消す
            hideKeyboard()
        }

        plus_minus.setOnClickListener { view ->
            // プラスマイナス切り替え
            plusminus()
        }
        money_text.setOnClickListener() {
            // 選択位置を末尾にする
            money_text.setSelection(money_text.length())
        }
        money_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // 自動で入力幅を広げる
                emsauto()
                pertimes.text = (getString(
                    R.string.pertimes,
                    Math.max(edittoInt(money_text) ?: 0, 0) / Math.max(edittoInt(edittimes) ?: 1, 1)
                ))
            }
        })
        day_text.setOnClickListener() {
            // カレンダーを表示する
            showDatePicker()
        }
        // QuickDaySet ボタンを使用したときの処理
        m7d.setOnClickListener() {
            setdayquick(-7)
        }
        m1d.setOnClickListener() {
            setdayquick(-1)
        }
        today.setOnClickListener() {
            setdayquick(0)
        }
        p1d.setOnClickListener() {
            setdayquick(1)
        }
        p7d.setOnClickListener() {
            setdayquick(7)
        }
        // スイッチの状態に応じて split_option を表示
        split_switch.setOnCheckedChangeListener { _, isChecked ->
            split_option.visibility = if (isChecked) View.VISIBLE else View.GONE
            pertimes.text = (getString(
                R.string.pertimes,
                Math.max(edittoInt(money_text) ?: 0, 0) / Math.max(edittoInt(edittimes) ?: 1, 1)
            ))
        }
        edittimes.addTextChangedListener(object : TextWatcher {
            // 回数を変更した時計算をやり直す
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                pertimes.text = (getString(
                    R.string.pertimes,
                    Math.max(edittoInt(money_text) ?: 0, 0) / Math.max(edittoInt(edittimes) ?: 1, 1)
                ))
            }
        })
        memo_add.setOnClickListener() {
            // メモ欄を表示して追加のボタンを消す
            memo_add.visibility = View.GONE
            memo_edit.visibility = View.VISIBLE
        }
        picture_add.setOnClickListener() {
            // ファイルを追加するときの画面を表示
            fileintent.apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            startActivityForResult(fileintent, READ_REQUEST_CODE);
        }
        picture_photo.setOnClickListener() {
            // 写真を撮影する画面を表示
            startActivityForResult(cameraintent, REQUEST_IMAGE_CAPTURE);
        }
        picture_delete.setOnClickListener() {
            deletepicture()
        }
    }

    // ここから各ボタンごとの処理

    fun plusminus() {
        // プラスマイナスを切り替えて表示も切り替える
        sign = !sign
        plus_minus.setText(if (sign) R.string.plus else R.string.minus)
    }

    fun emsauto() {
        // 文字列の幅を適切にする
        // 0 ~ 2 : 1em / 3 ~ 4 : 2em / 5 ~ 6 : 3em / 7 ~ 8 : 4em
        // 1em につき数字2文字の幅を確保します
        money_text.setEms(Math.max((money_text.length() + 1) / 2, 1))
    }

    fun showDatePicker() {
        // カレンダーを表示する
        if (BusyFlag == false) {
            BusyFlag = true
            // DatePickerDialogでカレンダー式の選択画面を作れる
            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener() { view, year, month, dayOfMonth ->
                    date = java.util.Date()
                    // UserSetDate に選択された日付を格納
                    UserSetDate = SimpleDateFormat("yyyy/M/d").parse(
                        getString(
                            R.string.dateformat,
                            year,
                            month,
                            dayOfMonth
                        )
                    )
                    //フォーマットを作成
                    val sdFormat = SimpleDateFormat("yyyy/MM/dd")
                    // 表示テキストを作成
                    day_text.setText(sdFormat.format(UserSetDate))
                    // 日付の差を計算する
                    val datediff = dateDiff(sdFormat.format(UserSetDate), sdFormat.format(date))
                    // 差に応じて表示を変更する
                    datediff_textshow(datediff)
                }, // Dateピッカーの初期値にUserSetDateを設定
                SimpleDateFormat("yyyy").format(UserSetDate).toInt(),
                SimpleDateFormat("MM").format(UserSetDate).toInt(),
                SimpleDateFormat("dd").format(UserSetDate).toInt()
            )
            // 表示します
            datePickerDialog.show()

            BusyFlag = false
        }
    }

    fun dateDiff(dateFromStrig: String?, dateToString: String?): Int {
        val sdf = SimpleDateFormat("yyyy/MM/dd")
        var dateTo: java.util.Date? = null
        var dateFrom: java.util.Date? = null
        // Date型に変換
        try {
            dateFrom = sdf.parse(dateFromStrig)
            dateTo = sdf.parse(dateToString)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        // 差分の日数を計算する
        val dateTimeTo = dateTo!!.time
        val dateTimeFrom = dateFrom!!.time
        val dayDiff = (dateTimeTo - dateTimeFrom) / (1000 * 60 * 60 * 24)
        return dayDiff.toInt()
    }

    fun setdayquick(num: Int) {
        date = java.util.Date()
        when (num) {

            // 0 の場合、今日と同じにする
            0 -> UserSetDate = date

            else -> {
                // 一度 calendar型 に変換して .add を使用する
                var calendar: Calendar = Calendar.getInstance()
                calendar.setTime(UserSetDate)
                calendar.add(Calendar.DAY_OF_MONTH, num)
                // Date型に戻す
                UserSetDate = calendar.getTime()
            }

        }
        // UserSetDateを表示する
        day_text.setText(SimpleDateFormat("yyyy/MM/dd").format(UserSetDate))
        val sdFormat =
            SimpleDateFormat("yyyy/MM/dd")
        // 日付の差を計算する
        val datediff = dateDiff(sdFormat.format(UserSetDate), sdFormat.format(date))
        // 差に応じて表示を変更する
        datediff_textshow(datediff)
    }

    fun edittoInt(edittext: EditText): Int? {
        // edittext を 数値に変換 空文字列ならNullを返す
        return edittext.text.toString().toIntOrNull()
    }

    fun datediff_textshow(datediff: Int) {
        // 差に応じて表示を変更するやつ
        when {
            (datediff == 0) -> day_text2.text = getString(R.string.Today_Parentheses)
            (datediff > 0) -> day_text2.text = getString(R.string.prev_day, datediff)
            (datediff < 0) -> day_text2.text = getString(R.string.next_day, (datediff * -1))
        }
    }

    fun hideKeyboard() {
        //キーボードを探してあれば消します
        val view = this@DataInputActivity.currentFocus
        view.let {
            val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(view!!.windowToken, 0)
        }
    }

    fun showpicture() {
        picture_add.visibility = View.GONE
        picture_photo.visibility = View.GONE
        picture_delete.visibility = View.VISIBLE
        photo_imageView.visibility = View.VISIBLE
    }

    fun deletepicture() {
        checkintent()
        picture_delete.visibility = View.GONE
        photo_imageView.visibility = View.GONE
    }
}
