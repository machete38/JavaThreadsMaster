package com.example.javathreadsmaster.dataManagers;

import com.example.javathreadsmaster.models.Book;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class LibraryDataManager {
    private final Gson gson = new Gson();

    public void exportToJson(List<Book> books, String filepath){
        String json = gson.toJson(books);
        try(FileWriter writer = new FileWriter(filepath)){
            writer.write(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Book> importFromJson(String filepath) {
        try(FileReader reader = new FileReader(filepath)){
            Type bookListType = new TypeToken<List<Book>>(){}.getType();
            return gson.fromJson(reader, bookListType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
