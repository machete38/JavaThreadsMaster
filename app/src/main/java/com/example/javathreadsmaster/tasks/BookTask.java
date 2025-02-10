package com.example.javathreadsmaster.tasks;

import android.os.AsyncTask;

import com.example.javathreadsmaster.database.DatabaseHelper;
import com.example.javathreadsmaster.models.Book;
import com.example.javathreadsmaster.utils.CRUDOperation;

import java.lang.ref.WeakReference;

public class BookTask extends AsyncTask<Object, Void, Object> {


    private WeakReference<DatabaseHelper> dbHelperRef;
    private CRUDOperation operation;
    private BookTaskCallback callback;

    public BookTask(DatabaseHelper dbHelper, CRUDOperation operation, BookTaskCallback callback) {
        this.dbHelperRef = new WeakReference<>(dbHelper);
        this.operation = operation;
        this.callback = callback;
    }

    @Override
    protected Object doInBackground(Object... params) {
        DatabaseHelper dbHelper = dbHelperRef.get();
        if (dbHelper == null) return null;

        switch (operation) {
            case ADD:
                return dbHelper.addBook((Book) params[0]);
            case GET:
                return dbHelper.getBook((Long) params[0]);
            case GET_ALL:
                return dbHelper.getAllBooks();
            case UPDATE:
                return dbHelper.updateBook((Book) params[0]);
            case DELETE:
                dbHelper.deleteBook((Long) params[0]);
                return null;
            default:
                return null;
        }
    }

    @Override
    protected void onPostExecute(Object result) {
        if (callback != null) {
            callback.onTaskComplete(operation, result);
        }
    }

    public interface BookTaskCallback{
        void onTaskComplete(CRUDOperation operation, Object result);
    }
}
