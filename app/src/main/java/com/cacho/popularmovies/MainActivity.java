package com.cacho.popularmovies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Spinner;

import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static com.cacho.popularmovies.MoviesGetter.REQUEST_POPULAR_BASE;
import static com.cacho.popularmovies.MoviesGetter.REQUEST_TOP_RATED_BASE;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, OnTaskDoneListener {
    private Spinner spinnerSorting;
    private GridView gridView;
    private Boolean isPopular = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerSorting = (Spinner) findViewById(R.id.spinnerSorting);
        spinnerSorting.setOnItemSelectedListener(this);
        gridView = (GridView) findViewById(R.id.gridview);
        getMovies();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        isPopular =  position == 0 ? true : false;
        getMovies();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    private void getMovies(){
        if(checkForInternet()) {
            String url = isPopular ? REQUEST_POPULAR_BASE : REQUEST_TOP_RATED_BASE;
            new MoviesGetter(this).execute(url);
        }
    }

    @Override
    public void onTaskDone(String responseData) {
        List<Movie> moviesArray = null;
        try {
            moviesArray = JsonUtils.parseMoviesResult(responseData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MoviesAdapter moviesAdapter = new MoviesAdapter(this, moviesArray);
        gridView.setAdapter(moviesAdapter);
        final List<Movie> finalMoviesArray = moviesArray;
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Movie selectedMovie = finalMoviesArray.get(position);

                Intent i = new Intent(getBaseContext(), MovieDetails.class);
                i.putExtra("title", selectedMovie.getTitle());
                i.putExtra("overview", selectedMovie.getOverview());
                i.putExtra("poster_path", selectedMovie.getPoster_path());
                i.putExtra("release_date", selectedMovie.getRelease_date());
                i.putExtra("vote", selectedMovie.getVote());
                startActivity(i);

            }
        });
    }

    @Override
    public void onError() {
        Log.wtf("CACHO", "HTTP ERROR");
    }


    private Boolean checkForInternet(){
        ConnectivityManager mgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = mgr.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}
