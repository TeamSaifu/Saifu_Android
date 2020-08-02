package com.ze20.saifu.ui.wish

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ze20.saifu.AddWishActivity
import com.ze20.saifu.R
import com.ze20.saifu.SQLiteDB
import kotlinx.android.synthetic.main.fragment_wish.view.*
import kotlinx.android.synthetic.main.fragment_wish_list.view.*

lateinit var root: View

class WishFragment : Fragment() {

    private var arrayListlayout: ArrayList<View> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = View.inflate(context, R.layout.fragment_wish, null)

        databaseCheck()

        return root
    }

    override fun onResume() {
        super.onResume()
        reload()
    }

    private fun databaseCheck() {

        // データベースをチェックしてレイアウトを作成する

        try {

            val dbHelper = SQLiteDB(requireContext(), "SaifuDB", null, 1)
            val database = dbHelper.readableDatabase

            // wish表
            // id primary key,name,price,url,picture

            val sql =
                "select * from wish order by id"

            val cursor = database.rawQuery(sql, null)
            if (cursor.count > 0) {
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    // arrayListlayoutに新たなレイアウトを作成
                    arrayListlayout.add(View.inflate(context, R.layout.fragment_wish_list, null))
                    // 各種処理をしたあとにビューにレイアウトを追加する
                    root.linearLayout.addView(
                        arrayListlayout[arrayListlayout.size - 1].apply {
                            // データベースの内容を作ったレイアウトに流し込む
                            idText.text = cursor.getInt(0).toString()
                            nameText.text = cursor.getString(1)
                            priceText.text =
                                getString(R.string.currencyString, "%,d".format(cursor.getInt(2)))
                            urlText.text = cursor.getString(3)
                            // URLが5文字以下ならURLボタンを消す
                            webButton.visibility =
                                if (urlText.length() < 5) View.INVISIBLE else View.VISIBLE
                            val blob: ByteArray? = cursor.getBlob(4)
                            // 画像ファイルがあれば復元
                            blob?.let {
                                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                                itemImage.setImageBitmap(bitmap)
                            }
                            // クリックリスナーなどをセット
                            setListener(this, cursor.getInt(2), cursor.getBlob(4))
                        }
                    )
                    // 次の項目へ
                    cursor.moveToNext()
                }
                cursor.close()
            }
        } catch (exception: Exception) {
            Log.e("selectData", exception.toString())
        }
    }

    private fun setListener(view: View, price: Int, picture: ByteArray?) {

        // リスナーをセットします

        view.showHideButton.setOnClickListener {
            // infoLayout の表示を切り替えてアイコンを変更する
            view.infoLayout.visibility =
                if (view.infoLayout.visibility == View.GONE) {
                    view.showHideButton.setImageResource(R.drawable.ic_baseline_arrow_upward_24)
                    View.VISIBLE
                } else {
                    view.showHideButton.setImageResource(R.drawable.ic_baseline_arrow_downward_24)
                    View.GONE
                }
        }
        view.webButton.setOnClickListener {
            // webを表示するボタン
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(view.urlText.text.toString())))
            } catch (exception: Exception) {
                Toast.makeText(activity, "URLが不正です。", Toast.LENGTH_LONG).show()
                Log.e("webAccess", exception.toString())
            }
        }
        view.editButton.setOnClickListener {
            // 項目を編集／削除するボタン
            val intent = Intent(activity, AddWishActivity::class.java)
            intent.putExtra("mode", "Edit")
            intent.putExtra("id", view.idText.text.toString())
            intent.putExtra("name", view.nameText.text.toString())
            intent.putExtra("price", price)
            intent.putExtra("url", view.urlText.text.toString())
            picture?.let { intent.putExtra("picture", it) }
            startActivity(intent)
            reload()
            // wishF.deleteData(id)
        }
    }

    private fun reload() {

        // 表示を更新するメソッドです

        root = View.inflate(context, R.layout.fragment_wish, null)
        databaseCheck()
        (view as ViewGroup?)?.let {
            it.removeAllViews()
            it.addView(root)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.setTitle((R.menu.menu_wishlist))
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_wishlist, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // plusボタンが押されたとき
            R.id.plusButton -> {
                startActivity(Intent(activity, AddWishActivity::class.java))
                reload()
            }
        }
        return true
    }
}
