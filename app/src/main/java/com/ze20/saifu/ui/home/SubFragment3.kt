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
import com.ze20.saifu.R
import com.ze20.saifu.SQLiteDBClass
import com.ze20.saifu.UtilityFunClass
import com.ze20.saifu.ui.input.DataInputActivity
import kotlinx.android.synthetic.main.activity_sub_fragment3.view.*

class SubFragment3 : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_sub_fragment3, container, false)
        reload(view)
        return view
    }

    override fun onResume() {
        super.onResume()
        reload(requireView())
    }

    private fun reload(view: View) {
        val shortcut: ArrayList<Button> =
            arrayListOf(
                view.shortcut_button13,
                view.shortcut_button14,
                view.shortcut_button15,
                view.shortcut_button16,
                view.shortcut_button17,
                view.shortcut_button18,
                view.shortcut_button19,
                view.shortcut_button20
            )
        val nameal: ArrayList<String> = arrayListOf()
        val priceal: ArrayList<Int> = arrayListOf()
        val categoryal: ArrayList<Int> = arrayListOf()
        val signal: ArrayList<Int> = arrayListOf()
        val SQLiteDB = SQLiteDBClass(requireContext(), "SaifuDB", null, 1)
        val database = SQLiteDB.readableDatabase
        val sql = "select * from shortcut order by 1 asc limit 8 offset 12;"
        val cursor = database.rawQuery(sql, null)

        var i = 0
        if (cursor.count > 0) {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                if (cursor.getString(1) == "") {
                    shortcut[i].text = (if (cursor.getInt(4) == 1) "+" else "") + getString(
                        R.string.currencyString,
                        "%,d".format(cursor.getInt(2))
                    )
                    shortcut[i].setTextSize(30.0f)
                } else {
                    shortcut[i].text = getString(
                        R.string.shortcutformat,
                        cursor.getString(1),
                        "%,d".format(cursor.getInt(2)),
                        if (cursor.getInt(4) == 1) "+" else ""
                    )
                }
                nameal.add(cursor.getString(1))
                priceal.add(cursor.getInt(2))
                categoryal.add(cursor.getInt(3))
                signal.add(cursor.getInt(4))
                shortcut[i].tag = i
                shortcut[i].visibility = View.VISIBLE
                shortcut[i].setOnClickListener {
                    val sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
                    if (sharedPref.getBoolean("shortCutQuickAdd", false)) {
                        val tag = it.tag as Int
                        it.isEnabled = false
                        if (UtilityFunClass().quickInsert(
                                context,
                                priceal[tag],
                                nameal[tag],
                                categoryal[tag],
                                signal[tag]
                            )) {
                            Toast.makeText(
                                activity,
                                getString(R.string.recordFinish),
                                Toast.LENGTH_LONG
                            ).show()
                            activity?.recreate()
                            it.isEnabled = true
                        } else {
                            Toast.makeText(
                                activity,
                                getString(R.string.recordError),
                                Toast.LENGTH_LONG
                            ).show()
                            it.isEnabled = true
                        }
                    } else {
                        val intent = Intent(activity, DataInputActivity::class.java)
                        val tag = it.tag as Int
                        intent.putExtra("mode", "Shortcut")
                        intent.putExtra("name", nameal[tag])
                        intent.putExtra("price", priceal[tag])
                        intent.putExtra("category", categoryal[tag])
                        intent.putExtra("sign", signal[tag] == 1)
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
