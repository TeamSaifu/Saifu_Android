package com.ze20.saifu

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.preference.PreferenceManager
import com.ze20.saifu.ui.input.DataInputActivity

/**
 * Implementation of App Widget functionality.
 */
class ShortcutShow4Widget : AppWidgetProvider() {

    companion object {
        const val BUTTON1 = "SHORTCUT_1"
        const val BUTTON2 = "SHORTCUT_2"
        const val BUTTON3 = "SHORTCUT_3"
        const val BUTTON4 = "SHORTCUT_4"
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // 複数のウィジェットがアクティブになっている可能性があるため、それらをすべて更新してください
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(
                context, appWidgetManager, appWidgetId
            )
        }
    }

    override fun onEnabled(context: Context) {
        // 最初のウィジェットが作成されたときの関連機能を入力します
    }

    override fun onDisabled(context: Context) {
        // 最後のウィジェットが無効になったときの関連機能を入力します
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        // 押されたボタンに応じて処理を変更
        // （意味ないように見えるけど1つにしたらおかしくなったのでこのままに）

        when (intent.action) {
            BUTTON1 -> shortcut(
                context, intent
            )
            BUTTON2 -> shortcut(
                context, intent
            )
            BUTTON3 -> shortcut(
                context, intent
            )
            BUTTON4 -> shortcut(
                context, intent
            )
        }
    }

    private fun shortcut(context: Context, intent: Intent) {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        val name = intent.getStringExtra("name")
        val price = intent.getIntExtra(
            "price", 0
        )
        val category = intent.getIntExtra(
            "category", 0
        )
        val sign = intent.getIntExtra(
            "signpm", -1
        )

        // ショートカット即時がオンがどうかの確認

        if (sharedPref.getBoolean(
                "shortCutQuickAdd", false
            )
        ) {
            // オンならばquickInsertを起動して即時登録
            if (UtilityFunClass().quickInsert(
                    context, price, name!!, category, sign
                )
            ) {
                Toast.makeText(
                    context, context.getString(R.string.recordFinish), Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    context, context.getString(R.string.recordError), Toast.LENGTH_LONG
                ).show()
            }
        } else {
            // オフならば情報を入力した状態のデータ入力画面を開く
            val dataIntent = Intent(
                context, DataInputActivity::class.java
            )
            dataIntent.putExtra(
                "mode", "Shortcut"
            )
            dataIntent.putExtra(
                "price", price
            )
            dataIntent.putExtra(
                "name", name
            )
            dataIntent.putExtra(
                "category", category
            )
            dataIntent.putExtra("sign", sign == 1)
            startActivity(
                context, dataIntent, null
            )
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        // RemoteViewsオブジェクトを構築する
        val views = RemoteViews(
            context.packageName, R.layout.shortcut4_widget
        )
        val sqLiteDB = SQLiteDBClass(
            context, "SaifuDB", null, 1
        )
        val database = sqLiteDB.readableDatabase
        val sql = "select * from shortcut order by 1 asc limit 4;"
        val cursor = database.rawQuery(
            sql, null
        )
        val price: ArrayList<Int> = arrayListOf()
        val name: ArrayList<String> = arrayListOf()
        val category: ArrayList<Int> = arrayListOf()
        val sign: ArrayList<Int> = arrayListOf()
        val shortcut: ArrayList<Int> = arrayListOf(
            R.id.shortcut_button1, R.id.shortcut_button2, R.id.shortcut_button3,
            R.id.shortcut_button4
        )
        var i = 0
        // ボタンに情報を設定
        if (cursor.count > 0) {
            cursor.moveToFirst()
            if (cursor.count <= 2) {
                views.setViewVisibility(
                    R.id.layout2, View.GONE
                )
            }
            while (!cursor.isAfterLast) {
                views.setViewVisibility(
                    R.id.textView, View.GONE
                )
                views.setViewVisibility(
                    R.id.button4Layout, View.VISIBLE
                )
                if (cursor.getString(1) == "") {
                    views.setTextViewText(
                        shortcut[i],
                        (if (cursor.getInt(4) == 1) "+" else "") + context.getString(
                            R.string.currencyString,
                            "%,d".format(cursor.getInt(2))
                        )
                    )
                } else {
                    views.setTextViewText(
                        shortcut[i],
                        context.getString(
                            R.string.shortcutformat, cursor.getString(1),
                            "%,d".format(cursor.getInt(2)),
                            if (cursor.getInt(4) == 1) "+" else ""
                        )
                    )
                }
                price.add(cursor.getInt(2))
                name.add(cursor.getString(1))
                category.add(cursor.getInt(3))
                sign.add(cursor.getInt(4))
                views.setViewVisibility(
                    shortcut[i], View.VISIBLE
                )
                i++
                cursor.moveToNext()
            }
        }
        cursor.close()
        // エラーが出るまでボタンにオンクリックをセットする
        try {
            views.setOnClickPendingIntent(
                R.id.shortcut_button1,
                PendingIntent.getBroadcast(
                    context, appWidgetId,
                    Intent(
                        context, ShortcutShow4Widget::class.java
                    ).apply {
                        action = BUTTON1
                        putExtra("mode", "Shortcut")
                        putExtra(
                            "price", price[0]
                        )
                        putExtra(
                            "name", name[0]
                        )
                        putExtra(
                            "category", category[0]
                        )
                        putExtra(
                            "signpm", sign[0]
                        )
                    },
                    0
                )
            )
            views.setOnClickPendingIntent(
                R.id.shortcut_button2,
                PendingIntent.getBroadcast(
                    context, appWidgetId,
                    Intent(
                        context, ShortcutShow4Widget::class.java
                    ).apply {
                        action = BUTTON2
                        putExtra("mode", "Shortcut")
                        putExtra("price", price[1])
                        putExtra("name", name[1])
                        putExtra("category", category[1])
                        putExtra(
                            "signpm", sign[1]
                        )
                    },
                    0
                )
            )
            views.setOnClickPendingIntent(
                R.id.shortcut_button3,
                PendingIntent.getBroadcast(
                    context, appWidgetId,
                    Intent(
                        context, ShortcutShow4Widget::class.java
                    ).apply {
                        action = BUTTON3
                        putExtra("mode", "Shortcut")
                        putExtra(
                            "price", price[2]
                        )
                        putExtra(
                            "name", name[2]
                        )
                        putExtra(
                            "category", category[2]
                        )
                        putExtra(
                            "signpm", sign[2]
                        )
                    },
                    0
                )
            )
            views.setOnClickPendingIntent(
                R.id.shortcut_button4,
                PendingIntent.getBroadcast(
                    context, appWidgetId,
                    Intent(
                        context, ShortcutShow4Widget::class.java
                    ).apply {
                        action = BUTTON4
                        putExtra("mode", "Shortcut")
                        putExtra(
                            "price", price[3]
                        )
                        putExtra(
                            "name", name[3]
                        )
                        putExtra(
                            "category", category[3]
                        )
                        putExtra(
                            "signpm", sign[3]
                        )
                    },
                    0
                )
            )
        } catch (expected: IndexOutOfBoundsException) {
            // IndexOutOfBoundsException エラーが出たら処理を終える
        }
        // ウィジェットを更新するようにウィジェットマネージャーに指示する
        appWidgetManager.updateAppWidget(
            appWidgetId, views
        )
    }
}
