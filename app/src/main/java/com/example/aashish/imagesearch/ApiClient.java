package com.example.aashish.imagesearch;

import com.example.aashish.imagesearch.models.ApiResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by aashish on 6/6/16.
 * Base API class
 */
public class ApiClient {

    public static final String BASE_URL = "https://en.wikipedia.org/";
    public static Gson defaultGson;
    public static Retrofit retrofit = null;

    static {
        defaultGson = new GsonBuilder()
                .setLenient()
                .registerTypeAdapter(ApiResult.class, new Deserializer.PageDeserializer())
                .create();
    }


    public static Retrofit getClient() {
        if (retrofit == null) {

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(defaultGson))
                    .build();
        }
        return retrofit;
    }
}
