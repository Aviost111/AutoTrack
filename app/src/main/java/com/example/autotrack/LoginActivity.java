package com.example.autotrack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private TextView signUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = findViewById(R.id.loginButton);
        signUp = findViewById(R.id.signUp);
        loginButton.setOnClickListener(new View.OnClickListener() {
            //log into app
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler(); // Create a Handler for delayed actions

                underlineText(signUp); // Apply underline animation
                underlineText(signUp);

                // Start RegisterActivity after animation
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                        startActivity(intent);
                    }
                }, 200); // Adjust delay as needed
            }
        });
    }
    private void

    underlineText(TextView textView)

    {
        String text = textView.getText().toString();
        SpannableString spannableString = new SpannableString(text);
        UnderlineSpan underlineSpan = new UnderlineSpan();
        spannableString.setSpan(underlineSpan, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);

        // Add a slight delay before removing underline
        textView.postDelayed(new Runnable() {
            @Override
            public void run() {
                spannableString.removeSpan(underlineSpan);
                textView.setText(spannableString);
            }
        }, 100); // Adjust delay as needed
    }

}