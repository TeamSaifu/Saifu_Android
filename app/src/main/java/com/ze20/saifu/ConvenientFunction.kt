package com.ze20.saifu

import android.app.AlertDialog
import android.app.Dialog
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import java.io.ByteArrayOutputStream

internal class ConvenientFunction {

    // 色んな所で使う関数をまとめておきます

    fun editToInt(editText: EditText): Int? {
        // editText を 数値に変換 空文字列ならNullを返す
        return editText.text.toString().toIntOrNull()
    }

    fun getBinaryFromBitmap(bitmap: Bitmap): ByteArray {
        // 画像をByteArray型に変換するやつです
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    fun getClip(applicationContext: Context): String? {
        // クリップボードを返す
        val mManager: ClipboardManager =
            applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        return mManager.primaryClip?.getItemAt(0)?.text.toString()
    }

    fun hideKeyboard(appCompatActivity: AppCompatActivity, view: View?) {
        // キーボードを探してあれば消します
        view.also {
            val manager =
                appCompatActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(view!!.windowToken, 0)
        }
    }

    fun CategoryImage(no: Int): Int? {
        return when (no) {
            0 -> R.drawable.ic_baseline_restaurant_24
            1 -> R.drawable.ic_baseline_sports_esports_24
            2 -> R.drawable.ic_baseline_phonelink_ring_24
            3 -> R.drawable.ic_baseline_train_24
            4 -> R.drawable.ic_baseline_medical
            5 -> R.drawable.ic_baseline_local_airport_24
            6 -> R.drawable.ic_baseline_utility
            7 -> R.drawable.ic_baseline_necessities
            8 -> R.drawable.ic_baseline_house
            9 -> R.drawable.ic_baseline_salary
            10 -> R.drawable.ic_baseline_bonus
            11 -> R.drawable.ic_baseline_extraordinary
            12 -> R.drawable.ic_baseline_more_horiz_24
            else -> null
        }
    }
}

class okCancelDialogFragment : DialogFragment() {

    // OKとキャンセルを表示するダイアログです

    // https://qiita.com/suzukihr/items/8973527ebb8bb35f6bb8

    // https://qiita.com/kumas/items/739f410438d182098b31

    var title = "タイトル"
    var message = "メッセージ"
    var okText = "OK"
    var onOkClickListener: DialogInterface.OnClickListener? = null
    var cancelText = "Cancel"
    var onCancelClickListener: DialogInterface.OnClickListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(getActivity()).run {
            setTitle(title)
            setMessage(message)
            setPositiveButton(okText, onOkClickListener)
            setNegativeButton(cancelText, onCancelClickListener)
            create()
        }
    }

    override fun onPause() {
        super.onPause()

        // onPause でダイアログを閉じる場合
        dismiss()
    }
}
