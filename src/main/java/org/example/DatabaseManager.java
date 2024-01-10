package org.example;

import java.util.UUID;

public class DatabaseManager {

    public String getUniqueUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
