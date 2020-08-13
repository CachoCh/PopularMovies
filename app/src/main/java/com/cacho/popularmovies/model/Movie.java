package com.cacho.popularmovies.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "movie_table")
public
class Movie {

    private String poster_path;
    private String title;
    private String overview;
    private String release_date;
    private String vote;
    private String duration;

    @PrimaryKey()
    private int id;


    public Movie(String title, String poster_path, String overview, String release_date, String vote, String duration, int id) {
        this.poster_path = poster_path;
        this.title = title;
        this.overview = overview;
        this.release_date = release_date;
        this.vote = vote;
        this.duration = duration;
        this.id = id;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public String getVote() {
        return vote;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDuration() {
        return duration;
    }
    public void setDuration(String duration) {
        this.duration = duration;
    }
}
