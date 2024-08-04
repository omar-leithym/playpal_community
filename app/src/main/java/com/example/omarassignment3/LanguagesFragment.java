package com.example.omarassignment3;



import android.content.Context;  // Ensure this import is present
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LanguagesFragment extends Fragment {

    private OnLanguageSelectedListener callback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_languages, container, false);

        view.findViewById(R.id.englishText).setOnClickListener(v -> selectLanguage("English"));
        view.findViewById(R.id.frenchText).setOnClickListener(v -> selectLanguage("French"));
        view.findViewById(R.id.arabicText).setOnClickListener(v -> selectLanguage("Arabic"));

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnLanguageSelectedListener) {
            callback = (OnLanguageSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnLanguageSelectedListener");
        }
    }

    private void selectLanguage(String language) {
        Bundle result = new Bundle();
        result.putString("selectedLanguage", language);
        getParentFragmentManager().setFragmentResult("requestKey", result);

        if (callback != null) {
            callback.onLanguageSelected(language);
        }

        // Navigate back to the previous fragment
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    public interface OnLanguageSelectedListener {
        void onLanguageSelected(String language);
    }
}
