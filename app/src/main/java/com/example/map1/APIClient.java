package com.example.map1;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    private static final String BASE_URL = "https://nominatim.openstreetmap.org/";
    private static APIRequest service;

    public static APIRequest getService() {
        if (service == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()) // Use Gson for JSON to Object conversion
                    .build();

            service = retrofit.create(APIRequest.class); // Create the Retrofit service
        }
        return service;
    }
}

