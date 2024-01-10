package org.example;

import java.util.ArrayList;

public class Itinerary extends DatabaseTable<Itinerary> {
    private ArrayList<Booking> bookings;
    private int numOfAttendees;
    private String itineraryReference;
    private LeadAttendee leadAttendee;

    public Itinerary(String id) {
        this.getEntityById("Itinerary", id);
        this.fetchBookings(id);
    }

    public Itinerary(LeadAttendee leadAttendee, String id) {
        this.leadAttendee = leadAttendee;
        this.getEntityById("Itinerary", id);
        this.fetchBookings(id);
    }

<<<<<<< Updated upstream
=======
    public Itinerary() {
        this.setId(this.dbClient.getUniqueUUID());
        this.bookings = new ArrayList<>();
        this.numOfAttendees = 0;
    }

    public Itinerary(String[] csvData) {
        this.createObject(csvData);
        this.leadAttendee = new LeadAttendee(this, csvData[3]);
        this.fetchBookings(csvData[0]);
    }

>>>>>>> Stashed changes
    @Override
    public Itinerary createObject(String[] csvData) {
        this.setId(csvData[0]);
        this.numOfAttendees = Integer.parseInt(csvData[1]);
        this.itineraryReference = csvData[2];

        if (leadAttendee == null) {
            this.leadAttendee = new LeadAttendee(this, csvData[3]);
        }

        return this;
    }

<<<<<<< Updated upstream
    private void fetchBookings(String id) {
        System.out.println("Fetching bookings for itinerary " + id);
=======
    public void fetchBookings(String id) {
>>>>>>> Stashed changes
        CSVReader csvReader = new CSVReader();
        ArrayList<Booking> bookings = new ArrayList<>();
        ArrayList<String[]> rawBookings = csvReader.getManyNestedEntitiesById("Booking", id, "itineraryId");


        for (String[] bookingData : rawBookings) {
            Booking booking = new Booking(this, bookingData[0]);
            bookings.add(booking.createObject(bookingData));
        }

        this.bookings = bookings;
    }


    public String getItineraryReference() {
        return this.itineraryReference;
    }

    public int getNumOfAttendees() {
        return this.numOfAttendees;
    }

    public ArrayList<Booking> getBookings() {
        return this.bookings;
    }
}
