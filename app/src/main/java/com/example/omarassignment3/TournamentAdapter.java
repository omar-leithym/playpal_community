package com.example.omarassignment3;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class TournamentAdapter extends RecyclerView.Adapter<TournamentAdapter.ViewHolder> {

    private Context context;
    private List<Tournament> tournamentList;
    private FragmentManager fragmentManager;
    private boolean isMyTournaments;

    public TournamentAdapter(Context context, List<Tournament> tournamentList, FragmentManager fragmentManager, boolean isMyTournaments) {
        this.context = context;
        this.tournamentList = tournamentList;
        this.fragmentManager = fragmentManager;
        this.isMyTournaments = isMyTournaments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tournament_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tournament tournament = tournamentList.get(position);

        holder.tournamentTitle.setText(tournament.getTournamentName());
        holder.tournamentTime.setText(tournament.getTimeLeft());


        if (isMyTournaments) {
            holder.signUpButton.setText("Withdraw");
            holder.tournamentDetails.setVisibility(View.GONE);

            holder.signUpButton.setOnClickListener(v -> {
                SharedPreferences sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                int userId = sharedPreferences.getInt("id", -1); // Assuming user_id is stored in shared preferences

                new WithdrawFromTournamentTask().execute(tournament.getTournamentID(), userId);

            });

        } else {
            holder.tournamentDetails.setText("Sport: " + tournament.getSportName() +
                    "\nPrize pool: " + tournament.getPrizePool() +
                    "\nRemaining Teams: " + tournament.getRemainingTeams() + "/" + tournament.getTotalTeams() +
                    "\nRequired PLayers Per Team: " + tournament.getTeamSize() +
                    "\nLocation: " + tournament.getLocation()

            );

            holder.signUpButton.setOnClickListener(v -> {
                int tournamentId = tournament.getTournamentID();
                TournamentSubmissionFragment fragment = TournamentSubmissionFragment.newInstance(tournamentId);

                fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, fragment)
                        .addToBackStack(null)
                        .commit();
            });
        }
    }

    @Override
    public int getItemCount() {
        return tournamentList.size();
    }

    private class WithdrawFromTournamentTask extends AsyncTask<Object, Void, String> {
        @Override
        protected String doInBackground(Object... params) {
            int tournamentId = (int) params[0];
            int userId = (int) params[1];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL("http://10.0.2.2:3001/tournament/withdraw");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                // Add authorization token if needed
                SharedPreferences sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                String token = sharedPreferences.getString("token", null);
                if (token != null) {
                    urlConnection.setRequestProperty("Authorization", "Bearer " + token);
                }

                // Set up the request body
                urlConnection.setDoOutput(true);
                String requestBody = "tournament_id=" + tournamentId + "&user_id=" + userId;
                try (OutputStream outputStream = urlConnection.getOutputStream()) {
                    outputStream.write(requestBody.getBytes());
                    outputStream.flush();
                }

                int responseCode = urlConnection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    InputStream errorStream = urlConnection.getErrorStream();
                    StringBuilder errorBuffer = new StringBuilder();
                    if (errorStream != null) {
                        reader = new BufferedReader(new InputStreamReader(errorStream));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            errorBuffer.append(line).append("\n");
                        }
                        reader.close();
                    }
                    return "Error: " + responseCode + "\n" + errorBuffer.toString();
                }

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                return buffer.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "Withdraw result: " + result);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tournamentTitle, tournamentTime, tournamentDetails;
        Button signUpButton;
        RelativeLayout tournamentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tournamentTitle = itemView.findViewById(R.id.tournament_title);
            tournamentTime = itemView.findViewById(R.id.tournament_time);
            tournamentDetails = itemView.findViewById(R.id.tournament_details);
            signUpButton = itemView.findViewById(R.id.sign_up_button);
            tournamentLayout = itemView.findViewById(R.id.tournament_layout);
        }
    }


}

