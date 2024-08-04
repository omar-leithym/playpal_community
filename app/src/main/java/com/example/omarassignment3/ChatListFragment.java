package com.example.omarassignment3;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatListFragment extends Fragment {

    private RecyclerView recyclerViewFriends;
    private FriendListAdapter friendListAdapter;
    private List<Friend> friends;
    private SearchView searchViewFriends;
    private List<Friend> originalFriends; // Store original list for filtering

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        ((Nav) requireActivity()).showNavigationBar();
        recyclerViewFriends = view.findViewById(R.id.recyclerViewFriends);
        recyclerViewFriends.setLayoutManager(new LinearLayoutManager(getContext()));

        friends = new ArrayList<>();
        originalFriends = new ArrayList<>(); // Initialize original list
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String myEmail = sharedPreferences.getString("email", "User");
        friendListAdapter = new FriendListAdapter(friends, new FriendListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Friend friend) {
                // Open ChatFragment with this friend
                Fragment chatFragment = new ChatFragment();
                Bundle bundle = new Bundle();
                bundle.putString("user1_email", myEmail); // Replace with actual logged in user's email
                bundle.putString("user2_email", friend.getEmail());
                chatFragment.setArguments(bundle);

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, chatFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        recyclerViewFriends.setAdapter(friendListAdapter);

        fetchFriends(myEmail);

        // Setup SearchView
        searchViewFriends = view.findViewById(R.id.searchViewFriends);
        searchViewFriends.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        return view;
    }

    private void fetchFriends(String myEmail) {
        String url = "http://10.0.2.2:3001/api/friends?userEmail=" + myEmail;

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            friends.clear();
                            originalFriends.clear(); // Clear original list before adding new data
                            Log.d("fetchFriends", "JSON Response: " + response.toString()); // Log the entire JSON response for debugging
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject friendJson = response.getJSONObject(i);

                                String email = friendJson.getString("email");
                                String name = friendJson.getString("name");
                                String profilePicUrl = friendJson.optString("profilePicUrl", ""); // Handle null or missing profilePicUrl
                                String lastMessage = friendJson.optString("last_message", "");

                                Friend friend = new Friend(email, name, profilePicUrl, lastMessage);
                                friends.add(friend);
                                originalFriends.add(friend); // Add to original list as well
                            }
                            friendListAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("fetchFriends", "Error parsing JSON: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.e("fetchFriends", "Volley Error: " + error.getMessage());
                    }
                });

        queue.add(request);
    }

    private void filter(String searchText) {
        // Filter the list based on searchText
        List<Friend> filteredList = new ArrayList<>();
        for (Friend friend : originalFriends) {
            if (friend.getName().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(friend);
            }
        }
        friends.clear();
        friends.addAll(filteredList);
        friendListAdapter.notifyDataSetChanged();
    }
}
