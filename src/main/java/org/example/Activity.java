package org.example;

import java.util.ArrayList;

public class Activity extends DatabaseTable<Activity> {
    private int baseCost;
    private String title;
    private String description;
    private String location;
    private String activityDate;
    private float duration;
    private int insuranceCost;

    public Activity(String id) {
        this.getEntityById("Activity", id);
    }

    public Activity(String[] csvData) {
        this.createObject(csvData);
    }

    @Override
    public Activity createObject(String[] csvData) {
        this.setId(csvData[0]);
        this.baseCost = Integer.parseInt(csvData[1]);
        this.title = csvData[2];
        this.description = csvData[3];
        this.location = csvData[4];
        this.activityDate = csvData[5];
        this.duration = Float.parseFloat(csvData[6]);
        this.insuranceCost = Integer.parseInt(csvData[8]);

        return this;
    }

    public ArrayList<ActivityService> getServices() {
        CSVReader csvReader = new CSVReader();
        ArrayList<ActivityService> services = new ArrayList<>();
        ArrayList<String[]> rawServices = csvReader.getManyNestedEntitiesById("ActivityService", this.getId(), "activityId");

        for (String[] serviceData : rawServices) {
            services.add(new ActivityService(this, serviceData[0]));
        }

        return services;
    }

    public int getBaseCost() {
        return this.baseCost;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public String getLocation() {
        return this.location;
    }

    public String getActivityDate() {
        return this.activityDate;
    }

    public float getDuration() {
        return this.duration;
    }

    public int getInsuranceCost() {
        return this.insuranceCost;
    }
}