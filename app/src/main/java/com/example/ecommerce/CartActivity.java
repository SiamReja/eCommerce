package com.example.ecommerce;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.ViewHolder.CartViewHolder;
import com.example.ecommerce.models.Cart;
import com.example.ecommerce.prevalent.Prevalent;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button nextProcess_BTN;
    private TextView totalPrice_TV, msg1_TV;

    private int overallTotalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cartList_RVID);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        nextProcess_BTN = findViewById(R.id.nextProcess_BTNID);
        totalPrice_TV = findViewById(R.id.totalPrice_TVID);
        msg1_TV = findViewById(R.id.msg1_TVID);

        nextProcess_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
                intent.putExtra("Total Price", String.valueOf(overallTotalPrice));
                startActivity(intent);
                finish();
            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();

        checkOrderStatus();

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhoneNumber()).child("Products"), Cart.class)
                .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {

                holder.productQuantity_CartTV.setText("Quantity = " + model.getQuantity());
                holder.productName_CartTV.setText(model.getProductName());
                holder.productPrice_CartTV.setText("Price = " + model.getPrice());

                int perProductTotalPrice = ((Integer.valueOf(model.getPrice()))) * Integer.valueOf(model.getQuantity());
                overallTotalPrice = overallTotalPrice + perProductTotalPrice;
                totalPrice_TV.setText("Total price = " + overallTotalPrice+"$");

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[] = new CharSequence[]{

                                "Edit", "Remove"
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Options");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    Intent intent = new Intent(CartActivity.this, ProductDetailsActivity.class);
                                    intent.putExtra("productID", model.getProductID());
                                    startActivity(intent);
                                }
                                if (which == 1) {
                                    cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhoneNumber()).child("Products")
                                            .child(model.getProductID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(CartActivity.this, "Item removed successfully", Toast.LENGTH_SHORT).show();
//                                                Intent intent = new Intent(CartActivity.this, HomeActivity.class);
//                                                startActivity(intent);

                                                overallTotalPrice = 0;

//                                                int perProductTotalPrice = ((Integer.valueOf(model.getPrice()))) * Integer.valueOf(model.getQuantity());
//                                                overallTotalPrice = overallTotalPrice+perProductTotalPrice;
//                                                totalPrice_TV.setText("Total price = "+overallTotalPrice);
                                            }
                                        }
                                    });
                                }
                            }
                        });

                        builder.show();
                    }
                });

            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    private void checkOrderStatus() {
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhoneNumber());
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String shippmectStatus = dataSnapshot.child("status").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();

                    if (shippmectStatus.equals("shipped")) {

                        totalPrice_TV.setText("Dear ," + userName + "/n your order is shipped successfully. Soon you will receive your order ar your door step");
                        recyclerView.setVisibility(View.GONE);
                        msg1_TV.setVisibility(View.VISIBLE);
                        msg1_TV.setText("Congratulation, your last order has been shipped");
                        nextProcess_BTN.setVisibility(View.GONE);
                    } else if (shippmectStatus.equals("not shipped")) {
                        totalPrice_TV.setText("Shipping status  = not Shipped");
                        recyclerView.setVisibility(View.GONE);
                        msg1_TV.setVisibility(View.VISIBLE);
                        nextProcess_BTN.setVisibility(View.GONE);


                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        overallTotalPrice = 0;
    }
}
