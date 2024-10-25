package com.example.ovum.models;

public class Patient {
    private String name;
    private String location;
    private String email;
    private String nextPeriodDate;
    private String cycleLength;
    private String latestPeriod;
    private String averageCycleLength;
    private String averagePeriodLength;
    private String dob;

    // Constructor
    public Patient(String name, String location, String email, String nextPeriodDate, String cycleLength, String latestPeriod, String averageCycleLength, String averagePeriodLength, String dob) {
        this.name = name;
        this.location = location;
        this.email = email;
        this.nextPeriodDate = nextPeriodDate;
        this.cycleLength = cycleLength;
        this.latestPeriod = latestPeriod;
        this.averageCycleLength = averageCycleLength;
        this.averagePeriodLength = averagePeriodLength;
        this.dob = dob;
    }

    // Getter methods
    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getEmail() {
        return email;
    }

    public String getNextPeriodDate() {
        return nextPeriodDate;
    }

    public String getCycleLength() {
        return cycleLength;
    }

    public String getLatestPeriod() {
        return latestPeriod;
    }

    public String getAverageCycleLength() {
        return averageCycleLength;
    }

    public String getAveragePeriodLength() {
        return averagePeriodLength;
    }
    public String getDob() {
        return dob;
    }

}
