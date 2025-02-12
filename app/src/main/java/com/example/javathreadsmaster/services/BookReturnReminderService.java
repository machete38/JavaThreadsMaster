package com.example.javathreadsmaster.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.javathreadsmaster.R;
import com.example.javathreadsmaster.database.DatabaseHelper;
import com.example.javathreadsmaster.models.Book;
import com.example.javathreadsmaster.repositories.BooksRepository;
import com.example.javathreadsmaster.utils.CRUDOperation;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class BookReturnReminderService extends Service implements BooksRepository.BooksRepCallback {

    private static final String CHANNEL_ID = "BookReturnReminder";
    private static final int NOTIFICATION_ID = 1;

    private volatile boolean isChecking = false;
    private Handler handler;
    private BooksRepository repository;
    private ReentrantLock lock = new ReentrantLock();
    private long checkInterval = 1000 * 60 * 60 * 24;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper());
        repository = new BooksRepository(getApplicationContext(), this);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Book Return Reminders";
            String description = "Channel for book return reminders";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("checkInterval"))
        {
            checkInterval = intent.getLongExtra("checkInterval", checkInterval);
        }
        startBookReturnCheck();
        return START_STICKY;
    }

    private void startBookReturnCheck() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkBookReturns();
                handler.postDelayed(this,checkInterval);
            }
        }, checkInterval);
    }

    private void checkBookReturns() {
        lock.lock();
        try{
            if (!isChecking){
                isChecking = true;
                repository.getOverdueBooks();
            }
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDataRecieved(CRUDOperation operation, Object result) {
        switch (operation){
            case OVERDUE:
                isChecking = false;
                performNotificationSending((List<Book>) result);
                break;

        }    }

    private void performNotificationSending(List<Book> books) {
        for (Book book : books)
        {
            sendNotification(book);
        }

    }
    private void sendNotification(Book book) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Book Return Reminder")
                .setContentText("Please return the book: " + book.getName())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

}