package com.example.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.models.Users;
import com.example.ecommerce.prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private EditText phoneNumberLoginET, passwordLoginET;
    private Button loginBTN;
    private ProgressDialog loadingbarLogin;
    private TextView adminLink, notAdminLink;

    private CheckBox checkBoxRememberMe;

    private String parentDBName = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        phoneNumberLoginET = findViewById(R.id.login_phoneNumber_ET_ID);
        passwordLoginET = findViewById(R.id.login_password_ET_ID);
        loginBTN = findViewById(R.id.login_BTN_ID);
        checkBoxRememberMe = findViewById(R.id.rememberMe_cBox_ID);
        adminLink = findViewById(R.id.admin_panel_TV_ID);
        notAdminLink = findViewById(R.id.not_admin_panel_TV_ID);

        Paper.init(this);

        loadingbarLogin = new ProgressDialog(this);

        loginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBTN.setText("Login Admin");
                adminLink.setVisibility(View.INVISIBLE);
                notAdminLink.setVisibility(View.VISIBLE);
                parentDBName = "Admins";
                checkBoxRememberMe.setVisibility(View.INVISIBLE);

            }
        });

        notAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBTN.setText("Login");
                adminLink.setVisibility(View.VISIBLE);
                notAdminLink.setVisibility(View.INVISIBLE);
                parentDBName = "Users";
                checkBoxRememberMe.setVisibility(View.VISIBLE);
            }
        });
    }

    private void loginUser() {
        String phoneNumberLogin = phoneNumberLoginET.getText().toString();
        String passwordLogin = passwordLoginET.getText().toString();

        if (TextUtils.isEmpty(phoneNumberLogin)) {
            Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(passwordLogin)) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
        } else {
            loadingbarLogin.setTitle("Create Account");
            loadingbarLogin.setMessage("Please wait, while we are checking the credentials");
            loadingbarLogin.setCanceledOnTouchOutside(false);
            loadingbarLogin.show();

            allowAcessToAccount(phoneNumberLogin, passwordLogin);
        }
    }

    private void allowAcessToAccount(final String phoneNumberLogin, final String passwordLogin) {

        if (checkBoxRememberMe.isChecked()) {
            Paper.book().write(Prevalent.UserPhoneKey, phoneNumberLogin);
            Paper.book().write(Prevalent.UserPasswordKey, passwordLogin);
        }


        final DatabaseReference ROOTREF;
        ROOTREF = FirebaseDatabase.getInstance().getReference();
        ROOTREF.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(parentDBName).child(phoneNumberLogin).exists()) {
                    Users usersData = dataSnapshot.child(parentDBName).child(phoneNumberLogin).getValue(Users.class);
                    Log.e("TAG", "onDataChange: " + usersData.getPhoneNumber());
                    if (usersData.getPhoneNumber().equals(phoneNumberLogin)) {

                        if (usersData.getPassword().equals(passwordLogin)) {


                            if (parentDBName.equals("Admins")) {
                                Toast.makeText(LoginActivity.this, "Welcome Admin, you are Logged in successfully", Toast.LENGTH_SHORT).show();
                                loadingbarLogin.dismiss();

                                startActivity(new Intent(LoginActivity.this, AdminCategoryActivity.class));
                            } else if (parentDBName.equals("Users")) {
                                Toast.makeText(LoginActivity.this, "Loged in successfully", Toast.LENGTH_SHORT).show();
                                loadingbarLogin.dismiss();

                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                Prevalent.currentOnlineUser = usersData;

                                startActivity(intent);
                            }

                        } else {
                            loadingbarLogin.dismiss();
                            Toast.makeText(LoginActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "This " + phoneNumberLogin + " number does not exits", Toast.LENGTH_SHORT).show();
                    loadingbarLogin.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(LoginActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
