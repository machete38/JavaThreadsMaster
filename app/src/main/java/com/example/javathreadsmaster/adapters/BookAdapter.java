package com.example.javathreadsmaster.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.javathreadsmaster.R;
import com.example.javathreadsmaster.models.Book;
import com.example.javathreadsmaster.models.Borrowing;
import com.example.javathreadsmaster.repositories.BooksRepository;
import com.example.javathreadsmaster.repositories.BorrowingsRepository;

import java.util.Date;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Book> books;
    private BooksRepository repository;
    private BorrowingsRepository borrowingsRepository;

    public BookAdapter(List<Book> books, BooksRepository repository, BorrowingsRepository borrowingsRepository) {
        this.books = books;
        this.repository = repository;
        this.borrowingsRepository = borrowingsRepository;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_books, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = books.get(position);
        holder.tvTitle.setText(book.getName());
        holder.tvAuthor.setText(book.getAuthor());
        holder.tvYear.setText(String.valueOf(book.getYear()));
        holder.btn.setText(book.isBorrowed() ? "Return" : "Borrow");
        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((book.isBorrowed()))
                {
                    borrowingsRepository.removeBorrowingByBookId(book.getId());
                }
                else
                {

                    Borrowing borrowing = new Borrowing(
                            0,
                            book.getId(),
                            1,
                            System.currentTimeMillis(),
                            System.currentTimeMillis()
                    );

                    borrowingsRepository.addBorrowing(borrowing);

                    book.setBorrowed(true);
                    repository.updateBook(book);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAuthor, tvYear;
        Button btn;

        BookViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvYear = itemView.findViewById(R.id.tvYear);
            btn = itemView.findViewById(R.id.btnBook);
        }
    }
}