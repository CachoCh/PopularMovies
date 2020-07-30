package com.cacho.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import java.util.List;

import model.Movie;

import static com.cacho.popularmovies.MoviesGetter.POSTER_BASE_PATH;

public class MoviesAdapter extends BaseAdapter {

    private Context mContext;
    private final List<Movie> movies;

    public MoviesAdapter(Context mContext, List<Movie> movies) {
        this.mContext = mContext;
        this.movies = movies;
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Object getItem(int i) {
        return movies.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //http://image.tmdb.org/t/p/w185/2CAL2433ZeIihfX1Hb2139CX0pW.jpg
        ImageView dummyImageView = new ImageView(mContext);
        String poster_path = String.format("%s%s", POSTER_BASE_PATH, movies.get(i).getPoster_path());
        Picasso.with(mContext).load(poster_path).into(dummyImageView);
        return dummyImageView;
    }
}
