package com.pac.ovum;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.pac.ovum.databinding.ActivityMainBinding;
import com.pac.ovum.databinding.DialogCalenderViewBinding;
import com.pac.ovum.utils.ui.DialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        // set the action of the fab button
        // Initialize the progress dialog
        progressDialog = new ProgressDialog(this);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DialogBuilder(view.getContext())
                        .setTitle("Alert Your Doctor about Today's Symptoms")
                        .setMessage("You are required to pay for the service.")
                        .setPositiveButton("Make payment", (dialog, which) -> {
                            // unwrap the dialog and show the bottom sheet dialog
                            showBottomDialog();
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_calendar, R.id.nav_cycles)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // setup the bottom navigation view
        NavigationUI.setupWithNavController(binding.appBarMain.bottomNavView, navController);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here.
        // Use if statements to handle the item clicks
        if (item.getItemId() == R.id.calendar_icon) {
            // Handle the calendar action
            showCalendarPopup(date -> {
                // TODO: Handle the ToolBar date selection here
            });
            return true;
        } else if (item.getItemId() == R.id.search_icon) {
            // TODO: Handle the search action
            return true;
        } else if (item.getItemId() == R.id.logout_icon) {
            // TODO: Handle the logout action
            // start the MainActivit2
            startActivity(new Intent(this, MainActivity2.class));
//            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void showCalendarPopup(OnDateSelectedListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        DialogCalenderViewBinding binding = DialogCalenderViewBinding.inflate(LayoutInflater.from(this));
        builder.setView(binding.getRoot());
        AlertDialog dialog = builder.create();
        binding.calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String formattedDate = String.format(Locale.getDefault(), "%s %02d", new SimpleDateFormat("MMMM", Locale.getDefault()).format(new Date(year, month, dayOfMonth)), dayOfMonth);
            listener.onDateSelected(formattedDate);
            dialog.dismiss();
        });
        dialog.show();
    }
    @SuppressLint("ClickableViewAccessibility")
    private void showBottomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);

        TextView confirmButton = dialog.findViewById(R.id.confirmButton);
        TextView cancelButton = dialog.findViewById(R.id.cancelButton);
        RadioGroup radioGroup = dialog.findViewById(R.id.paymentMethodRadioGroup);
        EditText phoneNumberEditText = dialog.findViewById(R.id.phoneNumberEditText);
        ImageView closeButton = dialog.findViewById(R.id.closeButton);


        // Set OnTouchListener to dismiss the fragment
        closeButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                dialog.dismiss(); // Dismiss the dialog on touch release
            }
            return true; // Indicate that the touch event is consumed
        });

        // Set an OnCheckedChangeListener for the RadioGroup
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // You can handle instant changes here if needed
        });

        confirmButton.setOnClickListener(v -> {
            int checkedId = radioGroup.getCheckedRadioButtonId();
            String customerPhoneNumber = phoneNumberEditText.getText().toString();

            if (checkedId == R.id.mtnMoMoRadioButton) {
                dialog.dismiss();
                lazilyRequestMtn(customerPhoneNumber);
            } else if (checkedId == R.id.airtelMoneyRadioButton) {
                Toast.makeText(this, "Airtel Money is yet to be contacted", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    // Other important methods to be used in the MainActivity class
    private void lazilyRequestMtn(String customerPhoneNumber) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://192.168.137.1/SwiftMoMo/requestpay.php";
        progressDialog.setMessage("Contacting MTN...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.hide();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            // log the response for debugging and display a toast message
                            Log.d("Response", jsonObject.toString());
                            Toast.makeText(getApplicationContext(), "payment went successfully", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.hide();
                if (volleyError instanceof com.android.volley.TimeoutError) {
                    Toast.makeText(getApplicationContext(), "Request timed out, Try again later", Toast.LENGTH_LONG).show();
                } else {
                    Log.v("Volley Error", volleyError.toString());
                    Toast.makeText(getApplicationContext(), "Opps! There was an issue, Try again later", Toast.LENGTH_LONG).show();
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("phone", customerPhoneNumber);
                params.put("amount", "1000");
                return params;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
        // Set the retry policy
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                2000, // Timeout in milliseconds
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // Number of retries
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT // Backoff multiplier
        ));

        queue.add(stringRequest);

    }

    // interfaces used in the MainActivity class

    // interface for handling date selection
    interface OnDateSelectedListener {
        void onDateSelected(String date);
    }

}