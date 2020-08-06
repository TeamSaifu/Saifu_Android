package com.ze20.saifu

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_wish.*

class AddWishActivity : AppCompatActivity() {

    private val fileIntent = Intent(Intent.ACTION_OPEN_DOCUMENT) // ファイルの選択
    private val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) // カメラ撮影
    private val cFunc = ConvenientFunction()
    private var busyFlag: Boolean = false
    private var mode = "New"
    var id: String? = null

    companion object {
        private const val REQUEST_IMAGE_CAPTURE: Int = 1
        private const val READ_REQUEST_CODE: Int = 42
        private const val DB_INSERT: Int = 10
        private const val DB_UPDATE: Int = 11
        private const val DB_DELETE: Int = 12
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_wish)
        setTitle(R.string.title_new)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        cFunc.checkIntent(this, intent, pictureAdd)
        cFunc.checkIntent(this, intent, picturePhoto)
        modecheck()
        setListeners()
    }

    private fun modecheck() {

        // 呼び出されたモードをチェック

        val intent = intent
        when (intent.getStringExtra("mode")) {
            "Edit" -> {
                mode = "Edit"
                id = intent.getStringExtra("id")
                nameEdit.setText(intent.getStringExtra("name"))
                priceEdit.setText(intent.getIntExtra("price", -1).toString())
                urlEdit.setText(intent.getStringExtra("url"))
                if (urlEdit.text.isNotEmpty()) {
                    urlAdd.visibility = View.GONE
                    urlLayout.visibility = View.VISIBLE
                }
                intent.getByteArrayExtra("picture")?.let {
                    val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                    photoImageView.setImageBitmap(bitmap)
                    if (it.size != 1) showPicture()
                }
                deleteItemButton.visibility = View.VISIBLE
            }
        }
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
        cFunc.photoOrCamera(
            this,
            contentResolver,
            requestCode,
            resultCode,
            resultData,
            photoImageView
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            // 登録ボタンを押した時の処理です

            R.id.applyButton -> {
                when (mode) {
                    "New" -> DBProcess(DB_INSERT)
                    "Edit" -> DBProcess(DB_UPDATE, id)
                }
            }

            // 戻るボタンならこっちになる
            else -> {
                finish()
            }
        }
        return true
    }

    private fun setListeners() {

        mainLayout.setOnClickListener {
            // 画面のどこかおしたらキーボードを消す
            cFunc.hideKeyboard(this, this@AddWishActivity.currentFocus)
        }
        urlAdd.setOnClickListener {
            urlAdd.visibility = View.GONE
            urlLayout.visibility = View.VISIBLE
        }
        pasteButton.setOnClickListener {
            urlEdit.setText(cFunc.getClip(applicationContext) ?: "")
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
        deleteItemButton.setOnClickListener {
            id?.let {
                DBProcess(DB_DELETE, it)
                finish()
            }
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
        photoImageView.setImageDrawable(null)
        cFunc.checkIntent(this, intent, pictureAdd)
        cFunc.checkIntent(this, intent, picturePhoto)
        pictureDelete.visibility = View.GONE
        photoImageView.visibility = View.GONE
    }

    private fun DBProcess(mode: Int, id: String? = null): Boolean {

        if (!busyFlag) {
            busyFlag = true
            var tag = "GetData"
            try {
                val dbHelper = SQLiteDB(applicationContext, "SaifuDB", null, 1)
                val database = dbHelper.writableDatabase // 書き込み可能

                // wish表
                // id primary key,name,price,url,picture

                // Bitmapに画像があれば取得 なければNull
                val bmp: Bitmap? = (photoImageView.drawable as BitmapDrawable?)?.bitmap
                // UPDATEするのに必要なデータをvalueにまとめる
                val values = ContentValues()
                values.run {
                    put("name", nameEdit.text.toString())
                    put("price", cFunc.editToInt(priceEdit))
                    put("url", urlEdit.text.toString())
                    bmp?.let { put("picture", cFunc.getBinaryFromBitmap(it)) }
                        ?: this.put("picture", "")
                }
                when (mode) {
                    DB_INSERT -> {
                        tag = "InsertData"
                        // DBに登録する できなければエラーを返す
                        database.insertOrThrow("wish", null, values)
                        finish() // 登録できたら画面を閉じる
                        return true
                    }
                    DB_UPDATE -> {
                        tag = "UpdateData"
                        // DBを更新する
                        val whereClauses = "id = ?"
                        val whereId = id!!
                        val whereArgs = arrayOf(whereId)
                        database.update("wish", values, whereClauses, whereArgs)
                        finish() // 登録できたら画面を閉じる
                        return true
                    }
                    DB_DELETE -> {
                        tag = "DeleteData"
                        // DBの項目を削除
                        val whereClauses = "id = ?"
                        val whereArgs = arrayOf(id!!)
                        database.delete("wish", whereClauses, whereArgs)
                        busyFlag = false
                        finish()
                        return true
                    }
                }
            } catch (exception: Exception) {
                Toast.makeText(this, getString(R.string.DataError), Toast.LENGTH_LONG).show()
                Log.e(tag, exception.toString()) // エラーをログに出力
                return false
            }
        }
        return true
    }
}
