package com.mafdy.udacity.movies;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mafdy.udacity.movies.Adapter.ReviewsAdapter;
import com.mafdy.udacity.movies.Database.Contract;
import com.mafdy.udacity.movies.Database.DbHelper;
import com.mafdy.udacity.movies.Object.MyApiResponse_Reviews;
import com.mafdy.udacity.movies.Object.MyApiResponse_Videos;
import com.mafdy.udacity.movies.Object.MyMovie;
import com.mafdy.udacity.movies.Object.MyReview;
import com.mafdy.udacity.movies.Object.MyVideo;
import com.mafdy.udacity.movies.Object.RetrofitInterface;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

import jp.wasabeef.recyclerview.animators.LandingAnimator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieDetailActivity extends Activity {

    ImageView mImageView_Poster,mImageView_PosterToolbar;
    TextView mTextView_Avg , mTextView_Title,mTextView_Date,mTextView_Plot;
    NestedScrollView mNestedScrollView;
    Button mButtonTrailers, mButtonReviews, mButtonFavourite;

    MyMovie mMovie;

    private DbHelper dbHelper;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        findViews();

        dbHelper = new DbHelper(this);


        Intent intent = getIntent();

        mMovie = (MyMovie)intent.getParcelableExtra("movie_detail");


        Picasso.with(this).load("http://image.tmdb.org/t/p/w342/" + mMovie.getBackdrop_path()).into(mImageView_PosterToolbar);

        Picasso.with(this).load("http://image.tmdb.org/t/p/w342/" + mMovie.getPoster_path()).into(mImageView_Poster);



        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);


        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorAccent));
        collapsingToolbarLayout.setTitle(mMovie.getTitle());

        mTextView_Plot.setText(mMovie.getOverview());

        mTextView_Title.setText(mMovie.getTitle());
        mTextView_Date.setText(mMovie.getRelease_date());


        mTextView_Avg.setText(mMovie.getVote_average() + "");


       getReviews();

       getTrailers();





        if (checkIfMovieIsInDb(mMovie)) {
            mButtonFavourite.setText("Marked As Favourite");
        }
        else
        {
            mButtonFavourite.setText("Marked As Not Favourite");
        }
        mButtonFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkIfMovieIsInDb(mMovie)) {
                    mButtonFavourite.setText("Marked As Favourite");
                    saveMovieInDb(mMovie);
                } else {
                    mButtonFavourite.setText("Marked As Not Favourite");
                    deleteMovieFromDb(mMovie);
                }
            }
        });

    }

   public void getTrailers() {


            mButtonTrailers.setEnabled(false);
            mButtonTrailers.setAlpha(0.5f);


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.themoviedb.org/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            RetrofitInterface service = retrofit.create(RetrofitInterface.class);

            Call<MyApiResponse_Videos> call_videos = service.getVideosForMovie(mMovie.getId(),MovieDetailActivity.this.getResources().getString(R.string.api_key));

            Response<MyApiResponse_Videos> response = null;
            try {
                call_videos.enqueue(new Callback<MyApiResponse_Videos>() {
                    @Override
                    public void onResponse(Call<MyApiResponse_Videos> call, Response<MyApiResponse_Videos> response) {


                        if(response != null) {
                            if(response.body() != null) {
                                if(response.body().getResults() != null) {
                                    final ArrayList<MyVideo> myVideos = new ArrayList<MyVideo>(Arrays.asList(response.body().getResults()));


                                    if(myVideos != null)
                                    {
                                        if(myVideos.size() > 0)
                                        {

                                            mButtonTrailers.setEnabled(true);
                                            mButtonTrailers.setAlpha(1f);

                                            CharSequence[] videosstrings = new CharSequence[myVideos.size()];

                                            int i =1;
                                            for(MyVideo v : myVideos)
                                            {
                                                videosstrings[i-1] = "Trailer " + i;
                                                i++;
                                            }

                                            final AlertDialog.Builder dialog = new AlertDialog.Builder(MovieDetailActivity.this)
                                                    .setItems(videosstrings, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                            Uri builtUri = Uri.parse("http://www.youtube.com").buildUpon().appendPath("watch")
                                                                    .appendQueryParameter("v", myVideos.get(which).getKey())
                                                                    .build();
                                                            Intent intent = new Intent(Intent.ACTION_VIEW);

                                                            intent.setData(builtUri);
                                                            if (intent.resolveActivity(getPackageManager()) != null) {
                                                                startActivity(intent);
                                                            }


                                                        }
                                                    });

                                            mButtonTrailers.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog.show();
                                                }
                                            });
                                        }
                                    }


                                }
                            }
                        }



                    }

                    @Override
                    public void onFailure(Call<MyApiResponse_Videos> call, Throwable t) {

                    }
                });



            } catch (Exception e) {
                e.printStackTrace();
            }




        }



    public void getReviews()
    {


        mButtonReviews.setEnabled(false);
        mButtonReviews.setAlpha(0.5f);

        mButtonTrailers.setEnabled(false);
        mButtonTrailers.setAlpha(0.5f);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitInterface service = retrofit.create(RetrofitInterface.class);

        Call<MyApiResponse_Reviews> call_reviews = service.getReviewsForMovie(mMovie.getId(),MovieDetailActivity.this.getResources().getString(R.string.api_key));

        Response<MyApiResponse_Reviews> response = null;
        try {
            call_reviews.enqueue(new Callback<MyApiResponse_Reviews>() {
                @Override
                public void onResponse(Call<MyApiResponse_Reviews> call, Response<MyApiResponse_Reviews> response) {

                    if(response != null) {
                        if (response.body() != null) {
                            if (response.body().getResults() != null) {
                                ArrayList<MyReview> myReviews = new ArrayList<MyReview>(Arrays.asList(response.body().getResults()));




                                if(myReviews != null)
                                {
                                    if(myReviews.size() > 0)
                                    {


                                        mButtonReviews.setEnabled(true);
                                        mButtonReviews.setAlpha(1f);


                                        final  Dialog dialog = new Dialog(MovieDetailActivity.this,R.style.DialogSlideAnim);
                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        dialog.setContentView(R.layout.reviews_dialog_layout);
                                        dialog.setCanceledOnTouchOutside(true);
                                        dialog.setCancelable(true);
                                        dialog.setTitle("Reviews");

                                        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.85);
                                        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.70);

                                        dialog.getWindow().setLayout(width,height);

                                        RecyclerView mRecyclerView  = (RecyclerView) dialog.findViewById(R.id.recyclerview_reviews);


                                        ReviewsAdapter mAdapter = new ReviewsAdapter(MovieDetailActivity.this, myReviews);

                                        LandingAnimator animator = new LandingAnimator();
                                        animator.setInterpolator(new OvershootInterpolator());

                                        mRecyclerView.setItemAnimator(animator);

                                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MovieDetailActivity.this);


                                        mRecyclerView.setLayoutManager(linearLayoutManager);


                                        mRecyclerView.setAdapter(mAdapter);


                                        mButtonReviews.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog.show();
                                            }
                                        });




                                    }

                                }
                            }
                        }
                    }




                }

                @Override
                public void onFailure(Call<MyApiResponse_Reviews> call, Throwable t) {

                }
            });



        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    private void deleteMovieFromDb(MyMovie movie) {
        String selection = Contract.TableEntry.COLUMN_MOVIEDBID + "=?";
        String[] selectionArgs = {String.valueOf(movie.getId())};

        getContentResolver().delete(Contract.TableEntry.CONTENT_URI.buildUpon().appendPath(movie.getId()+"").build(), selection, selectionArgs);
    }

    private void saveMovieInDb(MyMovie movie) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.TableEntry.COLUMN_MOVIEDBID, movie.getId());

        getContentResolver().insert(Contract.TableEntry.CONTENT_URI, contentValues);
    }

    private boolean checkIfMovieIsInDb(MyMovie movie) {
        Cursor cursor = getContentResolver().query(
                Contract.TableEntry.CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int movieId = cursor.getInt(
                        cursor.getColumnIndex(Contract.TableEntry.COLUMN_MOVIEDBID));
                if (movieId == movie.getId()) {
                    return true;
                }
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return false;
    }


    public void findViews()
    {
        mImageView_Poster = (ImageView) findViewById(R.id.imageView_MoviePoster);
        mImageView_PosterToolbar = (ImageView) findViewById(R.id.imageView_MoviePosterToolbar);
        mNestedScrollView = (NestedScrollView) findViewById(R.id.nestedscrollview);
        mTextView_Avg = (TextView) findViewById(R.id.textView_VoteAvg);
        mTextView_Date = (TextView) findViewById(R.id.textView_ReleaseDate);
        mTextView_Title = (TextView) findViewById(R.id.textView_Title);
        mTextView_Plot = (TextView) findViewById(R.id.textView_Plot);
        mButtonReviews = (Button) findViewById(R.id.button_Reviews);
        mButtonTrailers = (Button) findViewById(R.id.button_Trailers);
        mButtonFavourite = (Button) findViewById(R.id.button_favourite);

    }
}
