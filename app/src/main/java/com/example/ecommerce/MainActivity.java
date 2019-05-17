package com.example.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ecommerce.models.Users;
import com.example.ecommerce.prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    Button joinBTN, loginBTN;
    private String parentDBName = "Users";
    private ProgressDialog loadingBarMainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joinBTN = findViewById(R.id.main_join_now_BTN_ID);
        loginBTN =findViewById(R.id.main_login_BTN_ID);
        loadingBarMainActivity = new ProgressDialog(this);

        Paper.init(this);

        joinBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
            }
        });

        loginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        String userPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String userPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);

        if (userPhoneKey != "" && userPasswordKey != ""){
            if (!TextUtils.isEmpty(userPhoneKey) && !TextUtils.isEmpty(userPasswordKey)){
                allowAccess(userPhoneKey, userPasswordKey);

                loadingBarMainActivity.setTitle("Already logged in");
                loadingBarMainActivity.setMessage("Please wait...");
                loadingBarMainActivity.setCanceledOnTouchOutside(false);
                loadingBarMainActivity.show();
            }
        }

    }

    private void allowAccess(final String phoneNumberLogin, final String passwordLogin) {
        final DatabaseReference ROOTREF;
        ROOTREF = FirebaseDatabase.getInstance().getReference();
        ROOTREF.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(parentDBName).child(phoneNumberLogin).exists()){
                    Users usersData = dataSnapshot.child(parentDBName).child(phoneNumberLogin).getValue(Users.class);

                    if(usersData.getPhoneNumber().equals(phoneNumberLogin)){

                        if(usersData.getPassword().equals(passwordLogin)){
                            Toast.makeText(MainActivity.this, "you are already logged in", Toast.LENGTH_SHORT).show();
                            loadingBarMainActivity.dismiss();

                            Prevalent.currentOnlineUser = usersData;//updated
                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        }else {
                            loadingBarMainActivity.dismiss();
                            Toast.makeText(MainActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "This "+phoneNumberLogin+" number does not exits", Toast.LENGTH_SHORT).show();
                    loadingBarMainActivity.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
