package com.example.ovum.models;


import java.time.LocalDate;

/**
 * This  class is a description of the calendar events
 * Since the calendar is of the sunDeepK library, its Events logic cannot be easily manipulated to suit the app
 * Therefore this class defines the proper way that suits the events of the app user by abstracting out the necessary info from the sunDeepK Event
 *
 */
public class Event {

    LocalDate date;
    String eventDescription;

    public Event(LocalDate date, String eventDescription){
        this.date = date;
        this.eventDescription = eventDescription;
    }

    public LocalDate getDate(){
        return date;
    }
    public  String getEventDescription(){
        return eventDescription;
    }

}
