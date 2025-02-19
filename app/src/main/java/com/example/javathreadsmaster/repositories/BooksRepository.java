package com.example.javathreadsmaster.repositories;

import android.content.Context;
import android.widget.Toast;

import com.example.javathreadsmaster.database.DatabaseHelper;
import com.example.javathreadsmaster.models.Book;
import com.example.javathreadsmaster.tasks.BookTask;
import com.example.javathreadsmaster.tasks.DatabaseExecutor;
import com.example.javathreadsmaster.utils.CRUDOperation;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class BooksRepository implements BookTask.BookTaskCallback {

    private Context context;
    private DatabaseHelper dbHelper;
    BooksRepCallback callback;

    public BooksRepository(Context context, BooksRepCallback callback) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
        this.callback = callback;
    }

    // Public
    public void addBook(Book book) {
       addBookCompFuture(book);
    }

    public void getAllBooks() {
        getAllBooksCompFuture();
    }

    public void searchBooks(String query) {
       searchBooksCompFuture(query);
    }

    public void getOverdueBooks() {
        getOverdueBooksCompFuture();
    }

    public void updateBook(Book book) {
        updateBookCompFuture(book);
    }

    public void deleteBook() {

    }

    private void updateBookFuture(Book book)
    {
        Future<Integer> future = DatabaseExecutor.submit(() -> dbHelper.updateBook(book));
        try {
            int updated = future.get();
            callback.onDataRecieved(CRUDOperation.UPDATE, updated);
        } catch (ExecutionException | InterruptedException e) {
            Toast.makeText(context, "Unable to update book", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateBookCompFuture(Book book){
        CompletableFuture.supplyAsync(() -> dbHelper.updateBook(book)).thenAccept(
                updated -> callback.onDataRecieved(CRUDOperation.UPDATE, updated)
        ).exceptionally(
                e ->
                {
                    e.printStackTrace();
                    Toast.makeText(context, "Unable to update book", Toast.LENGTH_SHORT).show();
                    return null;
                }
        );
    }
    private void getAllBooksCompFuture()
    {
        CompletableFuture.supplyAsync(() -> dbHelper.getAllBooks()).thenAccept(books -> callback.onDataRecieved(CRUDOperation.GET_ALL,books)).exceptionally(
                e -> {
                    e.printStackTrace();
                    Toast.makeText(context, "Error loading books", Toast.LENGTH_SHORT).show();
                    return null;
                }
        );
    }

    private void getOverdueBooksFuture(){
        Future<List<Book>> future = DatabaseExecutor.submit(() -> dbHelper.getOverdueBooks());
        try {
            List<Book> books = future.get();
            callback.onDataRecieved(CRUDOperation.OVERDUE, books);

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error getting overdue books", Toast.LENGTH_SHORT).show();
        }
    }

    private void getOverdueBooksCompFuture(){
        CompletableFuture.supplyAsync(() -> dbHelper.getOverdueBooks()).thenAccept(
                books -> callback.onDataRecieved(CRUDOperation.OVERDUE, books)
        ).exceptionally(
                e -> {
                    e.printStackTrace();
                    Toast.makeText(context, "Error getting overdue books", Toast.LENGTH_SHORT).show();
                    return null;
                }
        );
    }
    private void searchBooksFuture(String query){
        Future<List<Book>> future = DatabaseExecutor.submit(() -> dbHelper.searchBooks(query));
        try {
            List<Book> books = future.get();
            callback.onDataRecieved(CRUDOperation.GET_ALL, books);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error searching books", Toast.LENGTH_SHORT).show();
        }
    }

    private void searchBooksCompFuture(String query)
    {
        CompletableFuture.supplyAsync(() -> dbHelper.searchBooks(query)).thenAccept(
                books -> callback.onDataRecieved(CRUDOperation.SEARCH, books)
        ).exceptionally(
                e ->
                {
                    e.printStackTrace();
                    Toast.makeText(context, "Error searching books", Toast.LENGTH_SHORT).show();
                    return null;
                }
        );
    }
    private void getAllBooksFuture()
    {
        Future<List<Book>> future = DatabaseExecutor.submit(dbHelper::getAllBooks);
        try {
            List<Book> books = future.get();
            callback.onDataRecieved(CRUDOperation.GET_ALL, books);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error loading books", Toast.LENGTH_SHORT).show();
        }
    }
    private void addBookCompFuture(Book book)
    {
        CompletableFuture.supplyAsync(() -> dbHelper.addBook(book)).thenAccept(id ->
        {
            if (id != -1)
            {
                Toast.makeText(context, "Book added successfully!", Toast.LENGTH_SHORT).show();
                callback.onDataRecieved(CRUDOperation.ADD,null);
            }
            else
            {
                Toast.makeText(context, "Error adding book: long -1", Toast.LENGTH_SHORT).show();
            }
        }).exceptionally(e -> {
            e.printStackTrace();
            Toast.makeText(context, "Error adding book: exception", Toast.LENGTH_SHORT).show();
            return null;
        });
    }
    private void addBookFuture(Book book) {
        Future<Long> future = DatabaseExecutor.submit(() -> dbHelper.addBook(book));
        try {
            long id = future.get();
            if (id != -1)
            {
                Toast.makeText(context, "Book added successfully!", Toast.LENGTH_SHORT).show();
                callback.onDataRecieved(CRUDOperation.ADD,null);
            }
            else
            {
                Toast.makeText(context, "Error adding book: long -1", Toast.LENGTH_SHORT).show();
            }
        }
        catch (ExecutionException | InterruptedException e){
            e.printStackTrace();
            Toast.makeText(context, "Error adding book", Toast.LENGTH_SHORT).show();
        }
    }

    private void addBookAsync(Book book) {
        new BookTask(dbHelper, CRUDOperation.ADD, this).execute(book);
    }



    private void getAllBooksAsync() {
        new BookTask(dbHelper, CRUDOperation.GET_ALL, this).execute();
    }


    @Override
    public void onTaskComplete(CRUDOperation operation, Object result) {
        switch (operation) {
            case ADD:
                Toast.makeText(context, "Book added successfully!", Toast.LENGTH_SHORT).show();
                callback.onDataRecieved(CRUDOperation.ADD, null);
                break;
            case GET_ALL:
                callback.onDataRecieved(CRUDOperation.GET_ALL, result);
                break;
            case OVERDUE:
                callback.onDataRecieved(CRUDOperation.OVERDUE, result);
                break;
            case UPDATE:
                callback.onDataRecieved(CRUDOperation.UPDATE, result);
                break;
        }
    }

    public interface BooksRepCallback {
        void onDataRecieved(CRUDOperation operation, Object result);
    }
}
