package com.cacho.popularmovies.recyclerview;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cacho.popularmovies.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.ViewHolder> {
    private List<String> mDataSet;
    private Context mContext;

    public TrailersAdapter(Context context, List<String> dataSet) {
        mContext = context;
        mDataSet = dataSet;
    }


    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public TrailersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailers_row_item, parent, false);

        return new ViewHolder(v, mContext);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull TrailersAdapter.ViewHolder viewHolder, int position) {
        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.setTrailerKey(mDataSet.get(position));
        viewHolder.getTextView().setText(String.format("Trailer %s", position + 1 ));
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }



    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private String mTrailerKey;

        public ViewHolder(View v, Context context) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    watchYoutubeVideo(context, mTrailerKey);

                }
            });
            textView = (TextView) v.findViewById(R.id.reviews_tv);
        }

        public void setTrailerKey(String key){
            mTrailerKey = key;
        }
        public TextView getTextView() {
            return textView;
        }
    }


    public static void watchYoutubeVideo(Context context, String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + id));

        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }
}
