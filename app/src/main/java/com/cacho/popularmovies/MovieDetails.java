package com.cacho.popularmovies;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import static com.cacho.popularmovies.MoviesGetter.POSTER_BASE_PATH;

public class MovieDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Bundle extras = getIntent().getExtras();
        if(extras !=null)
        {

            ((TextView) findViewById(R.id.title_tv)).setText(extras.getString("title"));
            ((TextView) findViewById(R.id.plot_tv)).setText(extras.getString("overview"));
            ((TextView) findViewById(R.id.date_tv)).setText(extras.getString("release_date"));
            ((TextView) findViewById(R.id.vote_tv)).setText(extras.getString("vote"));

            ImageView poster = findViewById(R.id.image_iv);
            String poster_path = String.format("%s%s", POSTER_BASE_PATH, extras.getString("poster_path"));
            Picasso.with(this).load(poster_path).into(poster);
        }


    }

}
