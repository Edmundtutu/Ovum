package com.pac.ovum.data.services.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton class for Retrofit client configuration
 */
public class RetrofitClient {
    // Base URL for development environment, can be easily changed for production
    private static final String BASE_URL = "https://d697-154-72-203-214.ngrok-free.app/api/";
    private static RetrofitClient instance;
    private final Retrofit retrofit;

    private RetrofitClient() {
        // Configure logging interceptor for debugging
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Configure OkHttpClient with interceptors
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(chain -> {
                    // Add authentication header - for future implementation
                    // This would use a token manager or shared preferences to get the token
//                     String token = TokenManager.getInstance().getToken(); //TODO: To create a Token Manager class for single source of truth tokens
                    // for now lets just provide the Token  the simple way
                    String token = "2|BkcO1xqx2dyoCOHILZCEadmepppHWolsgw9J0zCm8ca965cf";
                    return chain.proceed(chain.request().newBuilder()
                             .header("Authorization", "Bearer " + token)
                            .build());
                })
                .build();

        // Configure Gson for date/time handling
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                // Add any type adapters needed for LocalDate or LocalDateTime if needed
                .create();

        // Create Retrofit instance
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    /**
     * Get singleton instance of RetrofitClient
     * @return RetrofitClient instance
     */
    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    /**
     * Get API service interface
     * @param serviceClass The class of the API service interface
     * @param <T> The type of the API service interface
     * @return API service instance
     */
    public <T> T getService(Class<T> serviceClass) {
        return retrofit.create(serviceClass);
    }
} 