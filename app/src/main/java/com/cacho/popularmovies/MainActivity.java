package com.cacho.popularmovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

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
import android.widget.Toast;

import org.json.JSONException;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import model.Movie;
import model.MovieDAO;
import model.MovieRoomDatabase;

import static com.cacho.popularmovies.MainActivity.Sorting.FAVOURITES;
import static com.cacho.popularmovies.MainActivity.Sorting.POPULAR;
import static com.cacho.popularmovies.MainActivity.Sorting.TOP_RATED;
import static com.cacho.popularmovies.MoviesGetter.REQUEST_POPULAR_BASE;
import static com.cacho.popularmovies.MoviesGetter.REQUEST_TOP_RATED_BASE;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, OnTaskDoneListener {
    private Spinner spinnerSorting;
    private GridView gridView;
    private Sorting sortingType = POPULAR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerSorting = (Spinner) findViewById(R.id.spinnerSorting);
        spinnerSorting.setOnItemSelectedListener(this);
        gridView = (GridView) findViewById(R.id.gridview);
        MovieRoomDatabase db = MovieRoomDatabase.getDatabase(this);
        getMovies(db);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        sortingType = position == 0 ? POPULAR : position == 1 ? TOP_RATED : FAVOURITES;
        MovieRoomDatabase db = MovieRoomDatabase.getDatabase(this);
        getMovies(db);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    private void getMovies(MovieRoomDatabase db) {
        MovieDAO mMoviesDao = db.movieDao();

        if (sortingType != FAVOURITES) {
            if (checkForInternet()) {
                //String url = isPopular ? REQUEST_POPULAR_BASE : REQUEST_TOP_RATED_BASE;
                String url = sortingType == POPULAR ? REQUEST_POPULAR_BASE : REQUEST_TOP_RATED_BASE;
                new MoviesGetter(this).execute(url);
            } else {
                Toast.makeText(getApplicationContext(), "Sorry, there was a problem loading the movies", Toast.LENGTH_LONG).show();
            }
        } else {

            Future<List<Movie>> result = db.databaseWriteExecutor.submit(new Callable<List<Movie>>() {
                public List<Movie> call() throws Exception {
                    return mMoviesDao.getFavouriteMovies();
                }
            });

            try {
                List<Movie> favMovies = result.get();
                gridViewSetup(favMovies);
            } catch (Exception exception) {
               // Toast.makeText(getApplicationContext(), "Something", Toast.LENGTH_LONG).show();
            }
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

        gridViewSetup(moviesArray);
    }

    private void gridViewSetup(List<Movie> moviesArray) {
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
                i.putExtra("duration", selectedMovie.getDuration());
                i.putExtra("id", selectedMovie.getId());
                startActivity(i);

            }
        });
    }

    @Override
    public void onError() {
        Log.wtf("CACHO", "HTTP ERROR");
    }


    private Boolean checkForInternet() {
        ConnectivityManager mgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = mgr.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    enum Sorting {
        POPULAR,
        TOP_RATED,
        FAVOURITES
    }

}
