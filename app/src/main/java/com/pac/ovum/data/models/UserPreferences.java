package com.pac.ovum.data.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "userId",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = @Index("userId")
)
public class UserPreferences {
    @PrimaryKey
    private int userId; // Foreign key to User
    private int cycleLengthPreference; // User's preferred cycle length
    private String notificationSettings; // Stored in JSON format (e.g., {"reminder": true, "push": false})

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCycleLengthPreference() {
        return cycleLengthPreference;
    }

    public void setCycleLengthPreference(int cycleLengthPreference) {
        this.cycleLengthPreference = cycleLengthPreference;
    }

    public String getNotificationSettings() {
        return notificationSettings;
    }

    public void setNotificationSettings(String notificationSettings) {
        this.notificationSettings = notificationSettings;
    }
}
