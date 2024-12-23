package com.example.jobapplicationmdad.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.model.Job;
import com.example.jobapplicationmdad.model.JobApplication;
import com.example.jobapplicationmdad.util.DateConverter;

import java.util.List;

public class JobApplicationCardAdapter extends RecyclerView.Adapter<JobApplicationCardAdapter.ViewHolder> {

    private List<JobApplication> jobApplications;
    private final OnJobClickListener listener;

    // Interface for handling on click events
    public interface OnJobClickListener {
        void onViewJobDetails(String jobId);
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet  List<Job> containing the list of jobApplications
     * @param listener Defines the event to happen after clicking
     */
    public JobApplicationCardAdapter(List<JobApplication> dataSet, OnJobClickListener listener) {
        jobApplications = dataSet;
        this.listener = listener;
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvJobApplicationCardTitle;
        private final TextView tvJobApplicationCardAgencyName;
        private final TextView tvJobApplicationCardSalary;
        private final TextView tvJobApplicationCardLocations;
        private final TextView tvJobApplicationCardStatus;
        private final TextView tvJobApplicationCardCreatedAt;
        private JobApplication currentJobApplication;

        public ViewHolder(View view, OnJobClickListener listener) {
            super(view);
            // Define click listener for the ViewHolder's View
            tvJobApplicationCardTitle = view.findViewById(R.id.tvJobApplicationCardTitle);
            tvJobApplicationCardAgencyName = view.findViewById(R.id.tvJobApplicationCardAgencyName);
            tvJobApplicationCardSalary = view.findViewById(R.id.tvJobApplicationCardSalary);
            tvJobApplicationCardLocations = view.findViewById(R.id.tvJobApplicationCardLocations);
            tvJobApplicationCardStatus = view.findViewById(R.id.tvJobApplicationCardStatus);
            tvJobApplicationCardCreatedAt = view.findViewById(R.id.tvJobApplicationCardCreatedAt);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null && currentJobApplication != null) {
                        listener.onViewJobDetails(currentJobApplication.getJob().getJobId());
                    }
                }
            });

        }

        public void bind(JobApplication jobApplication) {
            currentJobApplication = jobApplication;
            tvJobApplicationCardTitle.setText(jobApplication.getJob().getPosition());
            tvJobApplicationCardAgencyName.setText(jobApplication.getJob().getUser().getAgency().getName());
            StringBuilder salary = new StringBuilder();
            if (jobApplication.getJob().getPartTimeSalary() != 0.0) {
                salary.append("$").append(String.format("%.2f", jobApplication.getJob().getPartTimeSalary())).append(" per hr");
            }
            if (jobApplication.getJob().getFullTimeSalary() != 0.0) {
                if (salary.length() > 0) {
                    salary.append(" / ");
                }
                salary.append("$").append(String.format("%.2f", jobApplication.getJob().getFullTimeSalary())).append(" per mth");
            }
            if (salary.length() == 0) {
                tvJobApplicationCardSalary.setVisibility(View.GONE);
            } else {
                tvJobApplicationCardSalary.setText(salary);
            }
            tvJobApplicationCardLocations.setText(jobApplication.getJob().getLocation());
            tvJobApplicationCardStatus.setText(jobApplication.getStatus().toString());
            tvJobApplicationCardCreatedAt.setText(DateConverter.formatDate(DateConverter.convertDateTimeToDate(jobApplication.getCreatedAt())));
        }

    }


    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_job_seeker_job_application_card, viewGroup, false);
        return new ViewHolder(view, listener);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.bind(jobApplications.get(position));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return jobApplications.size();
    }
}