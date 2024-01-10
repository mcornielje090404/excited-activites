package org.example;

public class BookingActivityService extends DatabaseTable<BookingActivityService> {
    private Booking booking;
    private ActivityService activityService;

    public BookingActivityService(ActivityService activityService, Booking booking) {
        this.setId(this.dbClient.getUniqueUUID());
        this.activityService = activityService;
        this.booking = booking;
    }

    public BookingActivityService(Booking booking, String[] csvData) {
        this.booking = booking;
        this.createObject(csvData);
    }

    @Override
    public BookingActivityService createObject(String[] csvData) {
        this.setId(csvData[0]);

        if (this.booking == null) {
            this.booking = new Booking(csvData[1]);
        }

        if (this.activityService == null) {
            this.activityService = new ActivityService(csvData[2]);
        }

        return this;
    }

    public ActivityService getActivityService() {
        return this.activityService;
    }
}
