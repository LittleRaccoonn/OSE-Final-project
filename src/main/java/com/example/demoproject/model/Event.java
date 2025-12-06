package com.example.demoproject.model;

public class Event {
    private String organizerName;
    private String eventName;
    private String place;
    private String dateTime;
    private int duration;
    private int peopleQuantity;
    private String phoneNumber;

    // Конструктор по умолчанию для Jackson
    public Event() {}

    public Event(String organizerName, String eventName, String place,
                 String dateTime, int duration, int peopleQuantity,
                 String phoneNumber) {
        this.organizerName = organizerName;
        this.eventName = eventName;
        this.place = place;
        this.dateTime = dateTime;
        this.duration = duration;
        this.peopleQuantity = peopleQuantity;
        this.phoneNumber = phoneNumber;
    }

    // Геттеры
    public String getOrganizerName() { return organizerName; }
    public String getEventName() { return eventName; }
    public String getPlace() { return place; }
    public String getDateTime() { return dateTime; }
    public int getDuration() { return duration; }
    public int getPeopleQuantity() { return peopleQuantity; }
    public String getPhoneNumber() { return phoneNumber; }

    // Сеттеры
    public void setOrganizerName(String organizerName) { this.organizerName = organizerName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    public void setPlace(String place) { this.place = place; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }
    public void setDuration(int duration) { this.duration = duration; }
    public void setPeopleQuantity(int peopleQuantity) { this.peopleQuantity = peopleQuantity; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    // Для CSV экспорта
    @Override
    public String toString() {
        return String.format("\"%s\",\"%s\",\"%s\",\"%s\",%d,%d,\"%s\"",
                organizerName, eventName, place, dateTime,
                duration, peopleQuantity, phoneNumber);
    }
}