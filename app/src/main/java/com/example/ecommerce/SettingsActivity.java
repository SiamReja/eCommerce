package com.example.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private EditText set_NameET, set_PhoneNumberForOrderET, set_AddressET;
    private TextView set_profileImage_change_TV_BTN, set_CloseTV_BTN, set_UpdateTV_BTN;

    private Uri imageUri;
    private String myUrl = "";
    private StorageReference storageProfilePictureReference;
    private String checker = "";

    private StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        storageProfilePictureReference = FirebaseStorage.getInstance().getReference().child("Profile Picture");


        profileImageView = findViewById(R.id.setting_profile_IMG_ID);
        set_NameET = findViewById(R.id.setting_Name_ET_ID);
        set_PhoneNumberForOrderET = findViewById(R.id.setting_phoneNumber_ET_ID);
        set_CloseTV_BTN = findViewById(R.id.setting_close_BTN_ID);
        set_profileImage_change_TV_BTN = findViewById(R.id.setting_profileImage_change_TV_ID);
        set_UpdateTV_BTN = findViewById(R.id.setting_update_account_BTN_ID);
        set_AddressET = findViewById(R.id.setting_address_ET_ID);

        userInfoDisplay(profileImageView, set_NameET, set_PhoneNumberForOrderET, set_AddressET);

        set_CloseTV_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        set_UpdateTV_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checker.equals("clicked")){
                    userInfoSaved();
                }else {
                    updateOnlyUserInfo();
                }
            }
        });
        set_profileImage_change_TV_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker = "clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data!=null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            profileImageView.setImageURI(imageUri);
        }
        else {
            Toast.makeText(this, "Something is WRONG, Try Again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
            finish();
        }
    }

    private void userInfoSaved() {
        if(TextUtils.isEmpty(set_NameET.getText().toString())){
            Toast.makeText(this, "Name is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(set_AddressET.getText().toString())){
            Toast.makeText(this, "Address is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(set_PhoneNumberForOrderET.getText().toString())){
            Toast.makeText(this, "Phone number is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if (checker.equals("clicked") ){
            uploadImage();
        }
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating profile");
        progressDialog.setMessage("Please Wait while we are updating your account information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null){
            final StorageReference fileref= storageProfilePictureReference
                    .child(Prevalent.currentOnlineUser.getPhoneNumber()+".jpg");

            uploadTask = fileref.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if (!task.isSuccessful()){
                        throw task.getException();
                    }

                    return fileref.getDownloadUrl();
                }
            })
            .addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        myUrl = downloadUri.toString();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("username", set_NameET.getText().toString());
                        userMap.put("address", set_AddressET.getText().toString());
                        userMap.put("phoneNumberForOrder", set_PhoneNumberForOrderET.getText().toString());
                        userMap.put("image", myUrl);
                        ref.child(Prevalent.currentOnlineUser.getPhoneNumber()).updateChildren(userMap);

                        progressDialog.dismiss();

                        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                        Toast.makeText(SettingsActivity.this, "Profile information updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(SettingsActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            Toast.makeText(this, "Image is not selected", Toast.LENGTH_SHORT).show();
        }

    }

    private void updateOnlyUserInfo() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("username", set_NameET.getText().toString());
        userMap.put("address", set_AddressET.getText().toString());
        userMap.put("phoneNumberForOrder", set_PhoneNumberForOrderET.getText().toString());
        ref.child(Prevalent.currentOnlineUser.getPhoneNumber()).updateChildren(userMap);


        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
        Toast.makeText(SettingsActivity.this, "Profile information updated successfully", Toast.LENGTH_SHORT).show();
        finish();

    }

    private void userInfoDisplay(final CircleImageView profileImageView, final EditText set_nameET, final EditText set_PhoneNumberForOrderET, final EditText set_addressET) {

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhoneNumber());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.child("image").exists()){
                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("username").getValue().toString();
                        String phoneNumber = dataSnapshot.child("phoneNumberForOrder").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(profileImageView);
                        set_nameET.setText(name);
                        set_PhoneNumberForOrderET.setText(phoneNumber);
                        set_addressET.setText(address);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
