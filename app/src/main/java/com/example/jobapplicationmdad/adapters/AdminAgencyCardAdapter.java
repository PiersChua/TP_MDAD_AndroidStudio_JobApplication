package com.example.jobapplicationmdad.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.model.Agency;
import com.example.jobapplicationmdad.model.User;

import java.util.List;

public class AdminAgencyCardAdapter extends RecyclerView.Adapter<AdminAgencyCardAdapter.ViewHolder> {

    private List<Agency> agencies;
    private final OnJobClickListener listener;


    // Interface for handling on click events
    public interface OnJobClickListener {
        void onManageAgency(String userId);

    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet  List<Agency> containing the list of agencies
     * @param listener Defines the event to happen after clicking
     */
    public AdminAgencyCardAdapter(List<Agency> dataSet, OnJobClickListener listener) {
        agencies = dataSet;
        this.listener = listener;
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvAgencyCardName;
        private final TextView tvAgencyCardEmail;
        private final TextView tvAgencyCardPhoneNumber;
        private final TextView tvAgencyCardUserName;
        private final TextView tvAgencyCardAgentCount;
        private final Button btnManageAgency;
        private Agency currentAgency;

        public ViewHolder(View view, OnJobClickListener listener) {
            super(view);
            // Define click listener for the ViewHolder's View
            tvAgencyCardName = view.findViewById(R.id.tvAgencyCardName);
            tvAgencyCardEmail = view.findViewById(R.id.tvAgencyCardEmail);
            tvAgencyCardPhoneNumber = view.findViewById(R.id.tvAgencyCardPhoneNumber);
            tvAgencyCardAgentCount = view.findViewById(R.id.tvAgencyCardAgentCount);
            tvAgencyCardUserName = view.findViewById(R.id.tvAgencyCardUserName);
            btnManageAgency = view.findViewById(R.id.btnManageAgency);
            btnManageAgency.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null && currentAgency != null) {
                        listener.onManageAgency(currentAgency.getUserId());
                    }
                }
            });
        }

        public void bind(Agency agency) {
            currentAgency = agency;
            tvAgencyCardName.setText(agency.getName());
            tvAgencyCardEmail.setText(agency.getEmail());
            tvAgencyCardPhoneNumber.setText(agency.getPhoneNumber());
            tvAgencyCardUserName.setText(agency.getUser().getFullName());
            tvAgencyCardAgentCount.setText(String.valueOf(agency.getAgentCount()));
        }

    }


    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_admin_agency_card, viewGroup, false);
        return new ViewHolder(view, listener);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.bind(agencies.get(position));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return agencies.size();
    }
}