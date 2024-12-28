package com.example.jobapplicationmdad.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.model.Job;
import com.example.jobapplicationmdad.model.User;
import com.example.jobapplicationmdad.util.DateConverter;

import java.util.List;

public class AgencyAdminAgentCardAdapter extends RecyclerView.Adapter<AgencyAdminAgentCardAdapter.ViewHolder> {

    private List<User> agents;
    private final OnJobClickListener listener;


    // Interface for handling on click events
    public interface OnJobClickListener {
        void onManageAgent(String userId);

        void onEditAgent(String userId);

    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet  List<User> containing the list of Job applications
     * @param listener Defines the event to happen after clicking
     */
    public AgencyAdminAgentCardAdapter(List<User> dataSet, OnJobClickListener listener) {
        agents = dataSet;
        this.listener = listener;
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvAgentCardName;
        private final TextView tvAgentCardEmail;
        private final TextView tvAgentCardPhoneNumber;
        private final TextView tvAgentCardJobCount;
        private final Button btnManageAgent;
        private final Button btnNavigateToEditAgent;
        private User currentUser;

        public ViewHolder(View view, OnJobClickListener listener) {
            super(view);
            // Define click listener for the ViewHolder's View
            tvAgentCardName = view.findViewById(R.id.tvAgentCardName);
            tvAgentCardEmail = view.findViewById(R.id.tvAgentCardEmail);
            tvAgentCardPhoneNumber = view.findViewById(R.id.tvAgentCardPhoneNumber);
            tvAgentCardJobCount = view.findViewById(R.id.tvAgentCardJobCount);
            btnManageAgent = view.findViewById(R.id.btnManageAgent);
            btnNavigateToEditAgent = view.findViewById(R.id.btnNavigateToEditAgent);
            btnManageAgent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null && currentUser != null) {
                        listener.onManageAgent(currentUser.getUserId());
                    }
                }
            });
            btnNavigateToEditAgent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null && currentUser != null) {
                        listener.onEditAgent(currentUser.getUserId());
                    }
                }
            });
        }

        public void bind(User user) {
            currentUser = user;
            tvAgentCardName.setText(user.getFullName());
            tvAgentCardEmail.setText(user.getEmail());
            tvAgentCardPhoneNumber.setText(user.getPhoneNumber());
            tvAgentCardJobCount.setText(String.valueOf(user.getJobCount()));
        }

    }


    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_agency_admin_agent_card, viewGroup, false);
        return new ViewHolder(view, listener);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.bind(agents.get(position));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return agents.size();
    }
}