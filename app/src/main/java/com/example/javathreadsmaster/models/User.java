package com.example.javathreadsmaster.models;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class User {
    private final long id;
    private String username;
    private final AtomicInteger borrowedBooksCount;
    private final List<Borrowing> borrowingHistory;

    public User(long id, String username) {
        this.id = id;
        setUsername(username);
        this.borrowedBooksCount = new AtomicInteger(0);
        this.borrowingHistory = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        this.username = username;
    }

    public int getBorrowedBooksCount() {
        return borrowedBooksCount.get();
    }

    public void incrementBorrowedBooksCount() {
        borrowedBooksCount.incrementAndGet();
    }

    public void decrementBorrowedBooksCount() {
        borrowedBooksCount.updateAndGet(count -> Math.max(0, count - 1));
    }

    public List<Borrowing> getBorrowingHistory() {
        return new ArrayList<>(borrowingHistory);
    }

    public void addBorrowing(Borrowing borrowing) {
        if (borrowing == null) {
            throw new IllegalArgumentException("Borrowing cannot be null");
        }
        borrowingHistory.add(borrowing);
        incrementBorrowedBooksCount();
    }

    public void removeBorrowing(Borrowing borrowing) {
        if (borrowing == null) {
            throw new IllegalArgumentException("Borrowing cannot be null");
        }
        if (borrowingHistory.remove(borrowing)) {
            decrementBorrowedBooksCount();
        }
    }

    public Borrowing getCurrentBorrowing() {
        return borrowingHistory.isEmpty() ? null : borrowingHistory.get(borrowingHistory.size() - 1);
    }

    public boolean canBorrowBook() {
        return getBorrowedBooksCount() < 5;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", borrowedBooksCount=" + borrowedBooksCount +
                '}';
    }
}