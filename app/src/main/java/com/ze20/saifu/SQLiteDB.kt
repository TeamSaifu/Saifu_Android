package com.ze20.saifu

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

//-----------------------------------------------
//  データベースクラス
//-----------------------------------------------
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

class SQLiteDB(
    context: Context,
    databaseName: String,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, databaseName, factory, version) {

    //データベース作成

    override fun onCreate(database: SQLiteDatabase?) {
        database?.execSQL("create table if not exists log (inputDate primary key,payDate,name,price,category,splitCount,picture,sign)")
        database?.execSQL("create table if not exists category (id INTEGER primary key AUTOINCREMENT,name,picture)")
        database?.execSQL("create table if not exists notification (id INTEGER primary key AUTOINCREMENT,content,picture)")
        database?.execSQL("create table if not exists budget (id INTEGER primary key AUTOINCREMENT,name,type,price)")
        database?.execSQL("create table if not exists shortcut (id INTEGER primary key AUTOINCREMENT,name,price,category)")
        database?.execSQL("create table if not exists wish (id INTEGER primary key AUTOINCREMENT,name,price,url,picture)")
    }

    override fun onUpgrade(database: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // if (oldVersion < newVersion) {
        // }
    }
}
