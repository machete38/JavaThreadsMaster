package com.example.javathreadsmaster.models;

import android.util.Log;

import java.util.Date;

public class Borrowing {
    long id;
    long bookId;
    long userId;
    long borrowingStart;
    long borrowingEnd;

    public Borrowing(long id, long bookId, long userId, long borrowingStart, long borrowingEnd) {
        Log.d("BORROWING: NEW", "book id:"+ bookId);
        this.id = id;
        this.bookId = bookId;
        this.userId = userId;
        this.borrowingStart = borrowingStart;
        this.borrowingEnd = borrowingEnd;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getBorrowingStart() {
        return borrowingStart;
    }

    public void setBorrowingStart(long borrowingStart) {
        this.borrowingStart = borrowingStart;
    }

    public long getBorrowingEnd() {
        return borrowingEnd;
    }

    public void setBorrowingEnd(long borrowingEnd) {
        this.borrowingEnd = borrowingEnd;
    }
}

