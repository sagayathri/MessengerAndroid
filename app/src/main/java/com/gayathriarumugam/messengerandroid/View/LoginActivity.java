package com.gayathriarumugam.messengerandroid.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gayathriarumugam.messengerandroid.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.content.SharedPreferences;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private String TAG = "LoginActivity";
    private EditText nameEditText;
    private Button btnGO;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameEditText = findViewById(R.id.nameEditText);
        btnGO = findViewById(R.id.btnGO);

        //Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        btnGO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Checks if name is empty
                if(nameEditText.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter a name", Toast.LENGTH_SHORT).show();
                } else {
                    AuthenticateUser();
                    nameEditText.setInputType(0);
                }
            }
        });
    }

    private void AuthenticateUser() {

        //Stores username to SharedPreferences
        SharedPreferences.Editor editor  = getSharedPreferences(String.valueOf(R.string.PREFS_NAME), MODE_PRIVATE).edit();
        editor.putString("name", nameEditText.getText().toString());
        editor.apply();

        //Signs in as Anonymous user in Firebase
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(LoginActivity.this, TopicsActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Unable to create an user",Toast.LENGTH_SHORT).show();
                        }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            //By-passes login screen
            startActivity(new Intent(this, TopicsActivity.class));
        }
    }
}
