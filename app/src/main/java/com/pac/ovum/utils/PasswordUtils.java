package com.pac.ovum.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PasswordUtils {
    
    /**
     * Hash a password using SHA-256.
     * Note: In a production environment, you would use a more secure hashing algorithm
     * with salting (e.g., BCrypt, PBKDF2, or Argon2), but this is a simple example.
     * 
     * @param plainPassword The plain text password to hash
     * @return The hashed password
     */
    public static String hashPassword(String plainPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            
            // Android requires API level 26+ for Base64.getEncoder()
            // For compatibility with lower API levels, use android.util.Base64 instead
            return android.util.Base64.encodeToString(hash, android.util.Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }
    
    /**
     * Validates if the plain password matches the hashed password.
     * 
     * @param plainPassword The plain text password to check
     * @param hashedPassword The stored hashed password
     * @return true if the passwords match, false otherwise
     */
    public static boolean validatePassword(String plainPassword, String hashedPassword) {
        String hashedInput = hashPassword(plainPassword);
        return hashedInput.equals(hashedPassword);
    }
} 