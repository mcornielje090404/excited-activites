package org.example;

public class ActivityService extends DatabaseTable<ActivityService> {
    private Activity activity;
    private String title;
    private int price;

    public ActivityService(Activity activity, String id) {
        this.getEntityById("ActivityService", id);
        this.activity = activity;
    }

    public ActivityService(String id) {
        this.getEntityById("ActivityService", id);
    }

    public ActivityService createObject(String[] csvData) {
        this.setId(csvData[0]);
        this.title = csvData[2];
        this.price = Integer.parseInt(csvData[3]);

        if (this.activity == null) {
            this.activity = new Activity(csvData[1]);
        }

        return this;
    }

    public String getTitle() {
        return this.title;
    }

    public int getPrice() {
        return this.price;
    }
}
