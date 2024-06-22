package com.example.ovum;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Login extends AppCompatActivity {
    private Button loginBtn;
    private TextView signUp;

    private ProgressDialog progressDialog;


    private EditText name;
    private EditText userPassword;

    // db
    private OvumDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = findViewById(R.id.loginbtn);
        signUp = findViewById(R.id.signUpOpt);
        name = findViewById(R.id.editTextName);
        userPassword = findViewById(R.id.editTextPassword);

        dbHelper = new OvumDbHelper(this);

        // innitializing the progressDialog
        progressDialog =new ProgressDialog(this);

        // Making the Input Text Views interactive with the login button
        EditText[] editTextArray = {name, userPassword};

        Drawable clickbg = ContextCompat.getDrawable(this, R.drawable.corner_radius_pinch);
        for (EditText editText : editTextArray) {
            editText.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    // Execute method when EditText gains focus
                    loginBtn.setEnabled(true);
                    loginBtn.setBackground(clickbg);
                }
            });
        }

        signUp.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, CreateAccount.class);
            startActivity(intent);
            finish();
        });

        // giving the loginBtn an onclick listener
        loginBtn.setOnClickListener(v -> userLogin());
    }
    public void userLogin() {
        final String username = name.getText().toString().trim();
        final String password = userPassword.getText().toString().trim();

        progressDialog.show();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                OvumContract.PatientEntry._ID,
                OvumContract.PatientEntry.COLUMN_NAME,
                OvumContract.PatientEntry.COLUMN_EMAIL
        };

        String selection = OvumContract.PatientEntry.COLUMN_NAME + " = ? AND " + OvumContract.PatientEntry.COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = { username, password };

        Cursor cursor = db.query(
                OvumContract.PatientEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(OvumContract.PatientEntry._ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(OvumContract.PatientEntry.COLUMN_NAME));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(OvumContract.PatientEntry.COLUMN_EMAIL));

            SharedPrefManager.getInstance(getApplicationContext()).userLogin(userId, name, email);

            // Debugging logs
            Log.d("UserDetails Provided", "ID: " + userId + ", Name: " + name + ", Email: " + email);
            Log.d("UserDetails Saved", "ID: " + SharedPrefManager.getInstance(getApplicationContext()).getUserId() + ", Name: " + SharedPrefManager.getInstance(getApplicationContext()).getUsername() + ", Email: " + SharedPrefManager.getInstance(getApplicationContext()).getUserEmail());

            progressDialog.dismiss();
            startActivity(new Intent(Login.this, MainActivity.class));
            finish(); // Optional: Close the current activity

        } else {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Login failed: Invalid credentials or user does not exist.", Toast.LENGTH_LONG).show();
        }

        if (cursor != null) {
            cursor.close();
        }
    }
}