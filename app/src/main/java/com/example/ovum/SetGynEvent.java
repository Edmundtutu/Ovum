package com.example.ovum;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ovum.databinding.ActivitySetGynEventBinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class SetGynEvent extends AppCompatActivity {
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySetGynEventBinding binding = ActivitySetGynEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // binding the button and relative layout
        confirmChangesBtn = binding.confirmBtnForEvents;
        RelativeLayout meetDoc = binding.forVisit;
        RelativeLayout talkToDoc = binding.forTalkToDoc;
        RelativeLayout logASymptom = binding.forMakeLog;
        RelativeLayout involveInSex = binding.forSexIntercource;
        RelativeLayout takeAPill = binding.forTakePill;
        RelativeLayout meetYourDoc = binding.forMeetUp;
        RelativeLayout callDoc = binding.forcalling;
        RelativeLayout prescribed = binding.forPrescribed;

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

            finish();

        }else {
            Log.v("Events", "Non of the Events are true  \nnon have been  saved");
        }
    }
}
