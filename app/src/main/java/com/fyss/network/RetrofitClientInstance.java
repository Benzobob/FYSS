package com.fyss.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {

  //  private static final String API_BASE_URL = "http://10.0.2.2:8080/FyssRestApi/webresources/";
   // private static final String API_BASE_URL = "http://192.168.43.72:8080/FyssRestApi/webresources/";
    private static final String API_BASE_URL = "http://192.168.137.1:8080/FyssRestApi/webresources/";
    //private static final String API_BASE_URL = "http://192.168.0.21:8080/FyssRestApi/webresources/";
    private static Retrofit retrofit;
    private static Gson gson;


    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
