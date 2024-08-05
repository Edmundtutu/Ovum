package com.example.ovum;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class LogSymptomsDialogFragment extends DialogFragment {
    // declaring the useful
    private SharedPrefManager sharedPrefs;
    Button confirmChangesBtn;

    public Boolean Heavy = false;
    public Boolean MidLight = false;
    public Boolean Light = false;
    public Boolean VeryLight = false;
    public Boolean RedSpot = false;
    public Boolean BrownSpot = false;
    public Boolean LightBrownSpot = false;
    public Boolean MoodSwings = false;
    public Boolean IsNervous = false;
    public Boolean IsStressed = false;
    public Boolean IsAngry = false;
    //
    // public Boolean FeelsGuilty = false;

    public Boolean IsBloating = false;
    public Boolean HasHeadache = false;
    public Boolean HasHighAppetite= false;
    public Boolean HasLowAppetite = false;
    public Boolean FeelsSleepy = false;
    public Boolean FeelsTired = false;
    public Boolean FeelsStomachPain = false;
    public Boolean testpatientId  = false;
    public Boolean test2 = false;
    RelativeLayout RelativeLayoutTestpatientId;

    private int count = 0;
    private Set<RelativeLayout> selectedLayouts = new HashSet<>();

    // for the title date
    private String titleDate;

    public LogSymptomsDialogFragment(String titleDate) {
        this.titleDate = titleDate;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the activity layout
        View view = inflater.inflate(R.layout.log_symptoms, container, false);
        sharedPrefs = SharedPrefManager.getInstance(getContext());
        // initialise all views in the fragment
        // initialise for the Title of the fragment the date
        TextView title = view.findViewById(R.id.heading_log_text_view);
        title.setText(titleDate);
        // initialize the confirm symptoms Activity
        confirmChangesBtn = view.findViewById(R.id.confirmbtn);
        // Find all the RelativeLayouts containing the icons
        RelativeLayout RelativeLayoutItempatientId = view.findViewById(R.id.forheavy);
        RelativeLayout RelativeLayoutItem2 = view.findViewById(R.id.formedium);
        RelativeLayout RelativeLayoutItem3 = view.findViewById(R.id.forlow);
        RelativeLayout RelativeLayoutItem4 = view.findViewById(R.id.forVerylight);
        RelativeLayout RelativeLayoutItem5 = view.findViewById(R.id.forRedSpotting);
        RelativeLayout RelativeLayoutItem6 = view.findViewById(R.id.forBrownSpotting);
        RelativeLayout RelativeLayoutItem7 = view.findViewById(R.id.forLigtBrownSpotting);
        RelativeLayout RelativeLayoutItem8 = view.findViewById(R.id.forMood);
        //RelativeLayout RelativeLayoutItem5 = view.findViewById(R.id.guilt);
        RelativeLayout RelativeLayoutItem9 = view.findViewById(R.id.fornervours);
        RelativeLayout RelativeLayoutItempatientId0 = view.findViewById(R.id.forAnger);
        RelativeLayout RelativeLayoutItempatientIdpatientId = view.findViewById(R.id.forStressed);
        RelativeLayout RelativeLayoutItempatientId2 = view.findViewById(R.id.forbloating);
        RelativeLayout RelativeLayoutItempatientId3 = view.findViewById(R.id.forheadache);
        RelativeLayout RelativeLayoutItempatientId4 = view.findViewById(R.id.forhighAppetite);
        RelativeLayout RelativeLayoutItempatientId5 = view.findViewById(R.id.forlowAppetite);
        RelativeLayout RelativeLayoutItempatientId6 = view.findViewById(R.id.forsleepy);
        RelativeLayout RelativeLayoutItempatientId7 = view.findViewById(R.id.forstomachPain);
        RelativeLayout RelativeLayoutItempatientId8 = view.findViewById(R.id.forTiredness);
        RelativeLayout RelativeLayoutTestpatientId = view.findViewById(R.id.test1);
        RelativeLayout RelativeLayoutTest2 = view.findViewById(R.id.test2);
        // Set click listeners for each RelativeLayout
        RelativeLayoutItempatientId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Heavy = toggleBackgroundTry(RelativeLayoutItempatientId);
            }
        });

        RelativeLayoutItem2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MidLight = toggleBackgroundTry(RelativeLayoutItem2);
            }
        });

        RelativeLayoutItem3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Light = toggleBackgroundTry(RelativeLayoutItem3);
            }
        });

        RelativeLayoutItem4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VeryLight = toggleBackgroundTry(RelativeLayoutItem4);
            }
        });

        RelativeLayoutItem5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RedSpot = toggleBackgroundTry(RelativeLayoutItem5);
            }
        });

        RelativeLayoutItem6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BrownSpot = toggleBackgroundTry(RelativeLayoutItem6);
            }
        });

        RelativeLayoutItem7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LightBrownSpot = toggleBackgroundTry(RelativeLayoutItem7);
            }
        });

        RelativeLayoutItem8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoodSwings = toggleBackgroundTry(RelativeLayoutItem8);
            }
        });

        RelativeLayoutItem9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IsNervous = toggleBackgroundTry(RelativeLayoutItem9);
            }
        });

        RelativeLayoutItempatientId0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IsAngry = toggleBackgroundTry(RelativeLayoutItempatientId0);
            }
        });

        RelativeLayoutItempatientIdpatientId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IsStressed = toggleBackgroundTry(RelativeLayoutItempatientIdpatientId);
            }
        });

        RelativeLayoutItempatientId2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IsBloating = toggleBackgroundTry(RelativeLayoutItempatientId2);
            }
        });

        RelativeLayoutItempatientId3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HasHeadache = toggleBackgroundTry(RelativeLayoutItempatientId3);
            }
        });

        RelativeLayoutItempatientId4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HasHighAppetite = toggleBackgroundTry(RelativeLayoutItempatientId4);
            }
        });
        RelativeLayoutItempatientId5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HasLowAppetite = toggleBackgroundTry(RelativeLayoutItempatientId5);
            }
        });
        RelativeLayoutItempatientId6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeelsSleepy= toggleBackgroundTry(RelativeLayoutItempatientId6);
            }
        });
        RelativeLayoutItempatientId7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeelsStomachPain = toggleBackgroundTry(RelativeLayoutItempatientId7);
            }
        });
        RelativeLayoutItempatientId8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeelsTired = toggleBackgroundTry(RelativeLayoutItempatientId8);
            }
        });
        RelativeLayoutTestpatientId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testpatientId = toggleBackgroundTry(RelativeLayoutTestpatientId);
            }
        });

        RelativeLayoutTest2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test2 = toggleBackgroundTry(RelativeLayoutTest2);
            }
        });

        // setting the onclick button for the confirm symptoms button
        confirmChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show a dialog for the user to confirm whether their symptoms should be saved
                LogSymptomsDialogFragment.ConfirmSymptomsDialog dialog = new LogSymptomsDialogFragment.ConfirmSymptomsDialog();
                dialog.show(getChildFragmentManager(), "confirmSymptoms");
            }
        });

        return view;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
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

    // method for logging the symptopms
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void logSymptoms() {
        // get the patient id and date the symptoms  are being logged
        // get the patient id
        int patientId = sharedPrefs.getUserId();
        // get the date the symptoms are being logged
        LocalDate fromTitleDate = new DateUtils().formatDateToLocalDate(titleDate);
        String dateRecorded = String.valueOf(fromTitleDate);
        // extract the true boolean values of each  toogle and update the database values in the symptoms table
        if (Heavy||MidLight||Light||VeryLight||RedSpot||BrownSpot||LightBrownSpot||IsStressed||IsAngry||MoodSwings||IsNervous||IsBloating||HasHeadache||HasHighAppetite||HasLowAppetite||FeelsSleepy||FeelsTired||FeelsStomachPain){
            // update the database values
            try (OvumDbHelper ovumDbHelper = new OvumDbHelper(getContext())) {
                if (Heavy) {
                    ovumDbHelper.updateSymptoms(patientId, "Physical", "Period", "Heavy Flo", dateRecorded, null, 5);
                    updateNextPeriodDate(ovumDbHelper,patientId,dateRecorded, fromTitleDate);
                }
                if (MidLight) {
                    ovumDbHelper.updateSymptoms(patientId, "Physical", "Period", "Mid Flo", dateRecorded, null, 5);
                    updateNextPeriodDate(ovumDbHelper,patientId,dateRecorded, fromTitleDate);
                }
                if (Light) {
                    ovumDbHelper.updateSymptoms(patientId, "Physical", "Period", "Light Flo", dateRecorded, null, 5);
                    updateNextPeriodDate(ovumDbHelper,patientId,dateRecorded, fromTitleDate);
                }
                if (VeryLight) {
                    ovumDbHelper.updateSymptoms(patientId, "Physical", "Period", "Very Light Flo", dateRecorded, null, 5);
                    updateNextPeriodDate(ovumDbHelper,patientId,dateRecorded, fromTitleDate);
                }
                if (RedSpot) {
                    ovumDbHelper.updateSymptoms(patientId, "Physical", "Spotting", "Red Color", dateRecorded, null, 5);
                }
                if (BrownSpot) {
                    ovumDbHelper.updateSymptoms(patientId, "Physical", "Spotting", "Brown Color", dateRecorded, null, 5);
                }
                if (LightBrownSpot) {
                    ovumDbHelper.updateSymptoms(patientId, "Physical", "Spotting", "Light Brown Color", dateRecorded, null, 5);
                }
                if (IsStressed) {
                    ovumDbHelper.updateSymptoms(patientId, "NonPhysical", "Mood", "Is Stressed", dateRecorded, null, 1);
                }
                if (IsAngry) {
                    ovumDbHelper.updateSymptoms(patientId, "NonPhysical", "Mood", "Is Angry", dateRecorded, null, 1);
                }
                if (MoodSwings) {
                    ovumDbHelper.updateSymptoms(patientId, "NonPhysical", "Mood", "Feels Apathetic", dateRecorded, null, 1);
                }
                if (IsNervous) {
                    ovumDbHelper.updateSymptoms(patientId, "NonPhysical", "Mood", "Is Nervous", dateRecorded, null, 1);
                }
                if (IsBloating) {
                    ovumDbHelper.updateSymptoms(patientId, "Physical", "Feeling", "Is Bloating", dateRecorded, null, 3);
                }
                if (HasHeadache) {
                    ovumDbHelper.updateSymptoms(patientId, "Physical", "Feeling", "Has Headache", dateRecorded, null, 1);
                }
                if (HasHighAppetite) {
                    ovumDbHelper.updateSymptoms(patientId, "Physical", "Feeling", "Has High Appetite", dateRecorded, null, 3);
                }

                if (HasLowAppetite) {
                    ovumDbHelper.updateSymptoms(patientId, "Physical", "Feeling", "Has Low Appetite", dateRecorded, null, 1);
                }
                if (FeelsSleepy) {
                    ovumDbHelper.updateSymptoms(patientId, "NonPhysical", "Mood", "Feels Sleepy", dateRecorded, null, 1);
                }
                if (FeelsTired) {
                    ovumDbHelper.updateSymptoms(patientId, "NonPhysical", "Mood", "Feels Tired", dateRecorded, null, 1);
                }
                if (FeelsStomachPain) {
                    ovumDbHelper.updateSymptoms(patientId, "Physical", "Feeling", "Feels Stomach Pain", dateRecorded, null, patientId);
                }
            }catch (Exception e){
                Log.e("LogSymptoms", "Error updating the symptoms table", e);
            }
            // for debugging purposes log any of the symptoms that are true and have been recorded
            // log the true states of the symptoms
            Log.v("LogSymptoms", "Feels Heavy: " + Heavy);
            Log.v("LogSymptoms", "Is Stressed: " + IsStressed);
            Log.v("LogSymptoms", "Is Angry: " + IsAngry);
            Log.v("LogSymptoms", "Feels Apathetic: " + MoodSwings);
            Log.v("LogSymptoms", "Is Nervous: " + IsNervous);
            Log.v("LogSymptoms", "Is Bloating: " + IsBloating);
            Log.v("LogSymptoms", "Has Headache: " + HasHeadache);
            Log.v("LogSymptoms", "Has High Appetite: " + HasHighAppetite);
            Log.v("LogSymptoms", "Has Low Appetite: " + HasLowAppetite);
            Log.v("LogSymptoms", "Feels Sleepy: " + FeelsSleepy);
            Log.v("LogSymptoms", "Feels Tired: " + FeelsTired);
            Log.v("LogSymptoms", "Feels Stomach Pain: " + FeelsStomachPain);

            Log.v("LogSymptoms", "Symptoms have been logged");
            dismiss();

        }else {
            Log.v("LogSymptoms", "Non of the  symptoms are true  \n non have been logged");
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateNextPeriodDate(OvumDbHelper ovumDbHelper, int patientId, String dateOfRecording, LocalDate dateRecorded) {
        // the current cycleLength and period length according to the logic of the prediction TBD
        int currentCycleLength = 28;
        int periodLength = 5;
        // set the PeriodSupposed to end Date
        LocalDate periodSupposedToEnd = dateRecorded.plusDays(periodLength);
        // get the next period date
        LocalDate nextPeriodDate = dateRecorded.plusDays(currentCycleLength);
        // set the next ovulation date
        LocalDate nextOvulationDate = dateRecorded.plusDays(currentCycleLength - 14);
        ovumDbHelper.updatePatient(patientId,null,null,null,null,null,null,null,currentCycleLength,periodLength,dateOfRecording, String.valueOf(periodSupposedToEnd),-1,-1,String.valueOf(nextOvulationDate),String.valueOf(nextPeriodDate));
        // update the shared Prefs as well
        sharedPrefs.storePatientInfo(String.valueOf(nextPeriodDate));
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext(), R.style.CustomDialog);
        dialog.setContentView(R.layout.log_symptoms);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        return dialog;
    }

    public static class ConfirmSymptomsDialog extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Confirm Symptoms");
            builder.setMessage("Are you sure you want to save these symptoms?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Save the symptoms to the database
                    // Call the logSymptoms method
                    ((LogSymptomsDialogFragment) getParentFragment()).logSymptoms();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Dismiss the dialog
                    dismiss();
                }
            });
            return builder.create();
        }
    }
}
