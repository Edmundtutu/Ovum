package com.pac.ovum.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.pac.ovum.data.models.User;

import java.util.List;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    @Query("SELECT * FROM User WHERE userId = :userId")
    LiveData<User> getUserById(int userId);

    @Query("SELECT * FROM User WHERE email = :email AND password = :password")
    LiveData<User> loginUser(String email, String password);

    @Query("SELECT * FROM User")
    LiveData<List<User>> getAllUsers();

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);
}
