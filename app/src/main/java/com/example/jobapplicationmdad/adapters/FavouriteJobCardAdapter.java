package com.example.jobapplicationmdad.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.model.Job;
import com.example.jobapplicationmdad.util.DateConverter;

import java.util.List;

public class FavouriteJobCardAdapter extends RecyclerView.Adapter<FavouriteJobCardAdapter.ViewHolder> {

    private List<Job> favouriteJobs;
    private final OnJobClickListener listener;

    // Interface for handling on click events
    public interface OnJobClickListener {
        void onViewJobDetails(String jobId);
        void onRemoveFavourite(String jobId,int position);
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet  List<Job> containing the list of favouriteJobs
     * @param listener Defines the event to happen after clicking
     */
    public FavouriteJobCardAdapter(List<Job> dataSet, OnJobClickListener listener) {
        favouriteJobs = dataSet;
        this.listener = listener;
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvFavouriteJobCardTitle;
        private final TextView tvFavouriteJobCardAgencyName;
        private final TextView tvFavouriteJobCardSalary;
        private final TextView tvFavouriteJobCardLocations;
        private final TextView tvFavouriteJobCardUpdatedAt;
        private final Button btnFavouriteJobFavourite;
        private Job currentJob;

        public ViewHolder(View view, OnJobClickListener listener) {
            super(view);
            // Define click listener for the ViewHolder's View
            tvFavouriteJobCardTitle = view.findViewById(R.id.tvFavouriteJobCardTitle);
            tvFavouriteJobCardAgencyName= view.findViewById(R.id.tvFavouriteJobCardAgencyName);
            tvFavouriteJobCardSalary = view.findViewById(R.id.tvFavouriteJobCardSalary);
            tvFavouriteJobCardLocations = view.findViewById(R.id.tvFavouriteJobCardLocations);
            tvFavouriteJobCardUpdatedAt = view.findViewById(R.id.tvFavouriteJobCardUpdatedAt);
            btnFavouriteJobFavourite = view.findViewById(R.id.btnFavouriteJob);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null && currentJob != null) {
                        listener.onViewJobDetails(currentJob.getJobId());
                    }
                }
            });
            btnFavouriteJobFavourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null && currentJob != null) {
                        listener.onRemoveFavourite(currentJob.getJobId(), getAdapterPosition());
                    }
                }
            });

        }

        public void bind(Job job) {
            currentJob = job;
            tvFavouriteJobCardTitle.setText(job.getPosition());
            tvFavouriteJobCardAgencyName.setText(job.getUser().getAgency().getName());
            StringBuilder salary = new StringBuilder();
            if (job.getPartTimeSalary() != 0.0) {
                salary.append("$").append(String.format("%.2f",job.getPartTimeSalary())).append(" per hr");
            }
            if (job.getFullTimeSalary() != 0.0) {
                if (salary.length() > 0) {
                    salary.append(" / ");
                }
                salary.append("$").append(String.format("%.2f",job.getFullTimeSalary())).append(" per mth");
            }
            if (salary.length() == 0) {
                tvFavouriteJobCardSalary.setVisibility(View.GONE);
            }
            else {
                tvFavouriteJobCardSalary.setText(salary);
            }
            tvFavouriteJobCardLocations.setText(job.getLocation());
            tvFavouriteJobCardUpdatedAt.setText(DateConverter.formatDate(DateConverter.convertDateTimeToDate(job.getUpdatedAt())));
        }

    }


    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_job_seeker_favourite_card, viewGroup, false);
        return new ViewHolder(view, listener);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.bind(favouriteJobs.get(position));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return favouriteJobs.size();
    }
}