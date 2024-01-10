package org.example;

public class DatabaseTable<T> implements DatabaseTableInterface<T> {
    final protected DatabaseManager dbClient = new DatabaseManager();
    private String id;

    protected T createEntity(T entity) {
        this.setId(this.dbClient.getUniqueUUID());
        return entity;
    }
    void getEntityById(String table, String id) {
        CSVReader csvReader = new CSVReader();
        this.createObject(csvReader.getEntityDataById(table, id));
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
