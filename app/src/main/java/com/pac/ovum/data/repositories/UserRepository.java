package com.pac.ovum.data.repositories;

import androidx.lifecycle.LiveData;

import com.pac.ovum.data.dao.UserDao;
import com.pac.ovum.data.models.User;
import com.pac.ovum.utils.AppExecutors;

import java.util.List;

public class UserRepository {
    private final UserDao userDao;

    public UserRepository(UserDao userDao) {
        this.userDao = userDao;
    }

    public void insertUser(User user) {
        AppExecutors.getInstance().diskIO().execute(() -> userDao.insertUser(user));
    }

    public LiveData<User> getUserById(int userId) {
        return userDao.getUserById(userId);
    }

    public LiveData<User> loginUser(String email, String password) {
        return userDao.loginUser(email, password);
    }

    public LiveData<List<User>> getAllUsers() {
        return userDao.getAllUsers();
    }

    public void updateUser(User user) {
        AppExecutors.getInstance().diskIO().execute(() -> userDao.updateUser(user));
    }

    public void deleteUser(User user) {
        AppExecutors.getInstance().diskIO().execute(() -> userDao.deleteUser(user));
    }
}
