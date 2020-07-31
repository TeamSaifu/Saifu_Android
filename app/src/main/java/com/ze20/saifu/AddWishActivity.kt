package com.ze20.saifu

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_wish.*

class AddWishActivity : AppCompatActivity() {

    private val fileIntent = Intent(Intent.ACTION_OPEN_DOCUMENT) // ファイルの選択
    private val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) // カメラ撮影
    private val cFunc = ConvenientFunction()
    var busyFlag: Boolean = false
    companion object {
        private const val REQUEST_IMAGE_CAPTURE: Int = 1
        private const val READ_REQUEST_CODE: Int = 42
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_wish)
        setTitle(R.string.title_new)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        checkIntent()
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

        when (item.itemId) {

            // 登録ボタンを押した時の処理です

            R.id.applyButton -> {
                if (!busyFlag) {
                    busyFlag = true
                    try {
                        val dbHelper = SQLiteDB(applicationContext, "SaifuDB", null, 1)
                        val database = dbHelper.writableDatabase // 書き込み可能

                        // wish表
                        // id primary key,name,price,url,picture

                        // Bitmapに画像があれば取得 なければNull
                        val bmp: Bitmap? = (photoImageView.drawable as BitmapDrawable?)?.bitmap
                        // INSERTするのに必要なデータをvalueにまとめる
                        val values = ContentValues()
                        values.run {
                            put("name", nameEdit.text.toString())
                            put("price", cFunc.editToInt(priceEdit))
                            put("url", urlEdit.text.toString())
                            bmp?.let { put("picture", cFunc.getBinaryFromBitmap(it)) }
                        }
                        // DBに登録する できなければエラーを返す
                        database.insertOrThrow("wish", null, values)
                        finish() // 登録できたら画面を閉じる
                        return true
                    } catch (exception: Exception) {
                        Toast.makeText(this, "データ登録エラー", Toast.LENGTH_LONG).show()
                        Log.e("insertData", exception.toString()) // エラーをログに出力
                        busyFlag = false
                        return false
                    }
                }
            }

            // 戻るボタンならこっちになる
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
}
