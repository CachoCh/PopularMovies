package model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MovieDAO {

    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Movie movie);

    @Query("DELETE FROM movie_table")
    void deleteAll();

    @Query("DELETE FROM movie_table WHERE id LIKE :movieId")
    void deleteMovie(String movieId);

    @Query("SELECT * from movie_table")
    List<Movie> getFavouriteMovies();

    @Query("SELECT COUNT(id) FROM movie_table")
    String getCount();

    @Query("SELECT id FROM movie_table  WHERE id LIKE :movieId")
    int searchExistingMovie(String movieId);
}

