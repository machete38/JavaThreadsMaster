package com.example.javathreadsmaster.repositories;

import android.content.Context;
import android.widget.Toast;

import com.example.javathreadsmaster.database.DatabaseHelper;
import com.example.javathreadsmaster.models.Book;
import com.example.javathreadsmaster.tasks.BookTask;
import com.example.javathreadsmaster.tasks.DatabaseExecutor;
import com.example.javathreadsmaster.utils.CRUDOperation;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
        Future<Long> future = DatabaseExecutor.submit(() -> dbHelper.addBook(book));
        try {
            long id = future.get();
            if (id != -1)
            {
                Toast.makeText(context.get(), "Book added successfully!", Toast.LENGTH_SHORT).show();
                callback.onDataRecieved(CRUDOperation.ADD,null);
            }
            else
            {
                Toast.makeText(context.get(), "Error adding book: long -1", Toast.LENGTH_SHORT).show();
            }
        }
        catch (ExecutionException | InterruptedException e){
            e.printStackTrace();
            Toast.makeText(context.get(), "Error adding book", Toast.LENGTH_SHORT).show();
        }
    }

    public void getAllBooks(){
        Future<List<Book>> future = DatabaseExecutor.submit(dbHelper::getAllBooks);
        try {
            List<Book> books = future.get();
            callback.onDataRecieved(CRUDOperation.GET_ALL, books);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(context.get(), "Error loading books", Toast.LENGTH_SHORT).show();
        }
    }

    public void searchBooks(String query){
        Future<List<Book>> future = DatabaseExecutor.submit(() -> dbHelper.searchBooks(query));
        try {
            List<Book> books = future.get();
            callback.onDataRecieved(CRUDOperation.GET_ALL, books);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(context.get(), "Error searching books", Toast.LENGTH_SHORT).show();
        }
    }
    public void addBookAsync(Book book) {
        new BookTask(dbHelper, CRUDOperation.ADD, this).execute(book);
    }

    public void getAllBooksAsync(){
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
