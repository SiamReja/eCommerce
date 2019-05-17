package com.example.ecommerce.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.ecommerce.Interfaces.ItemClickListener;
import com.example.ecommerce.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView productName_CartTV, productPrice_CartTV, productQuantity_CartTV;
    private ItemClickListener itemClickListener;

    public CartViewHolder(View itemView) {
        super(itemView);

        productName_CartTV = itemView.findViewById(R.id.order_userName_TVID);
        productPrice_CartTV = itemView.findViewById(R.id.order_totalPrice_TVID);
        productQuantity_CartTV = itemView.findViewById(R.id.order_phoneNumber_TVID);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
