package com.example.javathreadsmaster.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.javathreadsmaster.models.Book;

import java.util.ArrayList;
import java.util.List;

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

    //CRUD операции с Book
    public synchronized long addBook(Book book){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", book.getName());
        values.put("author", book.getAuthor());
        values.put("year", book.getYear());
        values.put("isbn", book.getIsbn());
        values.put("borrowed", book.isBorrowed() ? 1 : 0);
        long id = db.insert("books", null, values);
        db.close();
        return id;
    }

    public synchronized Book getBook(long id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("books", null, "id = ?", new String[]{String.valueOf(id)}, null, null, null);
        Book book = null;
        if (cursor.moveToFirst()){
            book = new Book(
                    cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getInt(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getInt(4) == 1
            );
            cursor.close();
        }
        db.close();
        return book;
    }

    public synchronized List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM books", null);
        if (cursor.moveToFirst()) {
            do {
                Book book = new Book(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getInt(4) == 1
                );
                books.add(book);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return books;
    }

    public synchronized int updateBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", book.getName());
        values.put("author", book.getAuthor());
        values.put("year", book.getYear());
        values.put("isbn", book.getIsbn());
        values.put("borrowed", book.isBorrowed() ? 1 : 0);
        int rowsAffected = db.update("books", values, "id = ?", new String[]{String.valueOf(book.getId())});
        db.close();
        return rowsAffected;
    }

    public synchronized void deleteBook(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("books", "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}
