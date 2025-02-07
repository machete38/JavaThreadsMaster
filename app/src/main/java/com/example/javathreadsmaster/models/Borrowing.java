package com.example.javathreadsmaster.models;

import java.util.Date;

public class Borrowing {
    long id;
    long bookId;
    long userId;
    Date borrowingStart;
    Date borrowingEnd;

    public Borrowing(long id, long bookId, long userId, Date borrowingStart, Date borrowingEnd) {
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

    public Date getBorrowingStart() {
        return borrowingStart;
    }

    public void setBorrowingStart(Date borrowingStart) {
        this.borrowingStart = borrowingStart;
    }

    public Date getBorrowingEnd() {
        return borrowingEnd;
    }

    public void setBorrowingEnd(Date borrowingEnd) {
        this.borrowingEnd = borrowingEnd;
    }
}

