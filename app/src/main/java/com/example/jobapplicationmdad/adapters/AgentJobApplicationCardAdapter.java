package com.example.jobapplicationmdad.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.model.Job;
import com.example.jobapplicationmdad.model.JobApplication;
import com.example.jobapplicationmdad.util.DateConverter;

import java.util.List;

public class AgentJobApplicationCardAdapter extends RecyclerView.Adapter<AgentJobApplicationCardAdapter.ViewHolder> {

    private List<JobApplication> jobApplications;
    private final OnJobClickListener listener;


    // Interface for handling on click events
    public interface OnJobClickListener {
        void onViewUser(String userId);
        void onAcceptJobApplication(String userId);
        void onRejectJobApplication(String userId);
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet  List<JobApplication> containing the list of Job applications
     * @param listener Defines the event to happen after clicking
     */
    public AgentJobApplicationCardAdapter(List<JobApplication> dataSet, OnJobClickListener listener) {
        jobApplications = dataSet;
        this.listener = listener;
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvJobApplicationCardUserName;
        private final TextView tvJobApplicationCardUserEmail;
        private final TextView tvJobApplicationCardUserPhoneNumber;
        private final Button btnAcceptJobApplication;
        private final Button btnRejectJobApplication;
        private JobApplication currentJobApplication;

        public ViewHolder(View view, OnJobClickListener listener) {
            super(view);
            // Define click listener for the ViewHolder's View
            tvJobApplicationCardUserName = view.findViewById(R.id.tvJobApplicationCardUserName);
            tvJobApplicationCardUserEmail = view.findViewById(R.id.tvJobApplicationCardUserEmail);
            tvJobApplicationCardUserPhoneNumber = view.findViewById(R.id.tvJobApplicationCardUserPhoneNumber);
            btnAcceptJobApplication = view.findViewById(R.id.btnAcceptJobApplication);
            btnRejectJobApplication = view.findViewById(R.id.btnRejectJobApplication);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null && currentJobApplication != null) {
                        listener.onViewUser(currentJobApplication.getUserId());
                    }
                }
            });
            btnAcceptJobApplication.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    if (listener != null && currentJobApplication != null) {
                        listener.onAcceptJobApplication(currentJobApplication.getUserId());
                    }
                }
            });
            btnRejectJobApplication.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    if (listener != null && currentJobApplication != null) {
                        listener.onRejectJobApplication(currentJobApplication.getUserId());
                    }
                }
            });
        }

        public void bind(JobApplication jobApplication) {
            currentJobApplication = jobApplication;
            tvJobApplicationCardUserName.setText(jobApplication.getUser().getFullName());
            tvJobApplicationCardUserEmail.setText(jobApplication.getUser().getEmail());
            tvJobApplicationCardUserPhoneNumber.setText(jobApplication.getUser().getPhoneNumber());
        }

    }


    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_agent_job_application_card, viewGroup, false);
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