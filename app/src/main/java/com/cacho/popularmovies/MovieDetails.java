package com.cacho.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import model.Movie;
import model.MovieDAO;
import model.MovieRoomDatabase;

import static com.cacho.popularmovies.MoviesGetter.POSTER_BASE_PATH;

public class MovieDetails extends AppCompatActivity {

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
                    extras.getString("vote"));

            ((TextView) findViewById(R.id.title_tv)).setText(extras.getString("title"));
            ((TextView) findViewById(R.id.plot_tv)).setText(extras.getString("overview"));
            ((TextView) findViewById(R.id.date_tv)).setText(extras.getString("release_date"));
            ((RatingBar) findViewById(R.id.vote_tv)).setNumStars(10);
            ((RatingBar) findViewById(R.id.vote_tv)).setRating(Float.valueOf(extras.getString("vote")));

            ImageView poster = findViewById(R.id.image_iv);
            String poster_path = String.format("%s%s", POSTER_BASE_PATH, extras.getString("poster_path"));
            Picasso.with(this).load(poster_path).into(poster);
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

}
