package com.example.javathreadsmaster.models;

import java.util.Calendar;

public class Book {
    private long id;
    private String name;
    private int year;
    private String author;
    private String isbn;
    private boolean borrowed;

    public Book(long id, String name, int year, String author, String isbn, boolean borrowed) {
        this.id = id;
        this.name = name;
        setYear(year);
        this.author = author;
        this.isbn = isbn;
        this.borrowed = borrowed;
    }

    public void setId(long id)
    {
        this.id = id;
    }
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        if (year <= 0 || year > currentYear) {
            throw new IllegalArgumentException("Invalid year");
        }
        this.year = year;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author cannot be null or empty");
        }
        this.author = author;
    }

    public boolean isBorrowed() {
        return borrowed;
    }

    public void setBorrowed(boolean borrowed) {
        this.borrowed = borrowed;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
}