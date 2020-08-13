package com.cacho.popularmovies.livedata;

import android.app.Application;
import com.cacho.popularmovies.model.*;
import androidx.lifecycle.LiveData;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;


public class MovieViewModel extends AndroidViewModel {
    private FavMovieRepository mRepository;
    private LiveData<List<Movie>> mAllMovies;

    public MovieViewModel(@NonNull Application application) {
        super(application);
        mRepository = new FavMovieRepository(application);
        mAllMovies = mRepository.getmAllMovies();

    }

    public LiveData<List<Movie>> getAllMovies(){
        return mAllMovies;
    }

    public void insert(Movie movie){ mRepository.insert(movie);}
}
