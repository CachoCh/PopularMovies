package com.cacho.popularmovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import model.Movie;

public final class JsonUtils {
    public static List<Movie> parseMoviesResult(String json) throws JSONException {
        List<Movie> moviesList = new ArrayList<>();
        JSONObject jo = new JSONObject(json);

        JSONArray ja = jo.getJSONArray("results");
        for(int i = 0; i < ja.length(); i++) {

            JSONObject movieJO = new JSONObject(ja.getString(i));
            Movie movie = new Movie(movieJO.getString("title"),
                    movieJO.getString("poster_path"),
                    movieJO.getString("overview"),
                    movieJO.getString("release_date"),
                    movieJO.getString("vote_average"),
                    "",
                    movieJO.getInt("id"));
            moviesList.add(movie);
        }

        return moviesList;
    }

    public static String parseMovieRuntime(String json) throws JSONException {
        JSONObject jo = new JSONObject(json);
        String runtime = jo.getString("runtime");

        return runtime;
    }

    public static List<String> parseMovieTrailersKey(String json) throws JSONException {
        JSONArray ja =  new JSONObject(json).getJSONObject("videos").getJSONArray("results");
        List<String> trailerKeyString = new ArrayList<>();

        for (int i = 0; i < ja.length(); i++) {
            JSONObject jo = ja.getJSONObject(i);
            trailerKeyString.add(jo.getString("key"));
        }
        return trailerKeyString;
    }

}
