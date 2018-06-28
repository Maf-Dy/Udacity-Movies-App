package com.mafdy.udacity.movies;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.mafdy.udacity.movies.Adapter.RecyclerViewAdapter;
import com.mafdy.udacity.movies.Database.Contract;
import com.mafdy.udacity.movies.Database.DbHelper;
import com.mafdy.udacity.movies.Object.MyApiResponse;
import com.mafdy.udacity.movies.Object.MyMovie;
import com.mafdy.udacity.movies.Object.RetrofitInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import jp.wasabeef.recyclerview.animators.LandingAnimator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<MyApiResponse> {

    final int loaderid= 223;
    public static Loader<MyApiResponse> loader;
    private DbHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DbHelper(this);

        LoaderManager loaderManager = getLoaderManager();


        Bundle b = new Bundle();

        if(savedInstanceState != null) {
            if (savedInstanceState.getString("recyclerorder", "").equals("")) {

                saveOrder("mostpopular");
                b.putString("sortby", "mostpopular");
            } else {
                b.putString("sortby", getOrder());
            }

            if(savedInstanceState.getInt("recyclerposition",0) != 0)
            {
                b.putInt("recyclerposition",savedInstanceState.getInt("recyclerposition"));
            }

        }
        else
        {
            b.putString("sortby","mostpopular");
        }


        loader = loaderManager.getLoader(loaderid);
        // If the Loader was null, initialize it. Else, restart it.
        if(loader==null){
            loaderManager.initLoader(loaderid, b ,this);
        }else{
            loaderManager.restartLoader(loaderid, b, this);
        }





    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);



    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        if(gridManager != null)
             outState.putInt("recyclerposition",gridManager.findFirstCompletelyVisibleItemPosition());

        outState.putString("recyclerorder",getOrder());


        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {


        super.onRestoreInstanceState(savedInstanceState);

    }

    @Override
    public Loader<MyApiResponse> onCreateLoader(int i, final Bundle bundle) {





        return new AsyncTaskLoader<MyApiResponse>(this) {

            private MyApiResponse responsee;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();

                if (responsee == null)
                {
                    forceLoad();
                } else
                {
                    deliverResult(responsee);
                }

            }

            @Override
            public void deliverResult(MyApiResponse data) {
                responsee = data;
                super.deliverResult(data);
            }

            @Override
            public MyApiResponse loadInBackground() {


                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://api.themoviedb.org/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                RetrofitInterface service = retrofit.create(RetrofitInterface.class);
                Call<MyApiResponse> call1 = null;


                if (bundle.getString("sortby").equals("toprated"))
                 call1 = service.getMoviesToprated(MainActivity.this.getResources().getString(R.string.api_key));
                else if (bundle.getString("sortby").equals("mostpopular"))
                    call1 = service.getMoviesPopular(MainActivity.this.getResources().getString(R.string.api_key));
                else
                {
                    Cursor cursor = getContentResolver().query(
                            Contract.TableEntry.CONTENT_URI, null, null, null, null);

                    final MyMovie[] moviesresults = new MyMovie[cursor.getCount()];
                    int i=0;

                    if (cursor != null) {
                        while (cursor.moveToNext()) {
                            int movieId = cursor.getInt(
                                    cursor.getColumnIndex(Contract.TableEntry.COLUMN_MOVIEDBID));

                            try {
                                Response<MyMovie> r = service.getMovie(movieId,MainActivity.this.getResources().getString(R.string.api_key)).execute();

                                moviesresults[i] = r.body();
                                i++;


                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(moviesresults != null) {
                                    if(moviesresults.length > 0) {
                                        MyApiResponse response = new MyApiResponse();
                                        response.setPage(1);
                                        response.setResults(moviesresults);
                                        response.setTotal_results(moviesresults.length);

                                        responsee = response;
                                        findAndSetViews(response,bundle.getInt("recyclerposition",0));
                                    }
                                }
                            }
                        });



                    }
                    if (cursor != null) {
                        cursor.close();
                    }
                }

                if(call1 != null) {
                    call1.enqueue(new Callback<MyApiResponse>() {
                        @Override
                        public void onResponse(Call<MyApiResponse> call, Response<MyApiResponse> response) {
                            if(response != null) {
                                if(response.body() != null) {
                                    responsee = response.body();
                                    findAndSetViews(response.body(),bundle.getInt("recyclerposition",0));
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyApiResponse> call, Throwable t) {

                        }
                    });
                }



                return null;
            }
        } ;
    }

    @Override
    public void onLoadFinished(Loader<MyApiResponse> loader, MyApiResponse myApiResponse) {


        if (myApiResponse != null)
        {
            // load ui

            if(myApiResponse.getResults() != null)
            findAndSetViews(myApiResponse,0);
        }


    }

    @Override
    public void onLoaderReset(Loader<MyApiResponse> loader) {



    }

    RecyclerView mRecyclerView;

    private RecyclerViewAdapter mAdapter;

    static ArrayList<MyMovie> movieList = new ArrayList<>();

    GridLayoutManager gridManager;

    public void findAndSetViews(MyApiResponse response, int position)
    {
        mRecyclerView = (RecyclerView) this.findViewById(R.id.recyclerview);

        movieList = new ArrayList<MyMovie>(Arrays.asList(response.getResults()));

        mAdapter = new RecyclerViewAdapter(MainActivity.this, movieList);

       // mRecyclerView.setHasFixedSize(true);

        LandingAnimator animator = new LandingAnimator();
        animator.setInterpolator(new OvershootInterpolator());

        mRecyclerView.setItemAnimator(animator);

         gridManager = new GridLayoutManager(this,2);


        mRecyclerView.setLayoutManager(gridManager);

        mRecyclerView.setAdapter(mAdapter);

        gridManager.scrollToPosition(position);



        mAdapter.SetOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, MyMovie model) {

                //handle item click events here

                Intent i = new Intent(MainActivity.this,MovieDetailActivity.class);
                i.putExtra("movie_detail", model);

                MainActivity.this.startActivity(i);


            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_scrolling, menu);

        return super.onCreateOptionsMenu(menu);
    }

    public void saveOrder(String order)
    {
        SharedPreferences sharedPref = getSharedPreferences(
                "SharedPref", Context.MODE_PRIVATE);
        sharedPref.edit().putString("order",order).apply();
    }

    public String getOrder()
    {
        SharedPreferences sharedPref = getSharedPreferences(
                "SharedPref", Context.MODE_PRIVATE);

        return sharedPref.getString("order","");
    }

    public void savePosition(int position)
    {
        SharedPreferences sharedPref = getSharedPreferences(
                "SharedPref", Context.MODE_PRIVATE);
        sharedPref.edit().putInt("position",position).apply();
    }

    public int getPosition()
    {
        SharedPreferences sharedPref = getSharedPreferences(
                "SharedPref", Context.MODE_PRIVATE);

        return sharedPref.getInt("position",0);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:

                AlertDialog.Builder build = new AlertDialog.Builder(this,AlertDialog.THEME_DEVICE_DEFAULT_DARK);

                build.setTitle("Sort By");
                build.setPositiveButton("Most Popular", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Bundle b = new Bundle();
                        b.putString("sortby","mostpopular");

                        saveOrder("mostpopular");

                        LoaderManager loaderManager = getLoaderManager();
                        loader = loaderManager.getLoader(loaderid);
                        // If the Loader was null, initialize it. Else, restart it.
                        if(loader==null){
                            loaderManager.initLoader(loaderid, b ,MainActivity.this);
                        }else{
                            loaderManager.restartLoader(loaderid, b, MainActivity.this);
                        }
                    }
                });
                build.setNegativeButton("Top rated", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Bundle b = new Bundle();
                        b.putString("sortby","toprated");

                        saveOrder("toprated");

                        LoaderManager loaderManager = getLoaderManager();
                        loader = loaderManager.getLoader(loaderid);
                        // If the Loader was null, initialize it. Else, restart it.
                        if(loader==null){
                            loaderManager.initLoader(loaderid, b ,MainActivity.this);
                        }else{
                            loaderManager.restartLoader(loaderid, b, MainActivity.this);
                        }
                    }
                });
                build.setNeutralButton("Favourites", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Bundle b = new Bundle();
                        b.putString("sortby","favourites");

                        saveOrder("favourites");

                        LoaderManager loaderManager = getLoaderManager();
                        loader = loaderManager.getLoader(loaderid);
                        // If the Loader was null, initialize it. Else, restart it.
                        if(loader==null){
                            loaderManager.initLoader(loaderid, b ,MainActivity.this);
                        }else{
                            loaderManager.restartLoader(loaderid, b, MainActivity.this);
                        }
                    }
                });

                build.create().show();



                return true;



            default:
                return super.onOptionsItemSelected(item);
        }


    }
}
