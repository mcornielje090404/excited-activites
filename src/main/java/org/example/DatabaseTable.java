package org.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.nio.file.Path;
import java.util.Scanner;

public class DatabaseTable<T> implements DatabaseTableInterface<T> {
    final protected DatabaseManager dbClient = new DatabaseManager();
    private String id;

    T getEntityById(String table, String id) {
        CSVReader csvReader = new CSVReader();
        return this.createObject(csvReader.getEntityDataById(table, id));
    }

    public T createObject(String[] data) {
        return null;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
