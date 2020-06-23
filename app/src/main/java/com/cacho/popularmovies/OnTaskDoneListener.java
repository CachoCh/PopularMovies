package com.cacho.popularmovies;

public interface OnTaskDoneListener {
    void onTaskDone(String responseData);

    void onError();
}
