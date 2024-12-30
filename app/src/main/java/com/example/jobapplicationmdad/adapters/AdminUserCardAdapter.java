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

public class AdminUserCardAdapter extends RecyclerView.Adapter<AdminUserCardAdapter.ViewHolder> {

    private List<User> users;
    private final OnJobClickListener listener;


    // Interface for handling on click events
    public interface OnJobClickListener {

        void onEditUser(String userId);

    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet  List<User> containing the list of Job applications
     * @param listener Defines the event to happen after clicking
     */
    public AdminUserCardAdapter(List<User> dataSet, OnJobClickListener listener) {
        users = dataSet;
        this.listener = listener;
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvUserCardName;
        private final TextView tvUserCardEmail;
        private final TextView tvUserCardPhoneNumber;
        private final TextView tvUserCardRole;
        private final Button btnNavigateToEditUser;
        private User currentUser;

        public ViewHolder(View view, OnJobClickListener listener) {
            super(view);
            // Define click listener for the ViewHolder's View
            tvUserCardName = view.findViewById(R.id.tvUserCardName);
            tvUserCardEmail = view.findViewById(R.id.tvUserCardEmail);
            tvUserCardPhoneNumber = view.findViewById(R.id.tvUserCardPhoneNumber);
            tvUserCardRole = view.findViewById(R.id.tvUserCardRole);
            btnNavigateToEditUser = view.findViewById(R.id.btnNavigateToEditUser);
            btnNavigateToEditUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null && currentUser != null) {
                        listener.onEditUser(currentUser.getUserId());
                    }
                }
            });
        }

        public void bind(User user) {
            currentUser = user;
            tvUserCardName.setText(user.getFullName());
            tvUserCardEmail.setText(user.getEmail());
            tvUserCardPhoneNumber.setText(user.getPhoneNumber());
            tvUserCardRole.setText(user.getRole());
        }

    }


    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_admin_user_card, viewGroup, false);
        return new ViewHolder(view, listener);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.bind(users.get(position));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return users.size();
    }
}