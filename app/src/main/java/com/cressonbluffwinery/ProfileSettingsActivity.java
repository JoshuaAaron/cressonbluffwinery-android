package com.cressonbluffwinery;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cressonbluffwinery.Prevalent.Prevalent;
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

//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
//import com.example.codingcafe.ecommerce.Prevalent.Prevalent;

public class ProfileSettingsActivity extends AppCompatActivity
{
    private CircleImageView profileImageView;
    private EditText fNameEditText, lNameEditText, uNameEditText, userPhoneEditText,
            addressEditText, cityEditText, stateEditText, zipEditText;

    private TextView profileChangeTextBtn,  closeTextBtn, saveTextButton;

    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePrictureRef;
    private String checker = "";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        storageProfilePrictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");
        permissions();
        profileImageView = (CircleImageView) findViewById(R.id.settings_profile_image);
        fNameEditText = (EditText) findViewById(R.id.settings_first_name);
        lNameEditText = (EditText) findViewById(R.id.settings_last_name);
        uNameEditText = (EditText) findViewById(R.id.settings_username);
        userPhoneEditText = (EditText) findViewById(R.id.settings_phone_number);
        addressEditText = (EditText) findViewById(R.id.settings_address);
        cityEditText = (EditText) findViewById(R.id.settings_city);
        stateEditText = (EditText) findViewById(R.id.settings_state);
        zipEditText = (EditText) findViewById(R.id.settings_zip);

        profileChangeTextBtn = (TextView) findViewById(R.id.profile_image_change_btn);
        closeTextBtn = (TextView) findViewById(R.id.close_settings_btn);
        saveTextButton = (TextView) findViewById(R.id.update_account_settings_btn);


        userInfoDisplay(profileImageView, fNameEditText, lNameEditText, uNameEditText, userPhoneEditText,
                addressEditText, cityEditText, stateEditText, zipEditText);


        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });


        saveTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (checker.equals("clicked"))
                {
                    userInfoSaved();
                }
                else
                {
                    updateOnlyUserInfo();
                }
            }
        });


        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                checker = "clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)
                        .start(ProfileSettingsActivity.this);
            }
        });
    }



    private void updateOnlyUserInfo()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap. put("firstName", fNameEditText.getText().toString());
        userMap. put("lastName", lNameEditText.getText().toString());
        userMap. put("userName", uNameEditText.getText().toString());
        userMap. put("address", addressEditText.getText().toString());
        userMap. put("city", cityEditText.getText().toString());
        userMap. put("state", stateEditText.getText().toString());
        userMap. put("zipCode", zipEditText.getText().toString());
        userMap. put("phone", userPhoneEditText.getText().toString());
        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

        startActivity(new Intent(ProfileSettingsActivity.this, HomeActivity.class));
        Toast.makeText(ProfileSettingsActivity.this, "Profile Info update successfully.", Toast.LENGTH_SHORT).show();
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            profileImageView.setImageURI(imageUri);
        }
        else
        {
            Toast.makeText(this, "Error, Try Again.", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(ProfileSettingsActivity.this, ProfileSettingsActivity.class));
            finish();
        }
    }




    private void userInfoSaved()
    {
        if (TextUtils.isEmpty(uNameEditText.getText().toString()))
        {
            Toast.makeText(this, "Username is mandatory.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(fNameEditText.getText().toString()))
        {
            Toast.makeText(this, "First Name is empty.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(lNameEditText.getText().toString()))
        {
            Toast.makeText(this, "Last Name is empty.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(addressEditText.getText().toString()))
        {
            Toast.makeText(this, "Address is empty.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(cityEditText.getText().toString()))
        {
            Toast.makeText(this, "City is empty.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(stateEditText.getText().toString()))
        {
            Toast.makeText(this, "State is empty.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(zipEditText.getText().toString()))
        {
            Toast.makeText(this, "Zip Code is empty.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(userPhoneEditText.getText().toString()))
        {
            Toast.makeText(this, "Phone Number is empty.", Toast.LENGTH_SHORT).show();
        }
        else if(checker.equals("clicked"))
        {
            uploadImage();
        }
    }



    private void uploadImage()
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait, while we are updating your account information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null)
        {
            final StorageReference fileRef = storageProfilePrictureRef
                    .child(Prevalent.currentOnlineUser.getPhone() + ".jpg");

            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception
                {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task)
                        {
                            if (task.isSuccessful())
                            {
                                Uri downloadUrl = task.getResult();
                                myUrl = downloadUrl.toString();

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

                                HashMap<String, Object> userMap = new HashMap<>();
                                userMap. put("name", uNameEditText.getText().toString());
                                userMap. put("firstName", fNameEditText.getText().toString());
                                userMap. put("lastName", lNameEditText.getText().toString());
                                userMap. put("address", addressEditText.getText().toString());
                                userMap. put("city", cityEditText.getText().toString());
                                userMap. put("state", stateEditText.getText().toString());
                                userMap. put("zipCode", zipEditText.getText().toString());
                                userMap. put("phone", userPhoneEditText.getText().toString());
                                userMap. put("image", myUrl);
                                ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

                                progressDialog.dismiss();

                                startActivity(new Intent(ProfileSettingsActivity.this, HomeActivity.class));
                                Toast.makeText(ProfileSettingsActivity.this, "Profile Info update successfully.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(ProfileSettingsActivity.this, "Error.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else
        {
            Toast.makeText(this, "image is not selected.", Toast.LENGTH_SHORT).show();
        }
    }


    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fNameEditText, final EditText lNameEditText, final EditText uNameEditText,
                                 final EditText userPhoneEditText, final EditText addressEditText,
                                 final EditText cityEditText, final EditText stateEditText, final EditText zipEditText)
    {
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    if (dataSnapshot.child("image").exists())
                    {
                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String firstName = dataSnapshot.child("firstName").getValue().toString();
                        String lastName = dataSnapshot.child("lastName").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();
                        String city = dataSnapshot.child("city").getValue().toString();
                        String state = dataSnapshot.child("state").getValue().toString();
                        String zipCode = dataSnapshot.child("zipCode").getValue().toString();
                        Picasso.get().load(image).into(profileImageView);
                        uNameEditText.setText(name);
                        fNameEditText.setText(firstName);
                        lNameEditText.setText(lastName);
                        userPhoneEditText.setText(phone);
                        addressEditText.setText(address);
                        cityEditText.setText(city);
                        stateEditText.setText(state);
                        zipEditText.setText(zipCode);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1){
            if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();

            }
            else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();

            }

        }
    }
    public void permissions(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            Toast.makeText(ProfileSettingsActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();

        }
        else {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){

                new AlertDialog.Builder(this)
                        .setTitle("Permission needed")
                        .setMessage("This permission is required for the app to function perfectly")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(ProfileSettingsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
            }
            else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
        }

    }
}
