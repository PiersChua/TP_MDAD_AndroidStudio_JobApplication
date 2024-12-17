package com.example.jobapplicationmdad.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.jobapplicationmdad.R;

import java.util.List;
import java.util.HashMap;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    private List<HashMap<String, String>> profileItems;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvLabel;
        private final TextView tvValue;

        /**
         * Holds references to the views within an item's layout
         * @param view
         */
        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            tvLabel = (TextView) view.findViewById(R.id.tvProfileLabel);
            tvValue = (TextView) view.findViewById(R.id.tvProfileValue);
        }

        public TextView getLabelTextView() {
            return tvLabel;
        }
        public TextView getValueTextView() {
            return tvValue;
        }
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet List<HashMap<String, String>> containing the data to populate views to be used
     * by RecyclerView
     */
    public ProfileAdapter(List<HashMap<String, String>> dataSet) {
        profileItems = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_profile, viewGroup, false);

        return new ViewHolder(view);
    }

    // Populate the ViewHolder with the data
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        HashMap<String, String> item = profileItems.get(position);
        String label = item.get("label");
        String value = item.get("value");
        viewHolder.getLabelTextView().setText(label);
        viewHolder.getValueTextView().setText(value);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return profileItems.size();
    }
}
