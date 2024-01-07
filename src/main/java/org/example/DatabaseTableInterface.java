package org.example;

public interface DatabaseTableInterface<T> {
    public T createObject(String[] csvData);
}
