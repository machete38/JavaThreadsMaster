package com.example.javathreadsmaster.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.javathreadsmaster.models.Book;
import com.example.javathreadsmaster.models.Borrowing;
import com.example.javathreadsmaster.models.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "library.db";
    private static final int DATABASE_VERSION = 2;

    private volatile ConcurrentHashMap<Long, Book> bookCache;
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        bookCache = new ConcurrentHashMap<>();
    }

    // Общие имена колонок
    private static final String KEY_ID = "id";
    // Таблица books - имена колонок

    private static final String KEY_TITLE = "title";
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_YEAR = "year";
    private static final String KEY_ISBN = "isbn";
    private static final String KEY_BORROWED = "borrowed";


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
                KEY_ISBN +" TEXT," +
                KEY_BORROWED +" INTEGER)");

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
        if (id != -1) {
            book.setId(id);
            bookCache.put(id, book);
        }
        return id;
    }

    public synchronized Book getBook(long id){
        Book cachedBook = bookCache.get(id);
        if (cachedBook != null) {
            return cachedBook;
        }
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
            bookCache.put(id, book);
        }
        db.close();
        return book;
    }

    public synchronized List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM books", null);

        int idIndex = cursor.getColumnIndex(KEY_ID);
        int titleIndex = cursor.getColumnIndex(KEY_TITLE);
        int authorIdIndex = cursor.getColumnIndex(KEY_AUTHOR);
        int yearIndex = cursor.getColumnIndex(KEY_YEAR);
        int isbnIndex = cursor.getColumnIndex(KEY_ISBN);
        int borrowedIndex = cursor.getColumnIndex(KEY_BORROWED);

        if (idIndex < 0 || titleIndex < 0 || authorIdIndex < 0 || yearIndex < 0 || isbnIndex < 0 || borrowedIndex < 0) {
            // Обработка ошибки: один или несколько ожидаемых столбцов отсутствуют
            cursor.close();
            db.close();
            throw new IllegalStateException("Database schema doesn't match expected Books table structure");
        }

        if (cursor.moveToFirst()) {
            do {
                Book book = new Book(
                        cursor.getLong(idIndex),
                        cursor.getString(titleIndex),
                        cursor.getInt(yearIndex),
                        cursor.getString(authorIdIndex),
                        cursor.getString(isbnIndex),
                        cursor.getInt(borrowedIndex) == 1
                );
                books.add(book);
                bookCache.put(book.getId(), book);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return books;
    }

    public synchronized int updateBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, book.getName());
        values.put(KEY_AUTHOR, book.getAuthor());
        values.put(KEY_YEAR, book.getYear());
        values.put(KEY_ISBN, book.getIsbn());
        values.put(KEY_BORROWED, book.isBorrowed() ? 1 : 0);
        int rowsAffected = db.update("books", values, "id = ?", new String[]{String.valueOf(book.getId())});
        db.close();
        return rowsAffected;
    }

    public synchronized void deleteBook(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("books", "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<Book> searchBooks(String query)
    {
       List<Book> results = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {KEY_ID, KEY_TITLE, KEY_AUTHOR, KEY_YEAR, KEY_ISBN, KEY_BORROWED};
        String selection = KEY_TITLE+" like ? OR "+KEY_AUTHOR +" like ?";
        String[] selectionArgs = {"%" + query + "%", "%"+ query + "%"};
        Cursor cursor = db.query("books",columns, selection, selectionArgs, null, null, null);

        int idIndex = cursor.getColumnIndex(KEY_ID);
        int titleIndex = cursor.getColumnIndex(KEY_TITLE);
        int authorIdIndex = cursor.getColumnIndex(KEY_AUTHOR);
        int yearIndex = cursor.getColumnIndex(KEY_YEAR);
        int isbnIndex = cursor.getColumnIndex(KEY_ISBN);
        int borrowedIndex = cursor.getColumnIndex(KEY_BORROWED);

        if (idIndex < 0 || titleIndex < 0 || authorIdIndex < 0 || yearIndex < 0 || isbnIndex < 0 || borrowedIndex < 0) {
            cursor.close();
            db.close();
            throw new IllegalStateException("Database schema doesn't match expected Books table structure");
        }

        if (cursor.moveToFirst()) {
            do {
                Book book = new Book(
                        cursor.getLong(idIndex),
                        cursor.getString(titleIndex),
                        cursor.getInt(yearIndex),
                        cursor.getString(authorIdIndex),
                        cursor.getString(isbnIndex),
                        cursor.getInt(borrowedIndex) == 1
                );
                results.add(book);
                bookCache.put(book.getId(), book);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return results;

    }
    // CRUD операции с User
    public synchronized long addUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", user.getUsername());
        values.put("borrowed_books_count", user.getBorrowedBooksCount());
        long id = db.insert("users", null, values);
        db.close();
        return id;
    }

    public synchronized User getUser(long id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("users", null, "id = ?", new String[]{String.valueOf(id)},null,null,null);
        User user = null;
        if (cursor.moveToFirst()){
            user = new User(
                    cursor.getLong(0),
                    cursor.getString(1)
            );
            cursor.close();
        }
        db.close();
        return user;
    }

    public synchronized List<User> getAllUsers(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users", null);
        List<User> users = new ArrayList<>();
        if (cursor.moveToFirst()){
            do {
                User user = new User(
                        cursor.getLong(0),
                        cursor.getString(1)
                );
                users.add(user);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return users;
    }

    public synchronized int updateUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", user.getUsername());
        values.put("borrowed_books_count", user.getBorrowedBooksCount());
        int id = db.update("users", values, "id =? ", new String[]{String.valueOf(user.getId())});
        db.close();
        return id;
    }

    public synchronized void deleteUser(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("users", "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }


    // CRUD операции с Borrowing
    public synchronized long addBorrowing(Borrowing borrowing){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("book_id", borrowing.getBookId());
        values.put("user_id", borrowing.getUserId());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        if (borrowing.getBorrowingStart() != null) {
            values.put("borrow_date", dateFormat.format(borrowing.getBorrowingStart()));
        }

        if (borrowing.getBorrowingEnd() != null) {
            values.put("return_date", dateFormat.format(borrowing.getBorrowingEnd()));
        }

        long id = db.insert("borrowings", null, values);
        db.close();
        return id;
    }

    public synchronized Borrowing getBorrowing(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Borrowing borrowing = null;

        Cursor cursor = db.query("borrowings", null, "id = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cursor.moveToFirst()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            long bookId = cursor.getLong(0);
            long userId = cursor.getLong(1);

            Date borrowDate = null;
            Date returnDate = null;

            try {
                String borrowDateStr = cursor.getString(2);
                if (borrowDateStr != null) {
                    borrowDate = dateFormat.parse(borrowDateStr);
                }

                String returnDateStr = cursor.getString(3);
                if (returnDateStr != null) {
                    returnDate = dateFormat.parse(returnDateStr);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            borrowing = new Borrowing(id, bookId, userId, borrowDate, returnDate);
            cursor.close();
        }

        db.close();
        return borrowing;
    }

    public synchronized List<Borrowing> getAllBorrowings() {
        List<Borrowing> borrowings = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM borrowings", null);

        int idIndex = cursor.getColumnIndex("id");
        int bookIdIndex = cursor.getColumnIndex("book_id");
        int userIdIndex = cursor.getColumnIndex("user_id");
        int borrowDateIndex = cursor.getColumnIndex("borrow_date");
        int returnDateIndex = cursor.getColumnIndex("return_date");

        if (idIndex < 0 || bookIdIndex < 0 || userIdIndex < 0 || borrowDateIndex < 0 || returnDateIndex < 0) {
            // Обработка ошибки: один или несколько ожидаемых столбцов отсутствуют
            cursor.close();
            db.close();
            throw new IllegalStateException("Database schema doesn't match expected Borrowing table structure");
        }

        if (cursor.moveToFirst()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            do {
                long id = cursor.getLong(idIndex);
                long bookId = cursor.getLong(bookIdIndex);
                long userId = cursor.getLong(userIdIndex);

                Date borrowDate = null;
                Date returnDate = null;

                try {
                    String borrowDateStr = cursor.getString(borrowDateIndex);
                    if (borrowDateStr != null && !borrowDateStr.isEmpty()) {
                        borrowDate = dateFormat.parse(borrowDateStr);
                    }

                    String returnDateStr = cursor.getString(returnDateIndex);
                    if (returnDateStr != null && !returnDateStr.isEmpty()) {
                        returnDate = dateFormat.parse(returnDateStr);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    // Можно добавить логирование или обработку ошибки парсинга даты
                }

                Borrowing borrowing = new Borrowing(id, bookId, userId, borrowDate, returnDate);
                borrowings.add(borrowing);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return borrowings;
    }

    public synchronized int updateBorrowing(Borrowing borrowing) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("book_id", borrowing.getBookId());
        values.put("user_id", borrowing.getUserId());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        if (borrowing.getBorrowingStart() != null) {
            values.put("borrow_date", dateFormat.format(borrowing.getBorrowingStart()));
        }

        if (borrowing.getBorrowingEnd() != null) {
            values.put("return_date", dateFormat.format(borrowing.getBorrowingEnd()));
        }
        int rowsAffected = db.update("borrowings", values, "id = ?", new String[]{String.valueOf(borrowing.getId())});
        db.close();
        return rowsAffected;
    }

    public synchronized void deleteBorrowing(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("borrowings", "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

}
