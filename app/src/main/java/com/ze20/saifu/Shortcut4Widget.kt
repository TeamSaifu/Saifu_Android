package com.ze20.saifu

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.view.View
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 */
class Shortcut4Widget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // 複数のウィジェットがアクティブになっている可能性があるため、それらをすべて更新してください
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // 最初のウィジェットが作成されたときの関連機能を入力します
    }

    override fun onDisabled(context: Context) {
        // 最後のウィジェットが無効になったときの関連機能を入力します
    }
}

internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    // RemoteViewsオブジェクトを構築する
    val views = RemoteViews(context.packageName, R.layout.shortcut4_widget)
    val SQLiteDB = SQLiteDB(context, "SaifuDB", null, 1)
    val database = SQLiteDB.readableDatabase
    val sql = "select * from shortcut order by 1 asc limit 4;"
    val cursor = database.rawQuery(sql, null)
    val shortcut: ArrayList<Int> =
        arrayListOf(R.id.shurtcut_button1, R.id.shurtcut_button2, R.id.shurtcut_button3, R.id.shurtcut_button4)
    var i = 0
    if (cursor.count > 0) {
        cursor.moveToFirst()
        if (cursor.count <= 2) {
            views.setViewVisibility(R.id.layout2, View.GONE)
        }
        while (!cursor.isAfterLast) {
            views.setViewVisibility(R.id.textView, View.GONE)
            views.setViewVisibility(R.id.button4Layout, View.VISIBLE)
            if (cursor.getString(1) == "") {
                views.setTextViewText(shortcut[i], context.getString(R.string.currencyString, "%,d".format(cursor.getInt(2))))
                //views.setTextViewTextSize(shortcut[i], 0, 72.0f)
            } else {
                views.setTextViewText(shortcut[i], context.getString(R.string.shortcutformat, cursor.getString(1), "%,d".format(cursor.getInt(2))))
            }
            views.setViewVisibility(shortcut[i], View.VISIBLE)
            /*shortcut[i].setOnClickListener {
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
            }*/
            i++
            cursor.moveToNext()
        }
    } else {
    }
    cursor.close()
    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}
