package com.example.liveli;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.liveli.parseobjects.UserProfile;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.json.JSONArray;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";

    EditText ptUsername;
    EditText etPassword;
    Button btnLogin;
    Button btnSignup;
    //Button btnSignupActivity;
    TextView tvSignUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (ParseUser.getCurrentUser() != null) {
            reDirectMainActivity();
        }

        //Log.i(TAG, ParseUser.getCurrentUser().getUsername());

        //get references to the view in loginActivity
        ptUsername = findViewById(R.id.ptUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);

        /*
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reDirectSignUpActivity();
            }
        });
        */

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = ptUsername.getText().toString();
                String password = etPassword.getText().toString();

                //call the function to handle login presses.
                loginUser(username, password);
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = ptUsername.getText().toString();
                String password = etPassword.getText().toString();

                //call the function to handle login presses.
                registerUser(username, password);
            }
        });
    }

    /*
    private void reDirectSignUpActivity() {
        Intent i = new Intent(this, SignUpActivity.class);
        startActivity(i);
    }
    */

    private void loginUser(String username, String password) {
        Log.i(TAG, "Logging In...");

        //Begin using Parse...
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                //means something is wrong
                if (e != null) {
                    //Log.e(TAG, "Error logging in...", e);
                    Toast.makeText(LoginActivity.this, "Incorrect Username/Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.i(TAG, "Log in was Successful");

                //If you made it here, it means we can launch the main timeline;
                reDirectMainActivity();

            }
        });

    }

    private void registerUser(String username, String password) {
        ParseUser user = new ParseUser();
        //user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    //TODO handle problem
                    Log.e(TAG, "Error Signing Up", e);
                    Toast.makeText(LoginActivity.this, "Username Already Taken", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Success!", Toast.LENGTH_LONG).show();

                    //saveProfilePic();

                    createProfile( user );
                    reDirectMainActivity();

                    //Intent i = new Intent(LoginActivity.this, Profile.class);
                    //startActivity(i);
                    //finish();
                }
            }
        });
    }

    private void reDirectMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);

        //Finishes the loginActivity and removed it from the activity stack;
        finish();
    }

    private void createProfile(ParseUser user) {
        UserProfile profile = new UserProfile();

        //handle user
        profile.setUser(user);
        profile.setChannels(new JSONArray());

        //Now save to backend
        profile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "error", e);
                    Toast.makeText(LoginActivity.this, "Error Creating Profile", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Profile Created", Toast.LENGTH_SHORT).show();
                    //etDescription.setText("");
                    //ivPostImage.setImageResource(0);
                    //reDirectMainActivity();
                }
            }
        });
    }

}