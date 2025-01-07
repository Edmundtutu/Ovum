package com.pac.ovum.ui.dialogs;

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

import com.pac.ovum.R;
import com.pac.ovum.utils.data.GynDataLogger;
import com.pac.ovum.utils.data.calendarutils.DateUtils;
import com.pac.ovum.utils.ui.DialogBuilder;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SetGynEventFragment extends DialogFragment {
    // Enum for events
    public enum GynEvent {
        MEET_DOC, TALK_TO_DOC, LOG_ASYMPTOM, INVOLVE_IN_SEX, TAKE_A_PILL,
        MEET_YOUR_DOC, CALL_DOC, PRESCRIBED
    }

    private Button confirmChangesBtn;
    private Set<GynEvent> selectedEvents = new HashSet<>();
    private Map<GynEvent, RelativeLayout> layoutMap = new HashMap<>();
    private String titleDate;

    public SetGynEventFragment(String titleDate) {
        this.titleDate = titleDate;
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.set_gyn_event, container, false);
        // set an onTouch Listener on the shade view
        ImageView closeButton = view.findViewById(R.id.shade_view);

        // Set OnTouchListener to dismiss the fragment
        closeButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                dismiss(); // Dismiss the dialog on touch release
            }
            return true; // Indicate that the touch event is consumed
        });
        confirmChangesBtn = view.findViewById(R.id.confirmBtnForEvents);
        initializeLayouts(view);
        setClickListeners();

        confirmChangesBtn.setOnClickListener(v -> showConfirmationDialog());
        return view;
    }

    private void initializeLayouts(View view) {
        layoutMap.put(GynEvent.MEET_DOC, view.findViewById(R.id.forVisit));
        layoutMap.put(GynEvent.TALK_TO_DOC, view.findViewById(R.id.forTalkToDoc));
        layoutMap.put(GynEvent.LOG_ASYMPTOM, view.findViewById(R.id.forMakeLog));
        layoutMap.put(GynEvent.INVOLVE_IN_SEX, view.findViewById(R.id.forSexIntercource));
        layoutMap.put(GynEvent.TAKE_A_PILL, view.findViewById(R.id.forTakePill));
        layoutMap.put(GynEvent.MEET_YOUR_DOC, view.findViewById(R.id.forMeetUp));
        layoutMap.put(GynEvent.CALL_DOC, view.findViewById(R.id.forcalling));
        layoutMap.put(GynEvent.PRESCRIBED, view.findViewById(R.id.forPrescribed));
    }

    private void setClickListeners() {
        for (Map.Entry<GynEvent, RelativeLayout> entry : layoutMap.entrySet()) {
            entry.getValue().setOnClickListener(v -> handleEventClick(entry.getKey(), entry.getValue()));
        }
    }

    private void handleEventClick(GynEvent event, RelativeLayout layout) {
        if (selectedEvents.contains(event)) {
            layout.setBackgroundResource(R.drawable.gradient_mixture);
            selectedEvents.remove(event);
        } else {
            layout.setBackgroundResource(R.drawable.circular_gradient);
            selectedEvents.add(event);
        }
        updateButtonState();
    }

    private void updateButtonState() {
        boolean hasSelectedEvents = !selectedEvents.isEmpty();
        confirmChangesBtn.setEnabled(hasSelectedEvents);
        confirmChangesBtn.setBackgroundColor(getResources().getColor(hasSelectedEvents ? R.color.hot_pink : R.color.pale_pink));
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showConfirmationDialog() {
        new DialogBuilder(getContext())
            .setTitle("Confirm Data")
            .setMessage("Are you sure you want to save this gynecological data?")
            .setPositiveButton("Yes", (dialog, which) -> logGynData())
            .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
            .show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void logGynData() {
        LocalDate fromTitleDate = new DateUtils().formatDateToLocalDate(titleDate);
        GynDataLogger gynDataLogger = new GynDataLogger(getContext());
        gynDataLogger.logGynData(selectedEvents, fromTitleDate);
        dismiss();
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext(), R.style.CustomDialog);
        dialog.setContentView(R.layout.set_gyn_event);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return dialog;
    }
}
