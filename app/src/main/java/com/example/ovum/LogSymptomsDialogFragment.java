package com.example.ovum;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.example.ovum.DialogBuilder;

public class LogSymptomsDialogFragment extends DialogFragment {
    // Enum for symptoms
    public enum Symptom {
        HEAVY, MID_LIGHT, LIGHT, VERY_LIGHT, RED_SPOT, BROWN_SPOT,
        LIGHT_BROWN_SPOT, MOOD_SWINGS, IS_NERVOUS, IS_STRESSED,
        IS_ANGRY, IS_BLOATING, HAS_HEADACHE, HAS_HIGH_APPETITE,
        HAS_LOW_APPETITE, FEELS_SLEEPY, FEELS_TIRED, FEELS_STOMACH_PAIN
    }

    private Button confirmChangesBtn;
    private Set<Symptom> selectedSymptoms = new HashSet<>();
    private Map<Symptom, RelativeLayout> layoutMap = new HashMap<>();

    // for the title date
    private String titleDate;

    public LogSymptomsDialogFragment(String titleDate) {
        this.titleDate = titleDate;
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.log_symptoms, container, false);
        confirmChangesBtn = view.findViewById(R.id.confirmbtn);
        ImageView closeButton = view.findViewById(R.id.imageView);

        // Set OnTouchListener to dismiss the fragment
        closeButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                dismiss(); // Dismiss the dialog on touch release
            }
            return true; // Indicate that the touch event is consumed
        });

        // Initialize UI components
        initializeLayouts(view);
        setClickListeners();

        confirmChangesBtn.setOnClickListener(v -> showConfirmationDialog());
        return view;
    }

    private void initializeLayouts(View view) {
        layoutMap.put(Symptom.HEAVY, view.findViewById(R.id.forheavy));
        layoutMap.put(Symptom.MID_LIGHT, view.findViewById(R.id.formedium));
        layoutMap.put(Symptom.LIGHT, view.findViewById(R.id.forlow));
        layoutMap.put(Symptom.VERY_LIGHT, view.findViewById(R.id.forVerylight));
        layoutMap.put(Symptom.RED_SPOT, view.findViewById(R.id.forRedSpotting));
        layoutMap.put(Symptom.BROWN_SPOT, view.findViewById(R.id.forBrownSpotting));
        layoutMap.put(Symptom.LIGHT_BROWN_SPOT, view.findViewById(R.id.forLigtBrownSpotting));
        layoutMap.put(Symptom.MOOD_SWINGS, view.findViewById(R.id.forMood));
        layoutMap.put(Symptom.IS_NERVOUS, view.findViewById(R.id.fornervous));
        layoutMap.put(Symptom.IS_ANGRY, view.findViewById(R.id.forAnger));
        layoutMap.put(Symptom.IS_STRESSED, view.findViewById(R.id.forStressed));
        layoutMap.put(Symptom.IS_BLOATING, view.findViewById(R.id.forbloating));
        layoutMap.put(Symptom.HAS_HEADACHE, view.findViewById(R.id.forheadache));
        layoutMap.put(Symptom.HAS_HIGH_APPETITE, view.findViewById(R.id.forhighAppetite));
        layoutMap.put(Symptom.HAS_LOW_APPETITE, view.findViewById(R.id.forlowAppetite));
        layoutMap.put(Symptom.FEELS_SLEEPY, view.findViewById(R.id.forsleepy));
        layoutMap.put(Symptom.FEELS_TIRED, view.findViewById(R.id.forTiredness));
        layoutMap.put(Symptom.FEELS_STOMACH_PAIN, view.findViewById(R.id.forstomachPain));


    }
    // setting the click listeners
    private void setClickListeners() {
        for (Map.Entry<Symptom, RelativeLayout> entry : layoutMap.entrySet()) {
            entry.getValue().setOnClickListener(v -> handleSymptomClick(entry.getKey(), entry.getValue()));
        }
    }

    private void handleSymptomClick(Symptom symptom, RelativeLayout layout) {
        if (selectedSymptoms.contains(symptom)) {
            layout.setBackgroundResource(R.drawable.gradient_mixture);
            selectedSymptoms.remove(symptom);
        } else {
            layout.setBackgroundResource(R.drawable.circular_gradient);
            selectedSymptoms.add(symptom);
        }
        updateButtonState();
    }

    private void updateButtonState() {
        boolean hasSelectedSymptoms = !selectedSymptoms.isEmpty();
        confirmChangesBtn.setEnabled(hasSelectedSymptoms);
        confirmChangesBtn.setBackgroundColor(getResources().getColor(hasSelectedSymptoms ? R.color.hot_pink : R.color.pale_pink));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showConfirmationDialog() {
        new DialogBuilder(getContext())
            .setTitle("Confirm Symptoms")
            .setMessage("Are you sure you want to save these symptoms?")
            .setPositiveButton("Yes", (dialog, which) -> logSymptoms())
            .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
            .show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void logSymptoms() {
        LocalDate fromTitleDate = new DateUtils().formatDateToLocalDate(titleDate);
        SymptomLogger symptomLogger = new SymptomLogger(getContext());
        symptomLogger.logSymptoms(selectedSymptoms, fromTitleDate);
        dismiss();
    }
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext(), R.style.CustomDialog);
        dialog.setContentView(R.layout.log_symptoms);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        return dialog;
    }
}
