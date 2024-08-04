package com.example.omarassignment3;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.io.OutputStream;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {

    private Context context;
    private List<Player> playerList;
    private FragmentManager fragmentManager;
    public PlayerAdapter(Context context, List<Player> playerList, FragmentManager fragmentManager) {
        this.context = context;
        this.playerList = playerList;
        this.fragmentManager= fragmentManager;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_player, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        Player player = playerList.get(position);
        holder.textViewName.setText(player.getFirstName() + " " + player.getLastName());
        holder.textViewLevel.setText(player.getLevel());
        Glide.with(context)
                .load(player.getImageUrl())
                .into(holder.imageView);

        Log.d(TAG, "Profile image URL: " + player.getImageUrl());

        holder.buttonInfo.setOnClickListener(v -> {
            String email = player.getEmail();
            Intent intent = new Intent(context, profilepageotherperson.class);
            intent.putExtra("email", email);
            context.startActivity(intent);
            //profilepage fragment = new profilepage();
            //Bundle args = new Bundle();
            //args.putString("email", email);
            //fragment.setArguments(args);


            //fragmentManager.beginTransaction()
            //        .replace(R.id.frame_layout, fragment)
            //        .addToBackStack(null)
            //        .commit();

        });

        holder.buttonMatch.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            int currentUserId = sharedPreferences.getInt("id", 1);
            int targetUserId = player.getID();
            new SendMatchRequestTask().execute(currentUserId, targetUserId);
        });
    }


    @Override
    public int getItemCount() {
        return playerList.size();
    }

    private class SendMatchRequestTask extends AsyncTask<Integer, Void, String> {
        private static final String URL_STRING = "http://10.0.2.2:3001/send-match-request"; // Replace with your server URL

        @Override
        protected String doInBackground(Integer... params) {
            int requesterId = params[0];
            int requestedId = params[1];

            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(URL_STRING);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                urlConnection.setDoOutput(true);

                String jsonInputString = String.format("{\"id\": %d, \"targetID\": %d}", requesterId, requestedId);

                try (OutputStream os = urlConnection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_CREATED) {
                    return "Match request sent successfully";
                } else {
                    return "Error sending match request";
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "Match request failed: " + e.getMessage();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            new Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
            );
        }
    }


    public class PlayerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewName;
        TextView textViewLevel;
        Button buttonInfo;
        Button buttonMatch;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewLevel = itemView.findViewById(R.id.textViewLevel);
            buttonInfo = itemView.findViewById(R.id.buttonInfo);
            buttonMatch = itemView.findViewById(R.id.buttonMatch);
        }
    }

}


