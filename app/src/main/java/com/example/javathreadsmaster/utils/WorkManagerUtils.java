package com.example.javathreadsmaster.utils;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.javathreadsmaster.workers.BookReturnReminderWorker;

import java.util.concurrent.TimeUnit;

public class WorkManagerUtils {

    private static final String BOOK_REMINDER_WORK = "book_return_reminder_work";

    public static void scheduleBooksReturnReminder(Context context, long intervalHours){

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        PeriodicWorkRequest bookReminderWork =
                new PeriodicWorkRequest.Builder(BookReturnReminderWorker.class,
                        intervalHours, TimeUnit.HOURS)
                        .setConstraints(constraints)
                        .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                BOOK_REMINDER_WORK,
                ExistingPeriodicWorkPolicy.KEEP,
                bookReminderWork);


    }

    public static void cancelBooksReturnReminder(Context context){
        WorkManager.getInstance(context).cancelUniqueWork(BOOK_REMINDER_WORK);
    }
}
