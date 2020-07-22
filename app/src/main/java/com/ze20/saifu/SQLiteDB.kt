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

private class SQLiteDB(context: Context, databaseName:String, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, databaseName, factory, version) {

    //データベース作成

    override fun onCreate(database: SQLiteDatabase?) {
        database?.execSQL("create table if not exists log (inputdate primary key,paydate,name,price,category,splitcount,picture)");
        database?.execSQL("create table if not exists category (id primary key,name,picture)");
        database?.execSQL("create table if not exists tuchi (id primary key,content,picture)");
        database?.execSQL("create table if not exists kotei (id primary key,name,type,price)");
        database?.execSQL("create table if not exists shortcut (id primary key,name,price)");
        database?.execSQL("create table if not exists hosii (id primary key,name,price,url,picture)");
    }

    override fun onUpgrade(database: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < newVersion) {
        }
    }
}
