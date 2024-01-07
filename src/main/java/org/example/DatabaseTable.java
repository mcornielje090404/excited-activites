package org.example;

public class DatabaseTable<T> {
    final private DatabaseManager dbClient = new DatabaseManager();
    private String id;

    protected T createEntity(T entity) {
        this.setId(this.dbClient.getUniqueUUID());
        return entity;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
