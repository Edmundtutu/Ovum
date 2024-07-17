package com.example.ovum;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class SetGynEventFragment extends DialogFragment {
    // declaring the useful
    Button confirmChangesBtn;
    public Boolean MeetDoc = false;
    public Boolean TalkToDoc = false;
    public Boolean LogASymptom = false;
    public Boolean InvolveInSex = false;
    public Boolean TakeAPill = false;
    public Boolean MeetYourDoc = false;
    public Boolean CallDoc = false;
    public Boolean Prescribed = false;
    private int count = 0;
    private Set<RelativeLayout> selectedLayouts = new HashSet<RelativeLayout>();

    private String titleDate;

    public SetGynEventFragment(String titleDate) {
        this.titleDate = titleDate;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.set_gyn_event, container, false);

        // initialize the title textview
        TextView title = view.findViewById(R.id.heading_text_view);
        title.setText(titleDate);
        // getting the views from the layout
        confirmChangesBtn =view.findViewById(R.id.confirmBtnForEvents);
        RelativeLayout meetDoc =view.findViewById(R.id.forVisit);
        RelativeLayout talkToDoc =view.findViewById(R.id.forTalkToDoc);
        RelativeLayout logASymptom =view.findViewById(R.id.forMakeLog);
        RelativeLayout involveInSex =view.findViewById(R.id.forSexIntercource);
        RelativeLayout takeAPill =view.findViewById(R.id.forTakePill);
        RelativeLayout meetYourDoc = view.findViewById(R.id.forMeetUp);
        RelativeLayout callDoc =view.findViewById(R.id.forcalling);
        RelativeLayout prescribed = view.findViewById(R.id.forPrescribed);

        // setting the listeners for the layouts
        meetDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeetDoc = toggleBackgroundTry(meetDoc);
            }
        });
        talkToDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TalkToDoc = toggleBackgroundTry(talkToDoc);
            }
        });
        logASymptom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogASymptom = toggleBackgroundTry(logASymptom);
            }
        });
        involveInSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InvolveInSex = toggleBackgroundTry(involveInSex);
            }
        });
        takeAPill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TakeAPill = toggleBackgroundTry(takeAPill);
            }
        });
        meetYourDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeetYourDoc = toggleBackgroundTry(meetYourDoc);
            }
        });
        callDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallDoc = toggleBackgroundTry(callDoc);
            }
        });
        prescribed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Prescribed = toggleBackgroundTry(prescribed);
            }
        });

        // setting the onclick for the confrim button!
        confirmChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEvents();
            }
        });
        return view;
    }

    private Boolean toggleBackgroundTry(RelativeLayout layout) {
        boolean isSelected = selectedLayouts.contains(layout);

        boolean state;
        if (!isSelected) {
            layout.setBackgroundResource(R.drawable.circular_gradient);
            selectedLayouts.add(layout);
            count++;
            state = true;
        } else {
            layout.setBackgroundResource(R.drawable.gradient_mixture);
            selectedLayouts.remove(layout);
            count--;
            state = false;
        }

        updateButtonState();

        return state;
    }

    // Method to update the button state based on the selection count
    private void updateButtonState() {
        if (count > 0) {
            confirmChangesBtn.setEnabled(true);
            confirmChangesBtn.setBackgroundColor(getResources().getColor(R.color.hot_pink));
        } else {
            confirmChangesBtn.setEnabled(false);
            confirmChangesBtn.setBackgroundColor(getResources().getColor(R.color.pale_pink));
        }
    }

    private void addEvents(){
        // if any of the values are true then add the event
        if(MeetDoc||TalkToDoc||LogASymptom||InvolveInSex||TakeAPill||MeetYourDoc||CallDoc||Prescribed){
            // create an Arraylist to store the events selected for the particular day
            List<String> events = new ArrayList<>();
            if(MeetDoc){
                // add the string Meet the Doctor to the list of events
                events.add("Meet the Doctor");
            }
            if(TalkToDoc){
                // add the string Talk to the Doctor to the list of events
                events.add("Talk to the Doctor");
            }
            if(LogASymptom){
                // add the string Log a Symptom to the list of events
                events.add("Log a Symptom");
            }
            if(InvolveInSex) {
                // add the string will involve in sex to the list of events
                events.add("Will involve in sex");
            }
            if(TakeAPill){
                // add the string Take a Pill to the list of events
                events.add("Take a Pill");
            }
            if(MeetYourDoc){
                // add the string Meet your Doctor to the list of events
                events.add("Meet your Doctor");
            }
            if(CallDoc){
                // add the string Call the Doctor to the list of events
                events.add("Call the Doctor");
            }
            if(Prescribed){
                // add the string Prescribed to the list of events
                events.add("Prescribed");
            }
            // log to the logcat the events that have been added
            Log.v("Events",events.toString());

            dismiss();

        }else {
            Log.v("Events", "Non of the Events are true  \nnon have been  saved");
        }
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext(), R.style.CustomDialog);
        dialog.setContentView(R.layout.set_gyn_event);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        return dialog;
    }
}
