package com.cacho.popularmovies;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MoviesGetter extends AsyncTask<String , Void ,String> {
    public static final String api_key = "";
    public final static String REQUEST_POPULAR_BASE = String.format("https://api.themoviedb.org/3/movie/popular?api_key=%s", api_key);
    public final static String REQUEST_TOP_RATED_BASE = String.format("https://api.themoviedb.org/3/movie/top_rated?api_key=%s", api_key);
    public final static String REQUEST_MOVIE_AND_TRAILERS = String.format("https://api.themoviedb.org/3/movie/%%s?api_key=%s&append_to_response=videos", api_key);
    public final static String REQUEST_REVIEWS = String.format("https://api.themoviedb.org/3/movie/%%s/reviews?api_key=%s", api_key);
    //public final static String IMAGE_QUALITY = "w185";
    public final static String POSTER_BASE_PATH = "https://image.tmdb.org/t/p/w185";

    private OnTaskDoneListener onTaskDoneListener;
    private String mRequestType;
    String server_response;


    public MoviesGetter(OnTaskDoneListener onTaskDoneListener, String requestType){
        this.onTaskDoneListener = onTaskDoneListener;
        mRequestType = requestType;
    }

    @Override
    protected String doInBackground(String... strings) {

        try {
            URL url = new URL(strings[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            int responseCode = urlConnection.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK) {
                server_response = readStream(urlConnection.getInputStream());
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return server_response;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (onTaskDoneListener != null && s != null) {
            onTaskDoneListener.onTaskDone(s, mRequestType);
        } else {
            onTaskDoneListener.onError();
        }
    }


    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

}
