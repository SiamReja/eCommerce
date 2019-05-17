package com.example.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class AdminAddNewProductActivity extends AppCompatActivity {

    private String category_name,productName, productDescription, productPrice,
            saveCurrentDate, saveCurrentTime, productRandomKey, downloadImageUrl;

    private Button add_new_product_BTN;
    private EditText productName_ET, product_description_ET, product_price_ET;
    private ImageView select_product_IMG;
    private static final int galleryPick = 1;
    private Uri imageUri;
    private StorageReference productImageRef;
    private DatabaseReference productRef;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        category_name = getIntent().getExtras().get("category").toString();
        productImageRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        productRef = FirebaseDatabase.getInstance().getReference().child("Products");

        add_new_product_BTN = findViewById(R.id.add_new_product_BTN_ID);
        productName_ET = findViewById(R.id.product_name_ET_ID);
        product_description_ET = findViewById(R.id.product_description_ET_ID);
        product_price_ET = findViewById(R.id.product_price_ET_ID);
        select_product_IMG = findViewById(R.id.select_product_IMG_ID);

        loadingbar = new ProgressDialog(this);

        select_product_IMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        
        add_new_product_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateProductData();
            }
        });

    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, galleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== galleryPick && resultCode==RESULT_OK && data!=null){
            imageUri = data.getData();
            select_product_IMG.setImageURI(imageUri);
        }
    }

    private void validateProductData() {
        productDescription = product_description_ET.getText().toString();
        productName = productName_ET.getText().toString();
        productPrice = product_price_ET.getText().toString();
        
        if (imageUri == null){
            Toast.makeText(this, "Product Image is required", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(productName)){
            Toast.makeText(this, "product name is required", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(productDescription)){
            Toast.makeText(this, "product description is required", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(productPrice)){
            Toast.makeText(this, "product price is required", Toast.LENGTH_SHORT).show();
        }else {
            storeProductInfo();
        }
    }

    private void storeProductInfo() {

        loadingbar.setTitle("Adding New Product");
        loadingbar.setMessage("Please wait, while we are adding new product");
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;

        final StorageReference filePath = productImageRef.child(imageUri.getLastPathSegment()+productRandomKey+".jpg");

        final UploadTask uploadTask = filePath.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AdminAddNewProductActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                loadingbar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddNewProductActivity.this, "Product image Uploaded Successfully", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()){
                            throw task.getException();
                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();

                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            downloadImageUrl = task.getResult().toString();

                            Toast.makeText(AdminAddNewProductActivity.this, "Got product image URL successfully", Toast.LENGTH_SHORT).show();

                            saveProductInfoToDatabase();
                        }
                    }
                });
            }
        });
    }

    private void saveProductInfoToDatabase(){
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("productID", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("category", category_name);
        productMap.put("image", downloadImageUrl);
        productMap.put("description", productDescription);
        productMap.put("name", productName);
        productMap.put("price", productPrice);

        productRef.child(productRandomKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    loadingbar.dismiss();
                    Toast.makeText(AdminAddNewProductActivity.this, "Product is added successfully", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(AdminAddNewProductActivity.this, AdminCategoryActivity.class));
                }
                else{
                    loadingbar.dismiss();
                    String message = task.getException().toString();
                    Toast.makeText(AdminAddNewProductActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
