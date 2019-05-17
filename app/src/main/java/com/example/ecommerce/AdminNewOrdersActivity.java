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

import com.example.ecommerce.models.AdminOrders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminNewOrdersActivity extends AppCompatActivity {

    private RecyclerView orderList;
    private DatabaseReference orderRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);

        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders");

        orderList = findViewById(R.id.ordersList_RVID);
        orderList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<AdminOrders>  options =
                new FirebaseRecyclerOptions.Builder<AdminOrders>().setQuery(orderRef, AdminOrders.class).build();

        FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, final int position, @NonNull final AdminOrders model) {

                        holder.userName.setText("Name: "+model.getName());
                        holder.userPhoneNumber.setText("Phone Number: "+model.getPhone());
                        holder.userShippingAddress.setText("Shipping Address: "+model.getAddress()+", "+model.getCity());
                        holder.userDateTime.setText("Date & Time: "+model.getDate()+" "+model.getTime());
                        holder.userTotalAmount.setText("Total Amount: $"+model.getTotalAmount());

                        holder.showOrders_BTN.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String uID = getRef(position).getKey();

                                Intent intent = new Intent(AdminNewOrdersActivity.this, AdminUserProductsActivity.class);
                                intent.putExtra("userID", uID);
                                startActivity(intent);
                            }
                        });

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CharSequence options[] = new CharSequence[]{
                                        "Yes" ,
                                        "No"
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrdersActivity.this);
                                builder.setTitle("Is these products shipped?");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {

                                        if(i == 0){
                                            String uID = getRef(position).getKey();

                                            removeOrder(uID);
                                        }else {
                                            finish();
                                        }
                                    }
                                });

                                builder.show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout, parent,false);

                        return new AdminOrdersViewHolder(view);
                    }
                };
        orderList.setAdapter(adapter);
        adapter.startListening();
    }


    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder{

        public TextView userName, userPhoneNumber, userTotalAmount, userDateTime, userShippingAddress;
        public Button showOrders_BTN;

        public AdminOrdersViewHolder(View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.order_userName_TVID);
            userPhoneNumber = itemView.findViewById(R.id.order_phoneNumber_TVID);
            userShippingAddress = itemView.findViewById(R.id.order_addressCity_TVID);
            userTotalAmount = itemView.findViewById(R.id.order_totalPrice_TVID);
            userDateTime = itemView.findViewById(R.id.order_dateTime_TVID);
            showOrders_BTN = itemView.findViewById(R.id.showAllProducts_BTNID);
        }
    }

    private void removeOrder(String uID) {
        orderRef.child(uID).removeValue();
    }
}
