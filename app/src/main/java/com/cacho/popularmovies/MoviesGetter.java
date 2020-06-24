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
    public final static String REQUEST_POPULAR_BASE = String.format("http://api.themoviedb.org/3/movie/popular?api_key=%s", api_key);
    public final static String REQUEST_TOP_RATED_BASE = String.format("http://api.themoviedb.org/3/movie/top_rated?api_key=%s", api_key);
    public final static String IMAGE_QUALITY = "w185";
    public final static String POSTER_BASE_PATH = "http://image.tmdb.org/t/p/w185";

    private OnTaskDoneListener onTaskDoneListener;
    String server_response;

    public MoviesGetter(OnTaskDoneListener onTaskDoneListener){
        this.onTaskDoneListener = onTaskDoneListener;
    }

    @Override
    protected String doInBackground(String... strings) {

        try {
            URL url = new URL(strings[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            int responseCode = urlConnection.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK){
                server_response = readStream(urlConnection.getInputStream());
                Log.v("CatalogClient", server_response);
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
            onTaskDoneListener.onTaskDone(s);
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
