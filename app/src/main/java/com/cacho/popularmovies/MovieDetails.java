package com.cacho.popularmovies;

import android.graphics.Typeface;
import android.os.Bundle;

import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import model.Movie;
import model.MovieDAO;
import model.MovieRoomDatabase;

import static com.cacho.popularmovies.MoviesGetter.POSTER_BASE_PATH;
import static com.cacho.popularmovies.MoviesGetter.REQUEST_MOVIE;

public class MovieDetails extends AppCompatActivity implements  OnTaskDoneListener{

    Movie mCurrentMovie;
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

            new MoviesGetter(this).execute(String.format(REQUEST_MOVIE, mCurrentMovie.getId()));
        }


    }

    public void saveFavourite(View v){
        //Toast.makeText(getApplicationContext(), "CLICK!", Toast.LENGTH_LONG).show();

        MovieRoomDatabase db = MovieRoomDatabase.getDatabase(this);
        MovieDAO mWordDao = db.movieDao();

        try {


            db.databaseWriteExecutor.execute(() -> {
                if (mWordDao.searchExistingMovie(mCurrentMovie.getTitle()) == 0) {
                    mWordDao.insert(mCurrentMovie); //TODO: make it more specific
                }

                String count = mWordDao.getCount();
                Log.wtf("CACHO", "count ->  " +  count);

            });


        } catch(Exception e){

        }

    }

    @Override
    public void onTaskDone(String responseData) {

        try {
            String duration = JsonUtils.parseMovieRuntime(responseData);
            mCurrentMovie.setDuration(duration);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ((TextView) findViewById(R.id.duration_tv)).setText(String.format("%s min",mCurrentMovie.getDuration()));
    }

    @Override
    public void onError() {

    }
}
