package com.cacho.popularmovies;

class Movie {

    private String poster_path;
    private String title;
    private String overview;
    private String release_date;
    private String vote;

    public Movie(String title, String poster_path, String overview, String release_date, String vote) {
        this.poster_path = poster_path;
        this.title = title;
        this.overview = overview;
        this.release_date = release_date;
        this.vote = vote;
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

}
