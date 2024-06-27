package com.example.ovum;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


public class LogSymptoms extends AppCompatActivity {
    // declaring the useful
    Button confirmChangesBtn;
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

        // Set click listeners for each LinearLayout
        linearLayoutItem1.setOnClickListener(new View.OnClickListener() {
            // declare the relativelayout id for each background
            final int id = R.id.forheavy;

            @Override
            public void onClick(View v) {
                toggleBackground(v,id);
                //  set the state symtop
                FeelsHeavy = true;
            }
        });

        linearLayoutItem2.setOnClickListener(new View.OnClickListener() {
            final int id = R.id.formedium;
            @Override
            public void onClick(View v) {
                toggleBackground(v,id);
                IsStressed = true;
            }
        });

        linearLayoutItem3.setOnClickListener(new View.OnClickListener() {
            final int id = R.id.forlow;
            @Override
            public void onClick(View v) {
                toggleBackground(v,id);
                IsAngry = true;
            }
        });

        linearLayoutItem4.setOnClickListener(new View.OnClickListener() {
            final int id = R.id.forapathetic;
            @Override
            public void onClick(View v) {
                toggleBackground(v,id);
                FeelsApathetic = true;
            }
        });

      /*  linearLayoutItem5.setOnClickListener(new View.OnClickListener() {
            final int id = R.id.forguilt;
            @Override
            public void onClick(View v) {
                toggleBackground(v,id);
                FeelsGuilty = true;
            }
        });*/

        linearLayoutItem6.setOnClickListener(new View.OnClickListener() {
            final int id = R.id.fornervours;
            @Override
            public void onClick(View v) {
                toggleBackground(v,id);
                IsNervous = true;
            }
        });

        linearLayoutItem7.setOnClickListener(new View.OnClickListener() {
            final int id = R.id.forbloating;
            @Override
            public void onClick(View v) {
                toggleBackground(v,id);
                IsBloating = true;
            }
        });

        linearLayoutItem8.setOnClickListener(new View.OnClickListener() {
            final int id = R.id.forheadache;
            @Override
            public void onClick(View v) {
                toggleBackground(v,id);
                HasHeadache= true;
            }
        });

        linearLayoutItem9.setOnClickListener(new View.OnClickListener() {
            final int id = R.id.forhighAppetite;
            @Override
            public void onClick(View v) {
                toggleBackground(v,id);
                HasHighAppetite = true;
            }
        });

        linearLayoutItem10.setOnClickListener(new View.OnClickListener() {
            final int id = R.id.forlowAppetite;
            @Override
            public void onClick(View v) {
                toggleBackground(v,id);
                HasLowAppetite = true;
            }
        });

        linearLayoutItem11.setOnClickListener(new View.OnClickListener() {
            final int id = R.id.forsleepy;
            @Override
            public void onClick(View v) {
                toggleBackground(v,id);
                FeelsSleepy = true;
            }
        });

        linearLayoutItem12.setOnClickListener(new View.OnClickListener() {
            final int id = R.id.forstomachPain;
            @Override
            public void onClick(View v) {
                toggleBackground(v,id);
                FeelsStomachPain = true;
            }
        });

        linearLayoutItem13.setOnClickListener(new View.OnClickListener() {
            final int id = R.id.forTiredness;
            @Override
            public void onClick(View v) {
                toggleBackground(v,id);
                FeelsTired = true;
            }
        });

        // setting the onclick button for the confirm symptoms button
        confirmChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // extract the true boolean values of each  toogle and update the database values in the symptoms table
                if (FeelsHeavy||IsStressed||IsAngry||FeelsApathetic||IsNervous||IsBloating||HasHeadache||HasHighAppetite||HasLowAppetite||FeelsSleepy||FeelsTired||FeelsStomachPain){
                    // update the database values
//                    OvumDbHelper ovumDbHelper = new OvumDbHelper(getApplicationContext());
                    // show a dialog for the user to confirm whether their symptoms should be saved


                    // if the user confirms the symptoms should be saved
//                    // loop through each true value and update the database for the patient accordingly
//                    if (FeelsHeavy){
//                        ovumDbHelper.updateSymptoms(1,1,"Heavy","Feels Heavy",null,null,1);
//                    }
//                    if (IsStressed){
//                        ovumDbHelper.updateSymptoms(1,1,"Stressed","Is Stressed",null,null,1);
//                    }
//                    if (IsAngry){
//                        ovumDbHelper.updateSymptoms(1,1,"Angry","Is Angry",null,null,1);
//                    }
//                    if (FeelsApathetic){
//                        ovumDbHelper.updateSymptoms(1,1,"Apathetic","Feels Apathetic",null,null,1);
//                    }
//                    if (IsNervous){
//                        ovumDbHelper.updateSymptoms(1,1,"Nervous","Is Nervous",null,null,1);
//                    }
//                    if (IsBloating){
//                        ovumDbHelper.updateSymptoms(1,1,"Bloating","Is Bloating",null,null,1);
//                    }
//                    if (HasHeadache){
//                        ovumDbHelper.updateSymptoms(1,1,"Headache","Has Headache",null,null,1);
//                    }
//                    if (HasHighAppetite){
//                        ovumDbHelper.updateSymptoms(1,1,"High Appetite","Has High Appetite",null,null,1);
//                    }
//
//                    if (HasLowAppetite){
//                        ovumDbHelper.updateSymptoms(1,1,"Low Appetite","Has Low Appetite",null,null,1);
//                    }
//                    if (FeelsSleepy){
//                        ovumDbHelper.updateSymptoms(1,1,"Sleepy","Feels Sleepy",null,null,1);
//                    }
//                    if (FeelsTired){
//                        ovumDbHelper.updateSymptoms(1,1,"Tired","Feels Tired",null,null,1);
//                    }
//                    if (FeelsStomachPain){
//                        ovumDbHelper.updateSymptoms(1,1,"Stomach Pain","Feels Stomach Pain",null,null,1);
//                    }
                    // show a toast message to the user that their symptoms have been saved
                    // finish this activity and return to the previous activity
                    finish();

                }
            }
        });
    }
    // Method to toggle background of the RelativeLayout inside LinearLayout
