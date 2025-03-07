package com.example.javathreadsmaster;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.WorkManager;

import com.example.javathreadsmaster.databinding.ActivityMainBinding;
import com.example.javathreadsmaster.services.BookReturnReminderService;
import com.example.javathreadsmaster.utils.PermissionUtils;
import com.example.javathreadsmaster.utils.WorkManagerUtils;

public class MainActivity extends AppCompatActivity {
    private static final int NOTIFICATION_PERMISSION_CODE = 123;

    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        startBookReturnReminderService();
        setButtonClickListeners();

    }

    private void startBookReturnReminderService() {
        if (PermissionUtils.INSTANCE.checkNotificationPermission(this)) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                Intent serviceIntent = new Intent(this, BookReturnReminderService.class);
                startService(serviceIntent);
            }
            else
            {
                WorkManagerUtils.scheduleBooksReturnReminder(this, 24);
            }

        }
    }



    private void setButtonClickListeners() {
        binding.btnManageBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, BooksManagementActivity.class);
                startActivity(i);
            }
        });

        binding.btnManageBorrowings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        binding.btnManageUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}