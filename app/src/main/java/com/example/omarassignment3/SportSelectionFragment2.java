package com.example.omarassignment3;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SportSelectionFragment2 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sport_selection_2, container, false);

        ImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        setupSportButton(view, R.id.footballButton, "Football");
        setupSportButton(view, R.id.basketballButton, "Basketball");
        setupSportButton(view, R.id.volleyballButton, "Volleyball");
        setupSportButton(view, R.id.tennisButton, "Tennis");
        setupSportButton(view, R.id.swimmingButton, "Swimming");

        return view;
    }

    private void setupSportButton(View view, int buttonId, String sport) {
        View button = view.findViewById(buttonId);
        button.setOnClickListener(v -> navigateToExpert(sport));
    }

    private void navigateToExpert(String sport) {
        ExpertFragment expertFragment = ExpertFragment.newInstance(sport);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, expertFragment)
                .addToBackStack(null)
                .commit();
    }
}