//    private Boolean toggleBackground(View v, int relativelayoutId) {
//        Boolean stateOfToogle = false;
//        RelativeLayout relativeLayout = v.findViewById(relativelayoutId);
//        Drawable currentBackground = relativeLayout.getBackground();
//        Drawable mixtureGradient = ContextCompat.getDrawable(this, R.drawable.gradient_mixture);
//        Drawable circularGradient = ContextCompat.getDrawable(this, R.drawable.circular_gradient);
//
//        // Check if the current background is the circular gradient
//        if (currentBackground != null && currentBackground.equals(circularGradient)) {
//            // If the current background is circular gradient, set it to the default background
//            relativeLayout.setBackground(mixtureGradient);
//        } else {
//            // If the current background is not circular gradient, set it to circular gradient
//            relativeLayout.setBackground(circularGradient);
//            stateOfToogle = true;
//        }
//
//        // Update the state of the confirmChangesBtn based on whether any layout is selected
//        if (isAnyLayoutSelected(v)) {
//            confirmChangesBtn.setEnabled(true);
//            confirmChangesBtn.setBackgroundColor(getResources().getColor(R.color.hot_pink));
//        } else {
//            confirmChangesBtn.setEnabled(false);
//            confirmChangesBtn.setBackgroundColor(getResources().getColor(R.color.pale_pink));
//        }
//        return stateOfToogle;
//    }
//
//    // Method to check if any layout is selected (i.e., has circular gradient background)
//    // Method to check if any layout is selected (i.e., has circular gradient background)
//    private boolean isAnyLayoutSelected(View v) {
//        ViewGroup parentLayout = (ViewGroup) v.getParent();
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

    // Method to reset all RelativeLayout backgrounds to the default state


    // Method to toggle background of the RelativeLayout inside LinearLayout
    private void toggleBackground(View v, int relativelayoutId) {
        RelativeLayout relativeLayout = v.findViewById(relativelayoutId);
        Drawable currentBackground = relativeLayout.getBackground();
        Drawable mixtureGradient = ContextCompat.getDrawable(this, R.drawable.gradient_mixture);
        Drawable circularGradient = ContextCompat.getDrawable(this, R.drawable.circular_gradient);

        // Check if the current background is the circular gradient
        if (currentBackground != null && currentBackground.equals(circularGradient)) {
            // If the current background is circular gradient, set it to the default background
            relativeLayout.setBackground(mixtureGradient);
            // Set button background color to default if no layout is selected
            if (!isAnyLayoutSelected(v)) {
                confirmChangesBtn.setEnabled(false);
                confirmChangesBtn.setBackgroundColor(getResources().getColor(R.color.pale_pink));
            }
        } else {
            // If the current background is not circular gradient, set it to circular gradient
            relativeLayout.setBackground(circularGradient);
            // Enable confirmChangesBtn and set its background
            confirmChangesBtn.setEnabled(true);
            confirmChangesBtn.setBackgroundColor(getResources().getColor(R.color.hot_pink));
        }
    }

    // Method to check if any layout is selected (i.e., has circular gradient background)
    private boolean isAnyLayoutSelected(View v) {
        LinearLayout parentLayout = (LinearLayout) v.getParent();
        for (int i = 0; i < parentLayout.getChildCount(); i++) {
            View child = parentLayout.getChildAt(i);
            if (child instanceof RelativeLayout) {
                Drawable background = child.getBackground();
                Drawable circularGradient = ContextCompat.getDrawable(this, R.drawable.circular_gradient);
                if (background != null && background.equals(circularGradient)) {
                    return true;
                }
            }
        }
        return false;
    }





}