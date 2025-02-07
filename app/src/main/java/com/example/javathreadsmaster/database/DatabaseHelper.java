package com.example.javathreadsmaster.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "library.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Общие имена колонок
    private static final String KEY_ID = "id";
    // Таблица books - имена колонок

    private static final String KEY_TITLE = "title";
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_YEAR = "year";
    private static final String KEY_ISBN = "isbn";


    private static final String KEY_USERNAME = "username";
    private static final String KEY_BORROWED_BOOKS_COUNT = "borrowed_books_count";


    private static final String KEY_BOOK_ID = "book_id";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_BORROW_DATE = "borrow_date";
    private static final String KEY_RETURN_DATE = "return_date";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE books (" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_TITLE + " TEXT, " +
                KEY_AUTHOR + " TEXT, " +
                KEY_YEAR + " INTEGER," +
                KEY_ISBN +" TEXT)");

        String CREATE_USERS_TABLE = "CREATE TABLE users ("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USERNAME + " TEXT,"
                + KEY_BORROWED_BOOKS_COUNT + " INTEGER" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_BORROWINGS_TABLE = "CREATE TABLE borrowings("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_BOOK_ID + " INTEGER,"
                + KEY_USER_ID + " INTEGER,"
                + KEY_BORROW_DATE + " TEXT,"
                + KEY_RETURN_DATE + " TEXT" + ")";
        db.execSQL(CREATE_BORROWINGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
