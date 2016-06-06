package com.example.aashish.imagesearch;

import com.example.aashish.imagesearch.models.ApiResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by aashish on 6/6/16.
 * Retrofit API interface
 */
public interface ApiInterface {

    String ACTION = "query";
    String PROP = "pageimages";
    String FORMAT = "json";
    String PIPROP = "thumbnail";
    int PILIMIT= 50;
    String GENERATOR = "prefixsearch";

    @GET("w/api.php")
    Call<ApiResult> getPages(@Query("action") String action,
                             @Query("prop") String prop,
                             @Query("format") String format,
                             @Query("piprop") String piprop,
                             @Query("pilimit") int pilimit,
                             @Query("generator") String generator,
                             @Query("pithumbsize") float dimen,
                             @Query("gpssearch") String searchQuery);
}
