package com.ze20.saifu.ui.wish

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
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
                    arrayListlayout.add(View.inflate(context, R.layout.fragment_wish_list, null))
                    root.linearLayout.addView(arrayListlayout[arrayListlayout.size - 1].apply {
                        idText.text = cursor.getInt(0).toString()
                        nameText.text = cursor.getString(1)
                        priceText.text =
                            getString(R.string.currency) + ("%,d".format(cursor.getInt(2)))
                        urlText.text = cursor.getString(3)
                        val blob: ByteArray? = cursor.getBlob(4)
                        blob?.let {
                            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                            itemImage.setImageBitmap(bitmap)
                        }
                        setListenter(this)
                    })
                    cursor.moveToNext()
                }
            }
        } catch (exception: Exception) {
            Log.e("selectData", exception.toString());
        }
    }

    private fun setListenter(view: View) {
        view.showHideButton.setOnClickListener {
            view.infoLayout.visibility =
                if (view.infoLayout.visibility == View.VISIBLE) {
                    view.showHideButton.setImageResource(R.drawable.ic_baseline_arrow_downward_24)
                    View.GONE
                } else {
                    view.showHideButton.setImageResource(R.drawable.ic_baseline_arrow_upward_24)
                    View.VISIBLE
                }
        }
        view.deleteButton.setOnClickListener {
            deleteData(view.idText.text.toString())
            reload()
        }
    }

    fun reload() {
        val inflater =
            activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        root = inflater.inflate(R.layout.fragment_wish, null)
        databaseCheck()
        (getView() as ViewGroup?)?.let {
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
            R.id.plusButton -> {
                startActivity(Intent(activity, AddWishActivity::class.java))
                reload()
            }
        }
        return true
    }

    private fun deleteData(whereId: String) {
        try {
            val dbHelper = SQLiteDB(requireContext(), "SaifuDB", null, 1)
            val database = dbHelper.writableDatabase

            val whereClauses = "id = ?"
            val whereArgs = arrayOf(whereId)
            database.delete("wish", whereClauses, whereArgs)
        } catch (exception: Exception) {
            Log.e("deleteData", exception.toString())
        }
    }
}
