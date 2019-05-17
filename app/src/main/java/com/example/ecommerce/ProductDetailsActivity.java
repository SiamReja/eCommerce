package com.example.ecommerce;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.ecommerce.models.Products;
import com.example.ecommerce.prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {

    private Button addToCartBTN;
    private ImageView productImage;
    private ElegantNumberButton numberBTN;
    private TextView productName, productDescription, productPrice;
    private String productID = "", status = "Normal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productID = getIntent().getStringExtra("productID");


        numberBTN = findViewById(R.id.productDetails_number_BTN_ID);
        addToCartBTN = findViewById(R.id.productDetails_addToCart_BTNID);
        productImage = findViewById(R.id.productDetails_IMG_ID);
        productName = findViewById(R.id.productDetails_name_TVID);
        productDescription = findViewById(R.id.productDetails_description_TVID);
        productPrice = findViewById(R.id.productDetails_price_TVID);

        getProductDetails(productID);

        addToCartBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (status.equals("Order Shipped") || status.equals("Order Placed")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProductDetailsActivity.this);
                    builder.setTitle("OK dibo eikhane");
                    builder.setCancelable(true);
                    builder.setMessage("You can purchase more products, once your order is shipped or confirmend");

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }else {
                    addingCartList();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        checkOrderStatus();
    }

    private void addingCartList() {

        String saveCurrentTime, saveCurrentDate;

        Calendar callforDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(callforDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(callforDate.getTime());

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("productID", productID);
        cartMap.put("productName", productName.getText().toString());
        cartMap.put("price", productPrice.getText().toString());
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("quantity", numberBTN.getNumber());
        cartMap.put("discount", "");

        cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhoneNumber()).child("Products").child(productID).updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    cartListRef.child("Admin View").child(Prevalent.currentOnlineUser.getPhoneNumber()).child("Products").child(productID).updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ProductDetailsActivity.this, "Added to cartlist", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ProductDetailsActivity.this, HomeActivity.class));
                            }
                        }
                    });
                }
            }
        });

    }

    private void getProductDetails(String productID) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");

        productRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Products products = dataSnapshot.getValue(Products.class);

                    productName.setText(products.getName());
                    productPrice.setText(products.getPrice());
                    productDescription.setText(products.getDescription());
                    Picasso.get().load(products.getImage()).into(productImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkOrderStatus() {
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhoneNumber());
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String shippmectStatus = dataSnapshot.child("status").getValue().toString();

                    if (shippmectStatus.equals("shipped")) {
                        status = "Order Shipped";

                    } else if (shippmectStatus.equals("not shipped")) {
                        status = "Order Placed";
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
