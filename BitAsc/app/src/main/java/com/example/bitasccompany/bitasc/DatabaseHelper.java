package com.example.bitasccompany.bitasc;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;


/**
 * Created by IIObmanov on 15.09.2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper implements BaseColumns {

    // имя базы данных
    private static final String DATABASE_NAME = "mydatabase.db";
    // версия базы данных
    private static final String DATABASE_TABLE = "cats";
    private static final int DATABASE_VERSION = 1;
    public static final String CAT_NAME_COLUMN = "cat_name";
    public static final String PHONE_COLUMN = "phone";
    public static final String AGE_COLUMN = "age";
    private static final String DATABASE_CREATE_SCRIPT = "create table "
            + DATABASE_TABLE + " (" + BaseColumns._ID
            + " integer primary key autoincrement, " + CAT_NAME_COLUMN
            + " text not null, " + PHONE_COLUMN + " integer, " + AGE_COLUMN
            + " integer);";

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
