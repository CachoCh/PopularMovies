package com.cacho.popularmovies;

import android.graphics.Typeface;
import android.os.Bundle;

import com.cacho.popularmovies.recyclerview.ReviewsAdapter;
import com.cacho.popularmovies.recyclerview.TrailersAdapter;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.cacho.popularmovies.model.Movie;
import com.cacho.popularmovies.model.MovieDAO;
import com.cacho.popularmovies.model.MovieRoomDatabase;

import static com.cacho.popularmovies.JsonUtils.parseMovieRuntime;
import static com.cacho.popularmovies.JsonUtils.parseMovieTrailersKey;
import static com.cacho.popularmovies.JsonUtils.parseReviews;
import static com.cacho.popularmovies.MoviesGetter.POSTER_BASE_PATH;
import static com.cacho.popularmovies.MoviesGetter.REQUEST_MOVIE_AND_TRAILERS;
import static com.cacho.popularmovies.MoviesGetter.REQUEST_REVIEWS;

public class MovieDetails extends AppCompatActivity implements  OnTaskDoneListener{

    private RecyclerView mTrailersRecyclerView;
    private RecyclerView.Adapter mTrailersAdapter;
    private RecyclerView mReviewsRecyclerView;
    private RecyclerView.Adapter mReviewsAdapter;
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
                });
            } catch(Exception e){

            }
            new MoviesGetter(this, MOVIE_REPLY).execute(String.format(REQUEST_MOVIE_AND_TRAILERS, mCurrentMovie.getId()));
            new MoviesGetter(this, REVIEW_REPLY).execute(String.format(REQUEST_REVIEWS, mCurrentMovie.getId()));


        }

        mTrailersRecyclerView = (RecyclerView) findViewById(R.id.trailers_rv);
        mReviewsRecyclerView = (RecyclerView) findViewById(R.id.reviews_rv);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mTrailersRecyclerView.setHasFixedSize(true);
        mTrailersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mReviewsRecyclerView.setHasFixedSize(true);
        mReviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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
    public void onTaskDone(String responseData, String responseType) {

        switch (responseType){
            case MOVIE_REPLY:{
                try {
                    String duration = parseMovieRuntime(responseData);
                    mCurrentMovie.setDuration(duration);

                    List<String> trailerKeyString = parseMovieTrailersKey(responseData);
                    mTrailersAdapter = new TrailersAdapter(this, trailerKeyString); //
                    mTrailersRecyclerView.setAdapter(mTrailersAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ((TextView) findViewById(R.id.duration_tv)).setText(String.format("%s min",mCurrentMovie.getDuration()));
                break;
            }
            case REVIEW_REPLY:{ //parseReviews
                try {
                    List<String> trailerKeyString = parseReviews(responseData);
                    mReviewsAdapter = new ReviewsAdapter(this, trailerKeyString);
                    mReviewsRecyclerView.setAdapter(mReviewsAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }
        }


    }

    @Override
    public void onError() {

    }
}
