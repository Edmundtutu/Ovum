package com.example.ovum.models;

import java.time.LocalDate;

public class Symptom extends Event{

    public Symptom(LocalDate date, String symptomDescription) {
        super(date, symptomDescription);
    }
    public  String getSymptomDescription(){
        return eventDescription;
    }
    public LocalDate getDate(){
        return date;
    }
}
