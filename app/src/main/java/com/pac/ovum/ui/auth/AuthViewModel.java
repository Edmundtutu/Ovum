package com.pac.ovum.ui.auth;

import android.app.Application;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.pac.ovum.data.database.AppDatabase;
import com.pac.ovum.data.models.CycleData;
import com.pac.ovum.data.models.User;
import com.pac.ovum.data.repositories.UserRepository;
import com.pac.ovum.data.services.AuthService;

import java.time.LocalDate;

public class AuthViewModel extends AndroidViewModel {
    
    private final AuthService authService;
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    public AuthViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        UserRepository userRepository = new UserRepository(database.userDao());
        authService = new AuthService(userRepository, application.getApplicationContext());
    }
    
    public LiveData<User> getCurrentUser() {
        return authService.getCurrentUser();
    }
    
    public LiveData<String> getAuthError() {
        return authService.getAuthError();
    }
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void register(String username, String email, String password, LocalDate dateOfBirth, String gender) {
        authService.register(username, email, password, dateOfBirth, gender);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean registerWithCycle(String username, String email, String password,
                                     LocalDate dateOfBirth, String gender, CycleData cycle) {
        authService.registerWithCycle(username, email, password, dateOfBirth, gender, cycle);
        return true;
    }
    
    public void login(String email, String password) {
        authService.login(email, password);
    }
    
    public void logout() {
        authService.logout();
    }
    
    public boolean isUserLoggedIn() {
        return authService.isUserLoggedIn();
    }
} 