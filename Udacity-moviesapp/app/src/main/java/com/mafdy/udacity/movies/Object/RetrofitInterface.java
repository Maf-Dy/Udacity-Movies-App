package com.mafdy.udacity.movies.Object;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by SBP on 02/04/2018.
 */

public interface RetrofitInterface {

    @GET("3/movie/popular")
    Call<MyApiResponse> getMoviesPopular(@Query("api_key") String key);

    @GET("3/movie/top_rated")
    Call<MyApiResponse> getMoviesToprated(@Query("api_key") String key);


    @GET("3/movie/{id}/videos")
    Call<MyApiResponse_Videos> getVideosForMovie(@Path("id") int id,@Query("api_key") String key);

    @GET("3/movie/{id}")
    Call<MyMovie> getMovie(@Path("id") int id,@Query("api_key") String key);

    @GET("3/movie/{id}/reviews")
    Call<MyApiResponse_Reviews> getReviewsForMovie(@Path("id") int id,@Query("api_key") String key);


}
