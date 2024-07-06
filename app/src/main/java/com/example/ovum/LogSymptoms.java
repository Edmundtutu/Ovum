package com.example.ovum;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.util.HashSet;
import java.util.Set;


public class LogSymptoms extends AppCompatActivity {
    // declaring the useful
    Button confirmChangesBtn;
    public Boolean HasCramps = false;
    public Boolean HasMoodSwings = false;
    public Boolean FeelsHeavy = false;
    public Boolean IsStressed = false;
    public Boolean IsAngry = false;
    public Boolean FeelsApathetic = false;
   //
   // public Boolean FeelsGuilty = false;
    public Boolean IsNervous = false;
    public Boolean IsBloating = false;
    public Boolean HasHeadache = false;
    public Boolean HasHighAppetite= false;
    public Boolean HasLowAppetite = false;
    public Boolean FeelsSleepy = false;
    public Boolean FeelsTired = false;
    public Boolean FeelsStomachPain = false;
    public Boolean test1  = false;
    public Boolean test2 = false;
    LinearLayout linearLayoutTest1;

    private int count = 0;
    private Set<LinearLayout> selectedLayouts = new HashSet<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_symptoms);
        // initialize the confirm symptoms Activity
        confirmChangesBtn = findViewById(R.id.confirmbtn);

        // Find all the LinearLayouts containing the icons
        LinearLayout linearLayoutItem1 = findViewById(R.id.heavy);
        LinearLayout linearLayoutItem2 = findViewById(R.id.medium);
        LinearLayout linearLayoutItem3 = findViewById(R.id.low);
        LinearLayout linearLayoutItem4 = findViewById(R.id.apathetic);
        //LinearLayout linearLayoutItem5 = findViewById(R.id.guilt);
        LinearLayout linearLayoutItem6 = findViewById(R.id.nervous);
        LinearLayout linearLayoutItem7 = findViewById(R.id.bloating);
        LinearLayout linearLayoutItem8 = findViewById(R.id.headache);
        LinearLayout linearLayoutItem9 = findViewById(R.id.highAppetite);
        LinearLayout linearLayoutItem10 = findViewById(R.id.lowAppetite);
        LinearLayout linearLayoutItem11 = findViewById(R.id.sleepy);
        LinearLayout linearLayoutItem12 = findViewById(R.id.stomachPain);
        LinearLayout linearLayoutItem13 = findViewById(R.id.tiredness);
        linearLayoutTest1 = findViewById(R.id.test1);
        LinearLayout linearLayoutTest2 = findViewById(R.id.test2);

        // Set click listeners for each LinearLayout
        linearLayoutItem1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               FeelsHeavy = toggleBackgroundTry(linearLayoutItem1);
            }
        });

        linearLayoutItem2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IsStressed = toggleBackgroundTry(linearLayoutItem2);
            }
        });

        linearLayoutItem3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IsAngry = toggleBackgroundTry(linearLayoutItem3);
            }
        });

        linearLayoutItem4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeelsApathetic = toggleBackgroundTry(linearLayoutItem4);
            }
        });

        /*linearLayoutItem5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleBackgroundTry(linearLayoutItem5, FeelsGuilty);
            }
        });*/

        linearLayoutItem6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IsNervous = toggleBackgroundTry(linearLayoutItem6);
            }
        });

        linearLayoutItem7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IsBloating = toggleBackgroundTry(linearLayoutItem7);
            }
        });

        linearLayoutItem8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HasHeadache = toggleBackgroundTry(linearLayoutItem8);
            }
        });

        linearLayoutItem9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HasHighAppetite = toggleBackgroundTry(linearLayoutItem9);
            }
        });

        linearLayoutItem10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HasLowAppetite = toggleBackgroundTry(linearLayoutItem10);
            }
        });

        linearLayoutItem11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeelsSleepy = toggleBackgroundTry(linearLayoutItem11);
            }
        });

        linearLayoutItem12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeelsStomachPain = toggleBackgroundTry(linearLayoutItem12);
            }
        });

        linearLayoutItem13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeelsTired = toggleBackgroundTry(linearLayoutItem13);
            }
        });
        linearLayoutTest1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 test1 = toggleBackgroundTry(linearLayoutTest1);
            }
        });

        linearLayoutTest2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test2 = toggleBackgroundTry(linearLayoutTest2);
            }
        });

        // setting the onclick button for the confirm symptoms button
        confirmChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show a dialog for the user to confirm whether their symptoms should be saved
                ConfirmSymptomsDialog dialog = new ConfirmSymptomsDialog();
                dialog.show(getSupportFragmentManager(), "confirmSymptoms");
            }
        });
    }


    // method for logging the symptopms
    public void logSymptoms() {
        // extract the true boolean values of each  toogle and update the database values in the symptoms table
        if (FeelsHeavy||IsStressed||IsAngry||FeelsApathetic||IsNervous||IsBloating||HasHeadache||HasHighAppetite||HasLowAppetite||FeelsSleepy||FeelsTired||FeelsStomachPain){
            // update the database values
            try (OvumDbHelper ovumDbHelper = new OvumDbHelper(getApplicationContext())) {
                if (FeelsHeavy) {
                    ovumDbHelper.updateSymptoms(1, "Physical", "Heavy", "Feels Heavy", null, null, 5);
                }
                if (IsStressed) {
                    ovumDbHelper.updateSymptoms(1, "Mood", "Stressed", "Is Stressed", null, null, 1);
                }
                if (IsAngry) {
                    ovumDbHelper.updateSymptoms(1, "Mood", "Angry", "Is Angry", null, null, 1);
                }
                if (FeelsApathetic) {
                    ovumDbHelper.updateSymptoms(1, "Mood", "Apathetic", "Feels Apathetic", null, null, 1);
                }
                if (IsNervous) {
                    ovumDbHelper.updateSymptoms(1, "Mood", "Nervous", "Is Nervous", null, null, 1);
                }
                if (IsBloating) {
                    ovumDbHelper.updateSymptoms(1, "Physical", "Bloating", "Is Bloating", null, null, 3);
                }
                if (HasHeadache) {
                    ovumDbHelper.updateSymptoms(1, "Physical", "Headache", "Has Headache", null, null, 1);
                }
                if (HasHighAppetite) {
                    ovumDbHelper.updateSymptoms(1, "Physical", "High Appetite", "Has High Appetite", null, null, 3);
                }

                if (HasLowAppetite) {
                    ovumDbHelper.updateSymptoms(1, "Physical", "Low Appetite", "Has Low Appetite", null, null, 1);
                }
                if (FeelsSleepy) {
                    ovumDbHelper.updateSymptoms(1, "Mood", "Sleepy", "Feels Sleepy", null, null, 1);
                }
                if (FeelsTired) {
                    ovumDbHelper.updateSymptoms(1, "Mood", "Tired", "Feels Tired", null, null, 1);
                }
                if (FeelsStomachPain) {
                    ovumDbHelper.updateSymptoms(1, "Physical", "Stomach Pain", "Feels Stomach Pain", null, null, 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // for debugging purposes log any of the symptoms that are true and have been recorded
            // log the true states of the symptoms
            Log.v("LogSymptoms", "Feels Heavy: " + FeelsHeavy);
            Log.v("LogSymptoms", "Is Stressed: " + IsStressed);
            Log.v("LogSymptoms", "Is Angry: " + IsAngry);
            Log.v("LogSymptoms", "Feels Apathetic: " + FeelsApathetic);
            Log.v("LogSymptoms", "Is Nervous: " + IsNervous);
            Log.v("LogSymptoms", "Is Bloating: " + IsBloating);
            Log.v("LogSymptoms", "Has Headache: " + HasHeadache);
            Log.v("LogSymptoms", "Has High Appetite: " + HasHighAppetite);
            Log.v("LogSymptoms", "Has Low Appetite: " + HasLowAppetite);
            Log.v("LogSymptoms", "Feels Sleepy: " + FeelsSleepy);
            Log.v("LogSymptoms", "Feels Tired: " + FeelsTired);
            Log.v("LogSymptoms", "Feels Stomach Pain: " + FeelsStomachPain);

            Log.v("LogSymptoms", "Symptoms have been logged");
            finish();

        }else {
            Log.v("LogSymptoms", "Non of the  symptoms are true  \n non have been logged");
        }

    }

    // inner class for confirming the symptoms dialog
    public static class ConfirmSymptomsDialog extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Confirm Symptoms");
            builder.setMessage("Are you sure you want to save these symptoms?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Save the symptoms to the database
                    // Call the logSymptoms method
                    assert getActivity() != null;
                    ((LogSymptoms) getActivity()).logSymptoms();
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


    @SuppressLint("UseCompatLoadingForDrawables")
    private Boolean toggleBackgroundTry(LinearLayout layout) {
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

}


//
//// setting onclick listners
//linearLayoutItem1.setOnClickListener(new View.OnClickListener() {
//// declare the relativelayout id for each background
//final int id = R.id.forheavy;
//
//        String Logstmt ;
//
//@Override
//public void onClick(View v) {
//        Logstmt = "false";
//        if(toggleBackground(v,id)){
//        //  set the state symptom
//        FeelsHeavy = true;
//        Logstmt = "true";
//        }
//        Log.v("Toggle State","Feels Heavy :" +Logstmt);
//        }
//        });
//
//        linearLayoutItem2.setOnClickListener(new View.OnClickListener() {
//final int id = R.id.formedium;
//@Override
//public void onClick(View v) {
//        toggleBackground(v,id);
//        IsStressed = true;
//        }
//        });
//
//        linearLayoutItem3.setOnClickListener(new View.OnClickListener() {
//final int id = R.id.forlow;
//@Override
//public void onClick(View v) {
//        toggleBackground(v,id);
//        IsAngry = true;
//        }
//        });
//
//        linearLayoutItem4.setOnClickListener(new View.OnClickListener() {
//final int id = R.id.forapathetic;
//@Override
//public void onClick(View v) {
//        toggleBackground(v,id);
//        FeelsApathetic = true;
//        }
//        });
//
//      /*  linearLayoutItem5.setOnClickListener(new View.OnClickListener() {
//            final int id = R.id.forguilt;
//            @Override
//            public void onClick(View v) {
//                toggleBackground(v,id);
//                FeelsGuilty = true;
//            }
//        });*/
//
//        linearLayoutItem6.setOnClickListener(new View.OnClickListener() {
//final int id = R.id.fornervours;
//@Override
//public void onClick(View v) {
//        toggleBackground(v,id);
//        IsNervous = true;
//        }
//        });
//
//        linearLayoutItem7.setOnClickListener(new View.OnClickListener() {
//final int id = R.id.forbloating;
//@Override
//public void onClick(View v) {
//        toggleBackground(v,id);
//        IsBloating = true;
//        }
//        });
//
//        linearLayoutItem8.setOnClickListener(new View.OnClickListener() {
//final int id = R.id.forheadache;
//@Override
//public void onClick(View v) {
//        toggleBackground(v,id);
//        HasHeadache= true;
//        }
//        });
//
//        linearLayoutItem9.setOnClickListener(new View.OnClickListener() {
//final int id = R.id.forhighAppetite;
//@Override
//public void onClick(View v) {
//        toggleBackground(v,id);
//        HasHighAppetite = true;
//        }
//        });
//
//        linearLayoutItem10.setOnClickListener(new View.OnClickListener() {
//final int id = R.id.forlowAppetite;
//@Override
//public void onClick(View v) {
//        toggleBackground(v,id);
//        HasLowAppetite = true;
//        }
//        });
//
//        linearLayoutItem11.setOnClickListener(new View.OnClickListener() {
//final int id = R.id.forsleepy;
//@Override
//public void onClick(View v) {
//        toggleBackground(v,id);
//        FeelsSleepy = true;
//        }
//        });
//
//        linearLayoutItem12.setOnClickListener(new View.OnClickListener() {
//final int id = R.id.forstomachPain;
//@Override
//public void onClick(View v) {
//        toggleBackground(v,id);
//        FeelsStomachPain = true;
//        }
//        });
//
//        linearLayoutItem13.setOnClickListener(new View.OnClickListener() {
//final int id = R.id.forTiredness;
//@Override
//public void onClick(View v) {
//        toggleBackground(v,id);
//        FeelsTired = true;
//        }
//        });

//
//    //     Method to toggle background of the RelativeLayout inside LinearLayout
//    private boolean toggleBackground(View v, int relativelayoutId) {
//        Boolean stateOfToggle = false;
//        RelativeLayout relativeLayout = v.findViewById(relativelayoutId);
//        Drawable currentBackground = relativeLayout.getBackground();
//        Drawable mixtureGradient = ContextCompat.getDrawable(this, R.drawable.gradient_mixture);
//        Drawable circularGradient = ContextCompat.getDrawable(this, R.drawable.circular_gradient);
//
//        // Check if the current background is the circular gradient
//        if (currentBackground != null && currentBackground.equals(circularGradient)) {
//            // If the current background is circular gradient, set it to the default background
//            relativeLayout.setBackground(mixtureGradient);
//            // Set button background color to default if no layout is selected
//            if (!isAnyLayoutSelected(v)) {
//                confirmChangesBtn.setEnabled(false);
//                confirmChangesBtn.setBackgroundColor(getResources().getColor(R.color.pale_pink));
//            }
//        } else {
//            stateOfToggle = true;
//            // If the current background is not circular gradient, set it to circular gradient
//            relativeLayout.setBackground(circularGradient);
//            // Enable confirmChangesBtn and set its background
//            confirmChangesBtn.setEnabled(true);
//            confirmChangesBtn.setBackgroundColor(getResources().getColor(R.color.hot_pink));
//        }
//        return stateOfToggle;
//    }
//
//    //     Method to check if any layout is selected (i.e., has circular gradient background)
//    private boolean isAnyLayoutSelected(View v) {
//        LinearLayout parentLayout = (LinearLayout) v.getParent();
//        Drawable circularGradient = ContextCompat.getDrawable(this, R.drawable.circular_gradient);
//
//        for (int i = 0; i < parentLayout.getChildCount(); i++) {
//            View child = parentLayout.getChildAt(i);
//            if (child instanceof RelativeLayout) {
//                Drawable background = child.getBackground();
//                if (background != null && background.equals(circularGradient)) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//
//    // Method to reset all RelativeLayout backgrounds to the default state
//    private void resetAllBackgrounds(View v) {
//        ViewGroup parentLayout = (ViewGroup) v.getParent();
//        Drawable mixtureGradient = ContextCompat.getDrawable(this, R.drawable.gradient_mixture);
//
//        for (int i = 0; i < parentLayout.getChildCount(); i++) {
//            View child = parentLayout.getChildAt(i);
//            if (child instanceof RelativeLayout) {
//                child.setBackground(mixtureGradient);
//            }
//        }
//    }
