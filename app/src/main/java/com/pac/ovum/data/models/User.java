package com.pac.ovum.data.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

@Entity
public class User {
    @PrimaryKey(autoGenerate = true)
    private int userId; // Unique user ID
    private String userName; // User's name
    private String email; // Email address
    private String password; // Hashed password for authentication
    private LocalDate dateOfBirth; // Date of birth (String or Date format)
    private String gender; // Male, Female, Other, etc.
    private String profileImageUri; // Path to the user's profile image
    private LocalDate registrationDate; // When the user registered

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfileImageUri() {
        return profileImageUri;
    }

    public void setProfileImageUri(String profileImageUri) {
        this.profileImageUri = profileImageUri;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", ProfileImageUri='" + profileImageUri + '\'' +
                '}';
    }
}
