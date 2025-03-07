package com.example.javathreadsmaster.workers;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.javathreadsmaster.R;
import com.example.javathreadsmaster.models.Book;
import com.example.javathreadsmaster.repositories.BooksRepository;
import com.example.javathreadsmaster.utils.CRUDOperation;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

public class BookReturnReminderWorker extends Worker implements BooksRepository.BooksRepCallback {

    private static final String CHANNEL_ID = "BookReturnReminder";
    private static final int NOTIFICATION_ID = 1;
    private static String TAG = "BookReturnReminderWorker";
    private ReentrantLock lock;
    private CountDownLatch latch;
    private List<Book> overdueBooks;
    public BookReturnReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        checkBookReturns();
        return Result.success();
    }

    private void checkBookReturns() {
        lock.lock();
        try
        {
            latch = new CountDownLatch(1);

            BooksRepository repository = new BooksRepository(getApplicationContext(), this);

            repository.getOverdueBooks();

            try
            {
                latch.await();
            }
            catch (InterruptedException e)
            {
                Log.e(TAG, "Interrupted while waiting for overdue books", e);
            }
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public void onDataRecieved(CRUDOperation operation, Object result) {
        switch (operation){
            case OVERDUE:
                overdueBooks = (List<Book>) result;
                performNotificationSending(overdueBooks);
                latch.countDown();
                break;

        }
    }

    private void performNotificationSending(List<Book> overdueBooks) {

        if (overdueBooks == null || overdueBooks.isEmpty()){
            Log.d(TAG, "No overdue books found");
            return;
        }

        for (Book book : overdueBooks)
        {
            sendNotification(book);
        }

    }

    private void sendNotification(Book book) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Book Return Reminder")
                .setContentText("Please return the book: " + book.getName())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
