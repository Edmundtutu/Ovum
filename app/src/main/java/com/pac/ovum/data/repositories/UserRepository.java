package com.pac.ovum.data.repositories;

import androidx.lifecycle.LiveData;

import com.pac.ovum.data.dao.UserDao;
import com.pac.ovum.data.models.CycleData;
import com.pac.ovum.data.models.User;
import com.pac.ovum.utils.AppExecutors;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class UserRepository {
    private final UserDao userDao;

    public UserRepository(UserDao userDao) {
        this.userDao = userDao;
    }

    public void insertUser(User user) {
        AppExecutors.getInstance().diskIO().execute(() -> userDao.insertUser(user));
    }

    /**
     * Inserts a User into the database and returns the new row's ID.
     * IMPORTANT: This method must be called from within a background thread!
     *
     * @param user The User object to insert.
     * @return The generated userId of the newly inserted User, or -1 if insertion failed.
     */
    public int insertUserSync(User user) {
        // Since this method should already be called from a background thread,
        // we can directly perform the database operations
        userDao.insertUser(user);
        List<User> users = userDao.getAllUsersSync();
        if (!users.isEmpty()) {
            return users.get(users.size() - 1).getUserId();
        }
        return -1;
    }
    
    /**
     * Inserts both a user and their cycle data in a single operation to prevent foreign key issues.
     * IMPORTANT: This method must be called from within a background thread!
     *
     * @param user The User object to insert
     * @param cycle The CycleData object to insert (user ID will be set automatically)
     * @param cycleRepository The CycleRepository to use for cycle insertion
     * @return The generated userId, or -1 if the operation failed
     */
    public int insertUserWithCycle(User user, CycleData cycle, CycleRepository cycleRepository) {
        try {
            // Insert user and get ID
            int userId = insertUserSync(user);
            if (userId == -1) {
                return -1;
            }
            
            // Set user ID on cycle and insert it
            cycle.setUserId(userId);
            long cycleId = cycleRepository.insertCycleSync(cycle);
            
            return (cycleId != -1) ? userId : -1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public LiveData<User> getUserById(int userId) {
        return userDao.getUserById(userId);
    }

    public LiveData<User> loginUser(String email, String password) {
        return userDao.loginUser(email, password);
    }

    public boolean isEmailTaken(String email) {
        try {
            Future<Boolean> future = AppExecutors.getInstance().diskIO().submit(() -> 
                userDao.isEmailTaken(email) > 0
            );
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
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
