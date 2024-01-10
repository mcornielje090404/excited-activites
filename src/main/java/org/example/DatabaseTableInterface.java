package org.example;

public interface DatabaseTableInterface<T> {
    T createObject(String[] csvData);
}
