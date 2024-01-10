package org.example;

public class LeadAttendee extends DatabaseTable<LeadAttendee> {
    private String firstName;
    private String lastName;
    private Itinerary itinerary;

    public LeadAttendee(Itinerary itinerary, String id) {
        this.itinerary = itinerary;
        this.getEntityById("LeadAttendee", id);
    }

    public LeadAttendee(Itinerary itinerary, String firstName, String lastName) {
        this.setId(this.dbClient.getUniqueUUID());
        this.itinerary = itinerary;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public LeadAttendee createObject(String[] csvData) {
        this.setId(csvData[0]);
        this.firstName = csvData[1];
        this.lastName = csvData[2];

        if (itinerary == null) {
            this.itinerary = new Itinerary(this, csvData[3]);
        }

        return this;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }
}
