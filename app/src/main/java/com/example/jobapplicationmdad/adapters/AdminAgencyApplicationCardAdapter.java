package com.example.jobapplicationmdad.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.model.AgencyApplication;
import com.example.jobapplicationmdad.model.JobApplication;
import com.example.jobapplicationmdad.util.DateConverter;

import java.util.List;

public class AdminAgencyApplicationCardAdapter extends RecyclerView.Adapter<AdminAgencyApplicationCardAdapter.ViewHolder> {

    private List<AgencyApplication> agencyApplications;
    private final OnJobClickListener listener;


    // Interface for handling on click events
    public interface OnJobClickListener {
        void onViewApplication(String agencyApplicationId);

        void onAcceptAgencyApplication(String agencyApplicationId);

        void onRejectAgencyApplication(String agencyApplicationId);
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet  List<AgencyApplication> containing the list of Job applications
     * @param listener Defines the event to happen after clicking
     */
    public AdminAgencyApplicationCardAdapter(List<AgencyApplication> dataSet, OnJobClickListener listener) {
        agencyApplications = dataSet;
        this.listener = listener;
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvAgencyApplicationCardName;
        private final TextView tvAgencyApplicationCardEmail;
        private final TextView tvAgencyApplicationCardPhoneNumber;
        private final TextView tvAgencyApplicationCardAddress;
        private final Button btnAcceptAgencyApplication;
        private final Button btnRejectAgencyApplication;
        private final LinearLayout llAgencyApplicationCardStatusDetails;
        private final LinearLayout llAgencyApplicationCardButtons;
        private final TextView tvAgencyApplicationCardStatus;
        private final TextView tvAgencyApplicationCardUpdatedAt;
        private AgencyApplication currentAgencyApplication;

        public ViewHolder(View view, OnJobClickListener listener) {
            super(view);
            // Define click listener for the ViewHolder's View
            tvAgencyApplicationCardName = view.findViewById(R.id.tvAgencyApplicationCardName);
            tvAgencyApplicationCardEmail = view.findViewById(R.id.tvAgencyApplicationCardEmail);
            tvAgencyApplicationCardPhoneNumber = view.findViewById(R.id.tvAgencyApplicationCardPhoneNumber);
            tvAgencyApplicationCardAddress = view.findViewById(R.id.tvAgencyApplicationCardAddress);
            btnAcceptAgencyApplication = view.findViewById(R.id.btnAcceptAgencyApplication);
            btnRejectAgencyApplication = view.findViewById(R.id.btnRejectAgencyApplication);
            llAgencyApplicationCardStatusDetails = view.findViewById(R.id.llAgencyApplicationCardStatusDetails);
            llAgencyApplicationCardButtons = view.findViewById(R.id.llAgencyApplicationCardButtons);
            tvAgencyApplicationCardStatus = view.findViewById(R.id.tvAgencyApplicationCardStatus);
            tvAgencyApplicationCardUpdatedAt = view.findViewById(R.id.tvAgencyApplicationCardUpdatedAt);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null && currentAgencyApplication != null) {
                        listener.onViewApplication(currentAgencyApplication.getAgencyApplicationId());
                    }
                }
            });
            btnAcceptAgencyApplication.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null && currentAgencyApplication != null) {
                        listener.onAcceptAgencyApplication(currentAgencyApplication.getAgencyApplicationId());
                    }
                }
            });
            btnRejectAgencyApplication.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null && currentAgencyApplication != null) {
                        listener.onRejectAgencyApplication(currentAgencyApplication.getAgencyApplicationId());
                    }
                }
            });
        }

        public void bind(AgencyApplication agencyApplication) {
            currentAgencyApplication = agencyApplication;
            tvAgencyApplicationCardName.setText(agencyApplication.getName());
            tvAgencyApplicationCardEmail.setText(agencyApplication.getEmail());
            tvAgencyApplicationCardPhoneNumber.setText(agencyApplication.getPhoneNumber());
            tvAgencyApplicationCardAddress.setText(agencyApplication.getAddress());
            tvAgencyApplicationCardStatus.setText(agencyApplication.getStatus().toString());
            tvAgencyApplicationCardUpdatedAt.setText("Updated on "+DateConverter.formatDateFromSql(agencyApplication.getUpdatedAt()));
            if (agencyApplication.getStatus() == AgencyApplication.Status.ACCEPTED) {
                tvAgencyApplicationCardStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_18, 0, 0, 0);
            } else if (agencyApplication.getStatus() == AgencyApplication.Status.REJECTED) {
                tvAgencyApplicationCardStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cancel_18, 0, 0, 0);
            }
            if (agencyApplication.getStatus() == AgencyApplication.Status.PENDING) {
                llAgencyApplicationCardButtons.setVisibility(View.VISIBLE);
                llAgencyApplicationCardStatusDetails.setVisibility(View.GONE);
            }
            else{
                llAgencyApplicationCardStatusDetails.setVisibility(View.VISIBLE);
                llAgencyApplicationCardButtons.setVisibility(View.GONE);
            }

        }

    }


    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_admin_agency_application_card, viewGroup, false);
        return new ViewHolder(view, listener);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.bind(agencyApplications.get(position));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return agencyApplications.size();
    }
}
