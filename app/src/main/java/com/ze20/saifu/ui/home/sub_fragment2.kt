package com.ze20.saifu.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.ze20.saifu.ConvenientFunction
import com.ze20.saifu.R
import com.ze20.saifu.SQLiteDB
import com.ze20.saifu.ui.input.DataInputActivity
import kotlinx.android.synthetic.main.activity_sub_fragment2.view.*

class sub_fragment2 : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_sub_fragment2, container, false)
        reload(view)
        return view
    }

    override fun onResume() {
        super.onResume()
        reload(requireView())
    }

    private fun reload(view: View) {
        val shortcut: ArrayList<Button> =
            arrayListOf(view.shurtcut_button5, view.shurtcut_button6, view.shurtcut_button7, view.shurtcut_button8, view.shurtcut_button9, view.shurtcut_button10, view.shurtcut_button11, view.shurtcut_button12)
        val nameal: ArrayList<String> = arrayListOf()
        val priceal: ArrayList<Int> = arrayListOf()
        val categoryal: ArrayList<Int> = arrayListOf()
        val SQLiteDB = SQLiteDB(requireContext(), "SaifuDB", null, 1)
        val database = SQLiteDB.readableDatabase
        val sql = "select * from shortcut order by 1 asc limit 8 offset 4;"
        val cursor = database.rawQuery(sql, null)

        var i = 0
        if (cursor.count > 0) {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                if (cursor.getString(1) == "") {
                    shortcut[i].text = getString(R.string.currencyString, "%,d".format(cursor.getInt(2)))
                    shortcut[i].setTextSize(30.0f)
                } else {
                    shortcut[i].text = getString(R.string.shortcutformat, cursor.getString(1), "%,d".format(cursor.getInt(2)))
                }
                nameal.add(cursor.getString(1))
                priceal.add(cursor.getInt(2))
                categoryal.add(cursor.getInt(3))
                shortcut[i].tag = i
                shortcut[i].visibility = View.VISIBLE
                shortcut[i].setOnClickListener {
                    val sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
                    if (sharedPref.getBoolean("shortCutQuickAdd", false)) {
                        val tag = it.tag as Int
                        it.isEnabled = false
                        if (ConvenientFunction().quickInsert(context, priceal[tag], nameal[tag], categoryal[tag])) {
                            Toast.makeText(activity, getString(R.string.recordFinish), Toast.LENGTH_LONG).show()
                            it.isEnabled = true
                        } else {
                            Toast.makeText(activity, getString(R.string.recordError), Toast.LENGTH_LONG).show()
                            it.isEnabled = true
                        }
                    } else {
                        val intent = Intent(activity, DataInputActivity::class.java)
                        val tag = it.tag as Int
                        intent.putExtra("mode", "Shortcut")
                        intent.putExtra("name", nameal[tag])
                        intent.putExtra("price", priceal[tag])
                        intent.putExtra("category", categoryal[tag])
                        startActivity(intent)
                    }
                }
                i++
                cursor.moveToNext()
            }
        }
        cursor.close()
    }
}
