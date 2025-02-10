package com.example.javathreadsmaster.repositories;

import android.content.Context;
import android.widget.Toast;

import com.example.javathreadsmaster.database.DatabaseHelper;
import com.example.javathreadsmaster.models.Book;
import com.example.javathreadsmaster.tasks.BookTask;
import com.example.javathreadsmaster.utils.CRUDOperation;

import java.lang.ref.WeakReference;

public class BooksRepository implements BookTask.BookTaskCallback {

    private WeakReference<Context> context;
    private DatabaseHelper dbHelper;
    BooksRepCallback callback;
    public BooksRepository(WeakReference<Context> context, BooksRepCallback callback){
        this.context = context;
        dbHelper = new DatabaseHelper(context.get());
        this.callback = callback;
    }

    public void addBook(Book book) {
        new BookTask(dbHelper, CRUDOperation.ADD, this).execute(book);
    }

    public void getAllBooks(){
        new BookTask(dbHelper, CRUDOperation.GET_ALL, this).execute();
    }

    public void updateBook(){

    }

    public void deleteBook()
    {

    }

    @Override
    public void onTaskComplete(CRUDOperation operation, Object result) {
        switch (operation)
        {
            case ADD:
                Toast.makeText(context.get(), "Book added successfully!", Toast.LENGTH_SHORT).show();
                callback.onDataRecieved(CRUDOperation.ADD,null);
                break;
            case GET_ALL:
                callback.onDataRecieved(CRUDOperation.GET_ALL, result);
                break;
        }
    }

    public interface BooksRepCallback{
        void onDataRecieved(CRUDOperation operation, Object result);
    }
}
