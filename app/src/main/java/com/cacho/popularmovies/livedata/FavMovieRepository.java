package com.cacho.popularmovies.livedata;

import android.app.Application;
import com.cacho.popularmovies.model.*;

import java.util.List;

import androidx.lifecycle.LiveData;

class FavMovieRepository {

    private MovieDAO mMovieDao;
    private LiveData<List<Movie>> mAllMovies;

    FavMovieRepository(Application application) {
        MovieRoomDatabase db = MovieRoomDatabase.getDatabase(application);
        mMovieDao = db.movieDao();
        mAllMovies = mMovieDao.getFavouriteMovies();
    }

    LiveData<List<Movie>> getmAllMovies() {return mAllMovies;}

    void insert(Movie movie) {
        MovieRoomDatabase.databaseWriteExecutor.execute(() -> {
            mMovieDao.insert(movie);
        });
    }
}
