package com.example.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {

    private Button createAccountBTN;
    private EditText usernameET, phoneNumberET, passwordET;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        createAccountBTN = findViewById(R.id.register_BTN_ID);
        usernameET =findViewById(R.id.register_username_ET_ID);
        phoneNumberET = findViewById(R.id.register_phoneNumber_ET_ID);
        passwordET = findViewById(R.id.register_password_ET_ID);

        loadingBar = new ProgressDialog(this);

        
        createAccountBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        String username = usernameET.getText().toString();
        String phoneNumber = phoneNumberET.getText().toString();
        String password = passwordET.getText().toString();

        if(TextUtils.isEmpty(username)){
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phoneNumber)){
            Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            validatePhoneNumber(username, phoneNumber, password);
        }

    }

    private void validatePhoneNumber(final String username, final String phoneNumber, final String password) {
        final DatabaseReference ROOTREF;
        ROOTREF = FirebaseDatabase.getInstance().getReference();
        ROOTREF.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.child("Users").child(phoneNumber).exists())){
                    HashMap<String, Object>userDataMap = new HashMap<>();
                    userDataMap.put("phoneNumber", phoneNumber);
                    userDataMap.put("password", password);
                    userDataMap.put("username", username);

                    ROOTREF.child("Users").child(phoneNumber).updateChildren(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(RegistrationActivity.this, "your account has been created", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                            }
                            else {
                                loadingBar.dismiss();
                                Toast.makeText(RegistrationActivity.this, "something is wrong, please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(RegistrationActivity.this, "This "+phoneNumber+" phone number is already exist.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegistrationActivity.this, "please try using another phone number.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
