package com.example.jobapplicationmdad.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.model.Job;
import com.example.jobapplicationmdad.util.DateConverter;

import java.util.List;

public class HomeJobCardAdapter extends RecyclerView.Adapter<HomeJobCardAdapter.ViewHolder> {

    private List<Job> jobs;
    private final OnJobClickListener listener;

    // Interface for handling on click events
    public interface OnJobClickListener {
        void onViewJobDetails(String jobId);
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet  List<Job> containing the list of jobs
     * @param listener Defines the event to happen after clicking
     */
    public HomeJobCardAdapter(List<Job> dataSet, OnJobClickListener listener) {
        jobs = dataSet;
        this.listener = listener;
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvHomeJobCardTitle;
        private final TextView tvHomeJobCardDescription;
        private final TextView tvHomeJobCardSalary;
        private final TextView tvHomeJobCardLocations;
        private final TextView tvHomeJobCardUpdatedAt;
        private Job currentJob;

        public ViewHolder(View view, OnJobClickListener listener) {
            super(view);
            // Define click listener for the ViewHolder's View
            tvHomeJobCardTitle = view.findViewById(R.id.tvHomeJobCardTitle);
            tvHomeJobCardDescription = view.findViewById(R.id.tvHomeJobCardDescription);
            tvHomeJobCardSalary = view.findViewById(R.id.tvHomeJobCardSalary);
            tvHomeJobCardLocations = view.findViewById(R.id.tvHomeJobCardLocations);
            tvHomeJobCardUpdatedAt = view.findViewById(R.id.tvHomeJobCardUpdatedAt);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null && currentJob != null) {
                        listener.onViewJobDetails(currentJob.getJobId());
                    }
                }
            });
        }

        public void bind(Job job) {
            currentJob = job;
            tvHomeJobCardTitle.setText(job.getPosition());
            tvHomeJobCardDescription.setText(job.getResponsibilities());
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
                tvHomeJobCardSalary.setVisibility(View.GONE);
            }
            else {
                tvHomeJobCardSalary.setText(salary);
            }
            tvHomeJobCardLocations.setText(job.getLocation());
            tvHomeJobCardUpdatedAt.setText(DateConverter.formatDate(DateConverter.convertDateTimeToDate(job.getUpdatedAt())));
        }

    }


    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_job_seeker_home_job_card, viewGroup, false);
        return new ViewHolder(view, listener);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.bind(jobs.get(position));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return jobs.size();
    }
}