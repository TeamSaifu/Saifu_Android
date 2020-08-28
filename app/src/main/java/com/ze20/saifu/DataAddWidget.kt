package com.ze20.saifu

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.content.ContextCompat.startActivity
import com.ze20.saifu.ui.input.DataInputActivity

// https://qiita.com/shiita0903/items/6292e462dea749ba03af
// https://qiita.com/Pinehead/items/d83c84e9308969b59654

/**
 * アプリウィジェット機能の実装。
 */
class DataAddWidget : AppWidgetProvider() {

    companion object {
        const val CLICK_ACTION = "CLICK_WIDGET"
    }

    internal fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.saifu_add_widget)

        val intent = Intent(context, DataAddWidget::class.java).apply {
            action = CLICK_ACTION
        }
        val pIntent = PendingIntent.getBroadcast(context, appWidgetId, intent, 0)
        views.setOnClickPendingIntent(R.id.penImage, pIntent)
        // ウィジェットを更新するようにウィジェットマネージャーに指示する
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

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

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent) // ACTION_APPWIDGET_UPDATEなどのIntentが処理される
        if (intent.action.equals(CLICK_ACTION)) {
            startActivity(context, Intent(context, DataInputActivity::class.java), null)
        }
    }
}
