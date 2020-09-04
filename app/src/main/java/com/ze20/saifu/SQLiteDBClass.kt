package com.ze20.saifu

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// -----------------------------------------------
//  データベースクラス
// -----------------------------------------------
//
//  読み取り専用で呼び出す場合
//  val database = SQLiteDB.readableDatabase
//  書き込みありで呼び出す場合
//  val database = SQLiteDB.writableDatabase
//
//  SELECTやINSERTやUPDATEとかは
//
//  https://qiita.com/NaoSekig/items/0d95d631378040c1961a
//
//  ここをみてね

class SQLiteDBClass(
    context: Context,
    databaseName: String,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, databaseName, factory, version) {

    // データベース作成

    override fun onCreate(database: SQLiteDatabase?) {
        database?.execSQL("create table if not exists log (inputDate primary key,payDate,name,price,category,splitCount,picture,sign)")
        database?.execSQL("create table if not exists category (id INTEGER primary key AUTOINCREMENT,name,picture)")
        categoryInitialization(database)
        database?.execSQL("create table if not exists notification (id INTEGER primary key AUTOINCREMENT,content,picture)")
        database?.execSQL("create table if not exists budget (id INTEGER primary key AUTOINCREMENT,name,isincome,price)")
        database?.execSQL("create table if not exists shortcut (id INTEGER primary key AUTOINCREMENT,name,price,category)")
        database?.execSQL("create table if not exists wish (id INTEGER primary key AUTOINCREMENT,name,price,url,picture)")
    }

    private fun categoryInitialization(database: SQLiteDatabase?) {
        var i = 0
        database?.execSQL("insert into category VALUES(" + i++ + ",'食費',0)")
        database?.execSQL("insert into category VALUES(" + i++ + ",'娯楽費',1)")
        database?.execSQL("insert into category VALUES(" + i++ + ",'通信費',2)")
        database?.execSQL("insert into category VALUES(" + i++ + ",'交通費',3)")
        database?.execSQL("insert into category VALUES(" + i++ + ",'医療費',4)")
        database?.execSQL("insert into category VALUES(" + i++ + ",'旅行費',5)")
        database?.execSQL("insert into category VALUES(" + i++ + ",'水道光熱費',6)")
        database?.execSQL("insert into category VALUES(" + i++ + ",'日用品費',7)")
        database?.execSQL("insert into category VALUES(" + i++ + ",'住居費',8)")
        database?.execSQL("insert into category VALUES(" + i++ + ",'給料',9)")
        database?.execSQL("insert into category VALUES(" + i++ + ",'ボーナス',10)")
        database?.execSQL("insert into category VALUES(" + i++ + ",'臨時収入',11)")
        database?.execSQL("insert into category VALUES(" + i + ",'その他',12)")
    }

    override fun onUpgrade(database: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // if (oldVersion < newVersion) {
        // }
    }
}
