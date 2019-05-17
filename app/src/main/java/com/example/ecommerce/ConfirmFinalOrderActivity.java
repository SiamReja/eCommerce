package com.example.ecommerce;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ecommerce.prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private EditText nameET, phoneET, addressET,cityET;
    private Button confirmBTN;

    private String totalAmount = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        totalAmount = getIntent().getStringExtra("Total Price");

        confirmBTN = findViewById(R.id.shipment_confirm_BTNID);
        nameET = findViewById(R.id.shipment_name_ETID);
        phoneET = findViewById(R.id.shipment_phoneNumber_ETID);
        addressET = findViewById(R.id.shipment_address_ETID);
        cityET = findViewById(R.id.shipment_city_ETID);

        confirmBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });
    }

    private void check() {
        if (TextUtils.isEmpty(nameET.getText().toString())){
            Toast.makeText(this, "Name is requared", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(phoneET.getText().toString())){
            Toast.makeText(this, "Phone Number is requared", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(addressET.getText().toString())){
            Toast.makeText(this, "Address is requared", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(cityET.getText().toString())){
            Toast.makeText(this, "City is requared", Toast.LENGTH_SHORT).show();
        }
        else {
            confirmOrder();
        }
    }

    private void confirmOrder() {
        final String saveCurrentTime, saveCurrentDate;

        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(callForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(callForDate.getTime());

        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhoneNumber());


        HashMap<String, Object> orderMap = new HashMap<>();

        orderMap.put("totalAmount", totalAmount);
        orderMap.put("name", nameET.getText().toString());
        orderMap.put("phone", phoneET.getText().toString());
        orderMap.put("city", cityET.getText().toString());
        orderMap.put("address", addressET.getText().toString());
        orderMap.put("date", saveCurrentDate);
        orderMap.put("time", saveCurrentTime);
        orderMap.put("status", "not shipped");

        orderRef.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View").child(Prevalent.currentOnlineUser.getPhoneNumber()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ConfirmFinalOrderActivity.this, "Final order placed Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ConfirmFinalOrderActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
            }
        });
     }
}
