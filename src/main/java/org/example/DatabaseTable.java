package org.example;

public class DatabaseTable<T> implements DatabaseTableInterface<T> {
    final private DatabaseManager dbClient = new DatabaseManager();
    private String id;

<<<<<<< Updated upstream
    protected T createEntity(T entity) {
        this.setId(this.dbClient.getUniqueUUID());
        return entity;
    }

    T getEntityById(String table, String id) {
=======
    void getEntityById(String table, String id) {
>>>>>>> Stashed changes
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
