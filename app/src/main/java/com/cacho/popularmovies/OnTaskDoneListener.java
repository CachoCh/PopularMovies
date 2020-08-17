package com.cacho.popularmovies;

public interface OnTaskDoneListener {
    public static final String MOVIE_REPLY = "m1";
    public static final String REVIEW_REPLY = "r2";

    void onTaskDone(String responseData, String responseType);

    void onError();
}
