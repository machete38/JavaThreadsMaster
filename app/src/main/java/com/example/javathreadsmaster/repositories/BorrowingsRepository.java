package com.example.javathreadsmaster.repositories;

import android.content.Context;
import android.widget.Toast;

import com.example.javathreadsmaster.database.DatabaseHelper;
import com.example.javathreadsmaster.models.Borrowing;
import com.example.javathreadsmaster.tasks.DatabaseExecutor;
import com.example.javathreadsmaster.utils.CRUDOperation;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class BorrowingsRepository {

    DatabaseHelper dbHelper;
    Context context;

    BorrowRepCallback callback;

    public BorrowingsRepository(Context context, BorrowRepCallback callback) {
        dbHelper = new DatabaseHelper(context);
        this.context = context;
        this.callback = callback;
    }

    public interface BorrowRepCallback{
        public void onBorrowingsRecieved(CRUDOperation operation, Object value);
    }

    public void addBorrowing(Borrowing borrowing){
        Future<Long> future = DatabaseExecutor.submit(() -> dbHelper.addBorrowing(borrowing));
        try{
            long id = future.get();
            callback.onBorrowingsRecieved(CRUDOperation.ADD, id);
        } catch (ExecutionException | InterruptedException e) {
            Toast.makeText(context, "Error adding borrowing", Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }
    }

    public void removeBorrowingByBookId(long id)
    {
        Future<Boolean> future = DatabaseExecutor.submit(() -> dbHelper.removeBorrowingByBookId(id));
        try{
            Boolean result = future.get();
            callback.onBorrowingsRecieved(CRUDOperation.REMOVE_BY_ID, result);
        } catch (ExecutionException | InterruptedException e) {
            Toast.makeText(context.getApplicationContext(), "Error with borrowing closure", Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }
    }
}
