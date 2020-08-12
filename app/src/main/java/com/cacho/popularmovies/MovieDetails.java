package com.cacho.popularmovies;

import android.graphics.Typeface;
import android.os.Bundle;

import com.cacho.popularmovies.recyclerview.TrailersAdapter;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import model.Movie;
import model.MovieDAO;
import model.MovieRoomDatabase;

import static com.cacho.popularmovies.JsonUtils.parseMovieRuntime;
import static com.cacho.popularmovies.JsonUtils.parseMovieTrailersKey;
import static com.cacho.popularmovies.MoviesGetter.POSTER_BASE_PATH;
import static com.cacho.popularmovies.MoviesGetter.REQUEST_MOVIE_AND_TRAILERS;

public class MovieDetails extends AppCompatActivity implements  OnTaskDoneListener{

    final static String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    Movie mCurrentMovie;
    private MovieRoomDatabase mDB;
    private MovieDAO mWordDao;
    private Button mFavouriteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Bundle extras = getIntent().getExtras();
        if(extras !=null)
        {

            mCurrentMovie = new Movie(extras.getString("title"),
                    extras.getString("poster_path"),
                    extras.getString("overview"),
                    extras.getString("release_date"),
                    extras.getString("vote"),
                    extras.getString("duration"),
                    extras.getInt("id"));

            ((TextView) findViewById(R.id.title_tv)).setText(extras.getString("title"));
            ImageView poster = findViewById(R.id.image_iv);
            String poster_path = String.format("%s%s", POSTER_BASE_PATH, extras.getString("poster_path"));
            Picasso.with(this).load(poster_path).into(poster);

            ((TextView) findViewById(R.id.plot_tv)).setText(extras.getString("overview"));
            String releaseDate = extras.getString("release_date");
            ((TextView) findViewById(R.id.date_tv)).setText(releaseDate.substring(0,4));
            ((TextView) findViewById(R.id.duration_tv)).setText(String.format("%s min",mCurrentMovie.getDuration()));
            ((TextView) findViewById(R.id.vote_tv)).setText(String.format("%s/10",extras.getString("vote")));
            ((TextView) findViewById(R.id.vote_tv)).setTypeface(null, Typeface.BOLD);
            mFavouriteButton = findViewById(R.id.favourite_bt);

            //favourite_bt
            mDB = MovieRoomDatabase.getDatabase(this);
            mWordDao = mDB.movieDao();
            try {
                mDB.databaseWriteExecutor.execute(() -> {
                    if (mWordDao.searchExistingMovie(String.valueOf(mCurrentMovie.getId())) == mCurrentMovie.getId()) {
                        setButton(mFavouriteButton, true);
                    } else {
                        setButton(mFavouriteButton, false);
                    }

                    String count = mWordDao.getCount();
                    Log.wtf("CACHO", "count ->  " +  count);

                });


            } catch(Exception e){

            }
            new MoviesGetter(this).execute(String.format(REQUEST_MOVIE_AND_TRAILERS, mCurrentMovie.getId()));
        }

        recyclerView = (RecyclerView) findViewById(R.id.trailers_rv);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void setButton(Button button, boolean onoff){
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                button.setPressed(onoff);
            }
        }, 100);
    }

    public void saveFavourite(View v){
        mDB = MovieRoomDatabase.getDatabase(this);
        mWordDao = mDB.movieDao();

        try {
            mDB.databaseWriteExecutor.execute(() -> {
                if (mWordDao.searchExistingMovie(String.valueOf(mCurrentMovie.getId())) != mCurrentMovie.getId()) {
                    setButton(mFavouriteButton, true);
                    mWordDao.insert(mCurrentMovie);
                } else {
                    setButton(mFavouriteButton, false);
                    mWordDao.deleteMovie(String.valueOf(mCurrentMovie.getId()));
                }
            });
        } catch(Exception e){

        }

    }

    @Override
    public void onTaskDone(String responseData) {

        try {
            String duration = parseMovieRuntime(responseData);
            mCurrentMovie.setDuration(duration);

            List<String> trailerKeyString = parseMovieTrailersKey(responseData);
            mAdapter = new TrailersAdapter(this, trailerKeyString); //
            recyclerView.setAdapter(mAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ((TextView) findViewById(R.id.duration_tv)).setText(String.format("%s min",mCurrentMovie.getDuration()));
    }

    @Override
    public void onError() {

    }
}
