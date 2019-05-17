package com.example.ecommerce.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ecommerce.Interfaces.ItemClickListener;
import com.example.ecommerce.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView productNameTV, productDescriptionTV, productPriceTV;
    public ImageView productImageIMG;
    public ItemClickListener listener;

    public ProductViewHolder(View itemView) {
        super(itemView);

        productImageIMG = itemView.findViewById(R.id.productImageIMGID);
        productNameTV = itemView.findViewById(R.id.productNameTVID);
        productDescriptionTV = itemView.findViewById(R.id.productDescriptionTVID);
        productPriceTV = itemView.findViewById(R.id.productPriceTVID);
    }

    public void setItemClickListner(ItemClickListener listner){
        this.listener = listner;
    }

    @Override
    public void onClick(View v) {

        listener.onClick(v, getAdapterPosition(), false);

    }
}
