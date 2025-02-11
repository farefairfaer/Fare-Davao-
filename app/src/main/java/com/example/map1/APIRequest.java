package com.example.map1;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIRequest {

        @GET("search")
        Call<List<resultData>> geocode(
                @Query("q") String searchTerm,
                @Query("format") String format,
                @Query("addressdetails") int addressdetails,
                @Query("viewbox") String viewbox,
                @Query("bounded") int bounded
        );
    }

