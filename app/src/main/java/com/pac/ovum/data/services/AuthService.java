package com.pac.ovum.data.services;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pac.ovum.data.database.AppDatabase;
import com.pac.ovum.data.models.CycleData;
import com.pac.ovum.data.models.User;
import com.pac.ovum.data.repositories.UserRepository;
import com.pac.ovum.utils.AppExecutors;
import com.pac.ovum.utils.PasswordUtils;

import java.time.LocalDate;
import java.util.List;

public class AuthService {
    private static final String TAG = "AuthService";
    private final UserRepository userRepository;
    private final Context context;
    private final SessionManager sessionManager;
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final MutableLiveData<String> authError = new MutableLiveData<>();
    private final AppDatabase database;

    public AuthService(UserRepository userRepository, Context context) {
        this.userRepository = userRepository;
        this.context = context;
        this.sessionManager = new SessionManager(context);
        this.database = AppDatabase.getInstance(context);
        
        // Check if user is already logged in
        if (sessionManager.isLoggedIn()) {
            int userId = sessionManager.getUserId();
            if (userId != -1) {
                // Load user from database
                loadUserById(userId);
            }
        }
    }

    private void loadUserById(int userId) {
        LiveData<User> userLiveData = userRepository.getUserById(userId);
        AppExecutors.getInstance().mainThread().execute(() -> {
            userLiveData.observeForever(user -> {
                if (user != null) {
                    currentUser.setValue(user);
                    Log.d(TAG, "User auto-logged in: " + user.getEmail());
                } else {
                    // User not found in database, clear session
                    sessionManager.logoutUser();
                }
                // Remove the observer after we get the result
                userLiveData.removeObserver(u -> {});
            });
        });
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public LiveData<String> getAuthError() {
        return authError;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void register(String username, String email, String password, LocalDate dateOfBirth, String gender) {
        // Handle errors first
        handleError(username,email,password,dateOfBirth,gender);
        // Create user object
        User user = new User();
        user.setUserName(username);
        user.setEmail(email);
        // Hash password before storing
        user.setPassword(PasswordUtils.hashPassword(password));
        user.setDateOfBirth(dateOfBirth);
        user.setGender(gender);
        user.setRegistrationDate(LocalDate.now());

        // Save user
        userRepository.insertUser(user);
        Log.d(TAG, "User registered: " + email);
        
        // Login the newly registered user
        login(email, password);
    }

    // Add this method to your AuthService class
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void registerWithCycle(String username, String email, String password,
                                LocalDate dateOfBirth, String gender, CycleData cycle) {
        // Handle errors first
        handleError(username,email,password,dateOfBirth,gender);

        // Log the parameters to confirm they are being sent
        Log.d(TAG, "REGISTRATION START: User=" + username + ", Email=" + email + ", Cycle Length=" + cycle.getCycleLength());
        
        // Create new user
        User user = new User();
        user.setUserName(username);
        user.setEmail(email);
        user.setPassword(PasswordUtils.hashPassword(password));
        user.setDateOfBirth(dateOfBirth);
        user.setGender(gender);
        user.setRegistrationDate(LocalDate.now());

        // Use AppExecutors for background operation
        AppExecutors.getInstance().diskIO().execute(() -> {
            try {
                Log.d(TAG, "Starting DB transaction in background thread");
                
                // Insert user and cycle in a single transaction
                int userId = database.insertUserWithCycle(user, cycle);
                
                // Check if user was inserted successfully
                if (userId == -1) {
                    Log.e(TAG, "DB TRANSACTION FAILED: User and cycle insertion failed");
                    AppExecutors.getInstance().mainThread().execute(() -> 
                        authError.setValue("Failed to register user - database error"));
                    return;
                }
                
                Log.d(TAG, "DB TRANSACTION SUCCESS: User ID=" + userId + ", Cycle ID=" + cycle.getCycleId());
                
                // IMPORTANT: Verify data was actually inserted
                AppExecutors.getInstance().diskIO().execute(() -> {
                    // Double-check the user exists in the database
                    List<User> users = database.userDao().getAllUsersSync();
                    Log.d(TAG, "VERIFICATION: Found " + users.size() + " users in database");
                    for (User u : users) {
                        Log.d(TAG, "  User ID=" + u.getUserId() + ", Name=" + u.getUserName() + ", Email=" + u.getEmail());
                    }
                    
                    // Double-check the cycle exists and is ongoing
                    CycleData ongoingCycle = database.cycleDao().getOngoingCycleByUserIdSync(userId);
                    if (ongoingCycle != null) {
                        Log.d(TAG, "VERIFICATION: Found ongoing cycle with ID=" + ongoingCycle.getCycleId() + 
                              " for user ID=" + userId);
                    } else {
                        Log.e(TAG, "VERIFICATION ERROR: No ongoing cycle found for user ID=" + userId);
                    }
                });
                
                // Switch to main thread for login
                AppExecutors.getInstance().mainThread().execute(() -> {
                    Log.d(TAG, "Starting login process after registration");
                    login(email, password);
                });
            } catch (Exception e) {
                Log.e(TAG, "REGISTRATION ERROR: " + e.getMessage(), e);
                // Handle error on main thread
                AppExecutors.getInstance().mainThread().execute(() -> {
                    authError.setValue("Failed to register user: " + e.getMessage());
                });
            }
        });
    }

    public void login(String email, String password) {
        // Clear any previous errors
        authError.setValue(null);
        Log.d(TAG, "LOGIN START: Email=" + email);
        
        // Validate input
        if (email == null || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Log.e(TAG, "LOGIN ERROR: Invalid email format");
            authError.setValue("Please enter a valid email address");
            return;
        }
        
        if (password == null || password.isEmpty()) {
            Log.e(TAG, "LOGIN ERROR: Empty password");
            authError.setValue("Password cannot be empty");
            return;
        }

        // Hash password for comparison
        String hashedPassword = PasswordUtils.hashPassword(password);
        Log.d(TAG, "LOGIN: Querying database for user");
        
        // Double-check user in database directly before LiveData query
        AppExecutors.getInstance().diskIO().execute(() -> {
            // Check directly if user exists using DAO methods
            try {
                int count = database.userDao().isEmailTaken(email);
                Log.d(TAG, "LOGIN VERIFICATION: Found " + count + " users with email " + email);
                
                // Try direct login with sync method
                User directUser = database.userDao().loginUserSync(email, hashedPassword);
                if (directUser != null) {
                    Log.d(TAG, "DIRECT LOGIN CHECK: Found user with ID=" + directUser.getUserId());
                } else {
                    Log.e(TAG, "DIRECT LOGIN CHECK: No user found with email/password combination");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error checking users: " + e.getMessage());
            }
        });
        
        // Query database - we need to observe the LiveData result
        LiveData<User> userLiveData = userRepository.loginUser(email, hashedPassword);
        AppExecutors.getInstance().mainThread().execute(() -> {
            Log.d(TAG, "LOGIN: Observing LiveData result");
            userLiveData.observeForever(user -> {
                if (user != null) {
                    Log.d(TAG, "LOGIN SUCCESS: User found with ID=" + user.getUserId() + ", Name=" + user.getUserName());
                    // Save login session
                    sessionManager.createLoginSession(user);
                    
                    // Update current user
                    currentUser.setValue(user);
                    Log.d(TAG, "User logged in: " + user.getEmail());
                } else {
                    Log.e(TAG, "LOGIN FAILED: No user found with matching credentials");
                    authError.setValue("Invalid email or password");
                }
                // Remove the observer after we get the result
                userLiveData.removeObserver(user1 -> {});
            });
        });
    }

    public void logout() {
        // Clear session data
        sessionManager.logoutUser();
        
        // Clear current user
        currentUser.setValue(null);
        Log.d(TAG, "User logged out");
    }
    
    public boolean isUserLoggedIn() {
        return sessionManager.isLoggedIn();
    }

    private void handleError(String username, String email, String password, LocalDate dateOfBirth, String gender){
        // Clear any previous errors
        authError.setValue(null);

        // Validate input
        if (username == null || username.trim().isEmpty()) {
            authError.setValue("Username cannot be empty");
            return;
        }

        if (email == null || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            authError.setValue("Please enter a valid email address");
            return;
        }

        if (password == null || password.length() < 6) {
            authError.setValue("Password must be at least 6 characters");
            return;
        }

        // Check if email is already taken
        if (userRepository.isEmailTaken(email)) {
            authError.setValue("Email address is already registered");
            return;
        }
    }
} 