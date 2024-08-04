package com.example.omarassignment3;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

// Inside FriendListAdapter.java

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.FriendViewHolder> {

    private List<Friend> friends;
    private List<Friend> originalFriends; // Store original list for filtering
    private OnItemClickListener listener;

    public FriendListAdapter(List<Friend> friends, OnItemClickListener listener) {
        this.friends = friends;
        this.originalFriends = new ArrayList<>(friends); // Initialize original list
        this.listener = listener;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Friend friend = friends.get(position);
        holder.bind(friend, listener);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public void filterList(List<Friend> filteredList) {
        friends.clear();
        friends.addAll(filteredList);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(Friend friend);
    }

    static class FriendViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewName;
        private ImageView imageViewProfilePic;
        private TextView textViewLastMessage;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            imageViewProfilePic = itemView.findViewById(R.id.imageViewProfilePic);
            textViewLastMessage = itemView.findViewById(R.id.textViewLastMessage);
        }

        public void bind(final Friend friend, final OnItemClickListener listener) {
            textViewName.setText(friend.getName());

            if (textViewLastMessage != null) {
                textViewLastMessage.setText(friend.getLastMessage() != null ? friend.getLastMessage() : " ");
            }

            if (imageViewProfilePic != null) {
                if (friend.getProfilePicUrl() != null && !friend.getProfilePicUrl().isEmpty()) {
                    Log.d("FriendListAdapter", "Loading image for " + friend.getName() + ": " + friend.getProfilePicUrl());
                    Picasso.get()
                            .load(friend.getProfilePicUrl())
                            .placeholder(R.drawable.default_profile_pic) // Optionally add a placeholder
                            .error(R.drawable.default_profile_pic) // Handle error
                            .into(imageViewProfilePic, new Callback() {
                                @Override
                                public void onSuccess() {
                                    Log.d("Picasso", "Image loaded successfully for " + friend.getName());
                                }

                                @Override
                                public void onError(Exception e) {
                                    Log.e("Picasso", "Failed to load image for " + friend.getName() + ": " + e.getMessage());
                                    imageViewProfilePic.setImageResource(R.drawable.default_profile_pic);
                                }
                            });
                } else {
                    Log.d("FriendListAdapter", "No profile pic URL for " + friend.getName());
                    imageViewProfilePic.setImageResource(R.drawable.default_profile_pic);
                }
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(friend);
                }
            });
        }
    }
}
