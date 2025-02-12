package com.example.javathreadsmaster;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.javathreadsmaster.adapters.BookAdapter;
import com.example.javathreadsmaster.databinding.ActivityBooksManagementBinding;
import com.example.javathreadsmaster.models.Book;
import com.example.javathreadsmaster.repositories.BooksRepository;
import com.example.javathreadsmaster.repositories.BorrowingsRepository;
import com.example.javathreadsmaster.tasks.DatabaseExecutor;
import com.example.javathreadsmaster.utils.CRUDOperation;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class BooksManagementActivity extends AppCompatActivity implements BooksRepository.BooksRepCallback, BorrowingsRepository.BorrowRepCallback {

    ActivityBooksManagementBinding binding;
    BooksRepository repository;
    private BookAdapter adapter;

    List<Book> bookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBooksManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new BooksRepository(getApplicationContext(), this);
        bookList = new ArrayList<>();

        setRV();
        setButtons();


    }

    private void setRV() {
        adapter = new BookAdapter(bookList, repository, new BorrowingsRepository(getApplicationContext(), this));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        binding.recyclerView.setAdapter(adapter);
        loadBooks();
    }

    private void setButtons() {
        binding.btnAdd.setOnClickListener(view -> showAddBookDialog());

        binding.btnUpdate.setOnClickListener(view -> loadBooks());

        binding.btnSearch.setOnClickListener(v -> {
            String text = binding.etSearch.getText().toString();
            if (!text.isBlank()) {
                repository.searchBooks(text);
            }
        });
    }

    private void showAddBookDialog() {
        View view = View.inflate(this, R.layout.item_add_book, null);
        new AlertDialog.Builder(this)
                .setView(view).setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = ((EditText) view.findViewById(R.id.etTitle)).getText().toString();
                        int year = Integer.valueOf(((EditText) view.findViewById(R.id.etYear)).getText().toString());
                        String author = ((EditText) view.findViewById(R.id.etAuthor)).getText().toString();
                        Book book = new Book(0, name, year, author, "isbn", false);
                        repository.addBook(book);
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
    }

    @Override
    public void onDataRecieved(CRUDOperation operation, Object result) {
        switch (operation) {
            case ADD:
            case UPDATE:
                loadBooks();
                break;
            case GET_ALL:
                updateBooksList((List<Book>) result);
                break;


        }
    }

    private void updateBooksList(List<Book> values) {
        bookList.clear();
        bookList.addAll(values);
        adapter.notifyDataSetChanged();
    }

    private void loadBooks() {
        repository.getAllBooks();
    }


    @Override
    public void onBorrowingsRecieved(CRUDOperation operation, Object value) {
        switch (operation)
        {
            case ADD:
                Toast.makeText(this, "New borrowing made successfuly!", Toast.LENGTH_SHORT).show();
                break;
            case REMOVE_BY_ID:
                Toast.makeText(this, "Borrowing closed successfuly!", Toast.LENGTH_SHORT).show();
                break;

        }
    }
}