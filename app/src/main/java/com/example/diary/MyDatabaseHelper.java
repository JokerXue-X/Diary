package com.example.diary;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    //编写建表语句，并把它变成一个字符串常量
    public static final String CREATE_DIARY = "create table Diary("
            + "id integer primary key autoincrement,"//自动将id项加一，并且不用输入
            + "title text,"
            + "content text,"
            + "createTime integer,"
            + "writer text,"
            + "photo_path text)";

    private Context mContext;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        //重写SQLiteOpenHelper的构造方法，四个参数
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {//重写SQLiteOpenHelper两个抽象方法onCreat()和onUpgrade()
        db.execSQL(CREATE_DIARY);
        //当通过创建MyDatabaseHelper对象，并调用getWritableDatabase()方法时，
        // 首先检测到数据库里面有没有Diary.db数据库，如果没有就会调用onCreate()方法创建一个Diary.db数据库和Diary表，若有则不执行onCreate()方法
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
